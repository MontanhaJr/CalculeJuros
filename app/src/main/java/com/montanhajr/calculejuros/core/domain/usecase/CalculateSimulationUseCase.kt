package com.montanhajr.calculejuros.core.domain.usecase

import com.montanhajr.calculejuros.core.domain.model.*
import javax.inject.Inject
import kotlin.math.pow

class CalculateSimulationUseCase @Inject constructor() {

    operator fun invoke(input: SimulationInput): SimulationResult {
        // Validation and Sanitization
        val productPrice = maxOf(0.0, input.productPrice)
        val installmentsCount = maxOf(1, input.installmentsCount)

        // Step 1: Resolve Cash Price
        val (valorAVista, percentualDesconto) = if (input.useDiscountToggle) {
            val cleanPerc = input.discountPercentage.coerceIn(0.0, 100.0)
            val price = productPrice * (1 - cleanPerc / 100.0)
            price to cleanPerc
        } else {
            val cleanCashPrice = input.cashPrice.coerceIn(0.0, productPrice)
            val perc = if (productPrice > 0) {
                (productPrice - cleanCashPrice) / productPrice * 100.0
            } else 0.0
            cleanCashPrice to perc
        }
        val valorDesconto = productPrice - valorAVista

        // Step 2: Resolve Installment, Total Value, and Card Rate
        val (valorParcela, valorTotalParcelado, taxaCartaoMensal) = if (input.useMonthlyRateToggle) {
            val i = maxOf(0.0, input.monthlyCardRate) / 100.0
            val installment = if (i == 0.0) {
                productPrice / installmentsCount
            } else {
                productPrice * i / (1 - (1 + i).pow(-installmentsCount))
            }
            val total = installment * installmentsCount
            Triple(installment, total, input.monthlyCardRate)
        } else {
            val cleanTotal = maxOf(productPrice, input.totalInstallmentValue)
            val installment = cleanTotal / installmentsCount
            val implicitRate = calculateImplicitRate(productPrice, installment, installmentsCount)
            Triple(installment, cleanTotal, implicitRate * 100.0)
        }
        val taxaCartaoAnual = (1 + taxaCartaoMensal / 100.0).pow(12) - 1
        val jurosTotaisFinanciamento = maxOf(0.0, valorTotalParcelado - productPrice)

        // Step 3: Resolve Profitability
        val (rentabilidadeMensal, rentabilidadeAnual) = if (input.useAnnualProfitabilityToggle) {
            val anual = maxOf(0.0, input.annualProfitability) / 100.0
            val mensal = (1 + anual).pow(1.0 / 12.0) - 1
            mensal to anual
        } else {
            val mensal = maxOf(0.0, input.monthlyProfitability) / 100.0
            val anual = (1 + mensal).pow(12) - 1
            mensal to anual
        }

        // Step 4: Net Gain for each strategy (calculated via loop for consistency with table)
        val im = rentabilidadeMensal
        val n = installmentsCount

        val monthlyDetails = mutableListOf<MonthlyDetail>()
        
        // Down Payment consideration
        val downPayment = if (input.useDownPayment) {
            if (input.downPaymentIsPercentage) {
                productPrice * (input.downPaymentPercentage / 100.0)
            } else {
                input.downPayment
            }
        } else 0.0

        // Step 4.1: Prepayment setup (Nubank Model: Prepayment of the LAST k installments)
        val prepaidInstallmentsCount = if (input.usePrepaymentDiscount) {
            input.prepaidInstallmentsCount.coerceIn(0, maxOf(0, installmentsCount - 1))
        } else 0
        
        // Quittance Month: When the user decides to pay off the remaining balance.
        // If they prepay k installments, it means they are at month (n - k) 
        // and pay the current installment (n - k) plus all remaining k installments.
        val quittanceMonth = if (prepaidInstallmentsCount > 0) installmentsCount - prepaidInstallmentsCount else -1
        var totalPrepaymentDiscountValue = 0.0
        
        // Initial balances: we assume the user has the 'productPrice' available.
        var balanceAVista = productPrice - valorAVista
        var balanceParceladoPadrao = productPrice - downPayment
        var balanceParceladoComAntecipacao = productPrice - downPayment

        // Month 0
        monthlyDetails.add(
            MonthlyDetail(
                month = 0,
                installmentBalance = balanceParceladoPadrao,
                cashBalance = balanceAVista,
                installmentPaid = downPayment,
                yieldInstallment = 0.0,
                yieldCash = 0.0,
                prepaymentInstallmentBalance = if (input.usePrepaymentDiscount) balanceParceladoComAntecipacao else null
            )
        )

        for (month in 1..n) {
            // 1. Standard Installment Strategy
            val yieldPadrao = maxOf(0.0, balanceParceladoPadrao * im)
            balanceParceladoPadrao = balanceParceladoPadrao + yieldPadrao - valorParcela

            // 2. Prepayment Installment Strategy
            val yieldAntecipado = maxOf(0.0, balanceParceladoComAntecipacao * im)
            
            val installmentPaidPrepayment = when {
                !input.usePrepaymentDiscount -> valorParcela
                month < quittanceMonth -> valorParcela // Same as standard until quittance
                month == quittanceMonth -> {
                    // Current month installment + prepayment of all remaining k installments
                    if (input.prepaymentDiscountIsPercentage) {
                        val annualRate = input.prepaymentDiscountPercentage / 100.0
                        val monthlyRate = (1 + annualRate).pow(1.0 / 12.0) - 1
                        
                        var sumVPPrepaid = 0.0
                        // Prepay k installments that would be due in months (quittanceMonth + 1) to n.
                        // Distance for installment j is (j - quittanceMonth)
                        for (kIdx in 1..prepaidInstallmentsCount) {
                            val distance = kIdx.toDouble()
                            val vp = valorParcela / (1 + monthlyRate).pow(distance)
                            sumVPPrepaid += vp
                            totalPrepaymentDiscountValue += (valorParcela - vp)
                        }
                        valorParcela + sumVPPrepaid
                    } else {
                        // Fixed value discount applied to the sum of prepaid installments
                        val totalOriginalPrepaid = valorParcela * prepaidInstallmentsCount
                        totalPrepaymentDiscountValue = input.prepaymentDiscountValue
                        valorParcela + maxOf(0.0, totalOriginalPrepaid - totalPrepaymentDiscountValue)
                    }
                }
                month > quittanceMonth && quittanceMonth != -1 -> 0.0 // Already paid
                else -> valorParcela // Should not happen if k > 0, but safe fallback
            }
            
            balanceParceladoComAntecipacao = balanceParceladoComAntecipacao + yieldAntecipado - installmentPaidPrepayment

            // 3. Cash Strategy
            val yieldAVista = maxOf(0.0, balanceAVista * im)
            balanceAVista = balanceAVista + yieldAVista
            
            monthlyDetails.add(
                MonthlyDetail(
                    month = month,
                    installmentBalance = balanceParceladoPadrao,
                    cashBalance = balanceAVista,
                    installmentPaid = if (input.usePrepaymentDiscount) installmentPaidPrepayment else valorParcela,
                    yieldInstallment = yieldPadrao,
                    yieldCash = yieldAVista,
                    prepaymentInstallmentBalance = if (input.usePrepaymentDiscount) balanceParceladoComAntecipacao else null
                )
            )
        }

        val ganhoLiquidoAVista = balanceAVista
        val ganhoLiquidoParceladoPadrao = balanceParceladoPadrao
        val ganhoLiquidoParceladoComAntecipacao = balanceParceladoComAntecipacao
        
        val jurosGanhosAVista = maxOf(0.0, ganhoLiquidoAVista - (productPrice - valorAVista))

        // Step 5: Final comparison and recommendation (Comparing 3 strategies)
        // If prepayment is active, we compare Cash vs (Better of Standard vs Prepaid)
        val melhorParcelado = if (input.usePrepaymentDiscount) {
            maxOf(ganhoLiquidoParceladoPadrao, ganhoLiquidoParceladoComAntecipacao)
        } else {
            ganhoLiquidoParceladoPadrao
        }

        val diferenca = melhorParcelado - ganhoLiquidoAVista
        
        val recomendacao = when {
            kotlin.math.abs(diferenca) < 0.005 -> RecommendationType.EMPATE
            diferenca > 0 -> {
                if (input.usePrepaymentDiscount && ganhoLiquidoParceladoComAntecipacao > ganhoLiquidoParceladoPadrao) {
                    RecommendationType.PARCELADO
                } else {
                    RecommendationType.PARCELADO
                }
            }
            else -> RecommendationType.A_VISTA
        }

        return SimulationResult(
            productPrice = productPrice,
            cashPrice = valorAVista,
            discountValue = valorDesconto,
            discountPercentage = percentualDesconto,
            installmentsCount = n,
            installmentValue = valorParcela,
            totalInstallmentValue = valorTotalParcelado,
            monthlyCardRate = taxaCartaoMensal,
            annualCardRate = taxaCartaoAnual * 100.0,
            totalFinancingInterest = jurosTotaisFinanciamento,
            monthlyProfitability = rentabilidadeMensal * 100.0,
            annualProfitability = rentabilidadeAnual * 100.0,
            netGainCash = maxOf(0.0, ganhoLiquidoAVista),
            interestGainedCash = jurosGanhosAVista,
            netGainInstallment = melhorParcelado,
            netGainStandardInstallment = ganhoLiquidoParceladoPadrao,
            netGainPrepaidInstallment = ganhoLiquidoParceladoComAntecipacao,
            difference = kotlin.math.abs(diferenca),
            recommendation = recomendacao,
            downPayment = downPayment,
            prepaymentDiscountValue = totalPrepaymentDiscountValue,
            prepaidInstallmentsCount = prepaidInstallmentsCount,
            monthlyDetails = monthlyDetails
        )
    }

    private fun calculateImplicitRate(principal: Double, installment: Double, n: Int): Double {
        if (installment * n <= principal) return 0.0

        var low = 0.0
        var high = 1.0

        fun principalParaTaxa(i: Double): Double {
            return if (i == 0.0) installment * n
            else installment * (1 - (1 + i).pow(-n)) / i
        }

        var iterations = 0
        while (principalParaTaxa(high) > principal && iterations < 100) {
            high *= 2.0
            iterations++
        }

        repeat(200) {
            val mid = (low + high) / 2.0
            if (principalParaTaxa(mid) > principal) low = mid
            else high = mid
        }

        return (low + high) / 2.0
    }
}
