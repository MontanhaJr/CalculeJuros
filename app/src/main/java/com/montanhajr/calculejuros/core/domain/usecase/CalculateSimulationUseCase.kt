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
        
        // Initial balances: we assume the user has the 'productPrice' available.
        // If they pay cash: they pay valorAVista now.
        // If they parcel: they pay downPayment now.
        var balanceAVista = productPrice - valorAVista
        var balanceParcelado = productPrice - downPayment

        // Month 0 (Initial State)
        monthlyDetails.add(
            MonthlyDetail(
                month = 0,
                installmentBalance = balanceParcelado,
                cashBalance = balanceAVista,
                installmentPaid = downPayment,
                yieldInstallment = 0.0,
                yieldCash = 0.0
            )
        )

        for (month in 1..n) {
            // Installment Strategy Evolution
            val yieldParcelado = maxOf(0.0, balanceParcelado * im)
            balanceParcelado = balanceParcelado + yieldParcelado - valorParcela

            // Cash Strategy Evolution
            val yieldAVista = maxOf(0.0, balanceAVista * im)
            balanceAVista = balanceAVista + yieldAVista
            
            monthlyDetails.add(
                MonthlyDetail(
                    month = month,
                    installmentBalance = balanceParcelado,
                    cashBalance = balanceAVista,
                    installmentPaid = valorParcela,
                    yieldInstallment = yieldParcelado,
                    yieldCash = yieldAVista
                )
            )
        }

        // Step 4.1: Prepayment Discount calculation (just as info or comparison)
        val valorTotalRestante = valorTotalParcelado // This is the sum of installments after down payment
        val prepaymentDiscountValue = if (input.usePrepaymentDiscount) {
            if (input.prepaymentDiscountIsPercentage) {
                valorTotalRestante * (input.prepaymentDiscountPercentage / 100.0)
            } else {
                input.prepaymentDiscountValue
            }
        } else 0.0

        val ganhoLiquidoAVista = balanceAVista
        val ganhoLiquidoParcelado = balanceParcelado
        val jurosGanhosAVista = maxOf(0.0, ganhoLiquidoAVista - (productPrice - valorAVista))

        // Step 5: Final comparison and recommendation
        val diferenca = ganhoLiquidoParcelado - ganhoLiquidoAVista
        val recomendacao = when {
            kotlin.math.abs(diferenca) < 0.005 -> RecommendationType.EMPATE
            diferenca > 0 -> RecommendationType.PARCELADO
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
            netGainInstallment = ganhoLiquidoParcelado,
            difference = kotlin.math.abs(diferenca),
            recommendation = recomendacao,
            downPayment = downPayment,
            prepaymentDiscountValue = prepaymentDiscountValue,
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
