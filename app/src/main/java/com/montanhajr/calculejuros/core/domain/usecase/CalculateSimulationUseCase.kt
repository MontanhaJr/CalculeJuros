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

        // Step 4: Net Gain for each strategy
        val im = rentabilidadeMensal
        val n = installmentsCount

        // Cash Strategy
        val ganhoLiquidoAVista = valorDesconto * (1 + im).pow(n)
        val jurosGanhosAVista = ganhoLiquidoAVista - valorDesconto

        // Installment Strategy
        val valorFuturoProduto = productPrice * (1 + im).pow(n)
        val valorFuturoParcelas = if (im == 0.0) {
            valorParcela * n
        } else {
            valorParcela * (((1 + im).pow(n) - 1) / im)
        }
        val ganhoLiquidoParcelado = valorFuturoProduto - valorFuturoParcelas

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
            interestGainedCash = maxOf(0.0, jurosGanhosAVista),
            netGainInstallment = ganhoLiquidoParcelado,
            difference = kotlin.math.abs(diferenca),
            recommendation = recomendacao
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
