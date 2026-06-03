package com.montanhajr.calculejuros.core.domain.usecase

import com.montanhajr.calculejuros.core.domain.model.*
import javax.inject.Inject
import kotlin.math.pow

class CalculateSimulationUseCase @Inject constructor() {

    operator fun invoke(input: SimulationInput): SimulationResult {
        val cashTotalCost = input.cashPrice

        if (input.investmentType == InvestmentType.NONE) {
            val installmentTotalCost = (input.installmentsCount * input.installmentValue) + input.downPayment
            val difference = cashTotalCost - installmentTotalCost
            val winner = when {
                difference > 0.1 -> WinnerType.INSTALLMENT // Rare if no investment
                difference < -0.1 -> WinnerType.CASH
                else -> WinnerType.NEUTRAL
            }

            return SimulationResult(
                cashTotalCost = cashTotalCost,
                installmentTotalCost = installmentTotalCost,
                difference = kotlin.math.abs(difference),
                winner = winner,
                grossYield = 0.0,
                taxAmount = 0.0,
                netYield = 0.0,
                monthlyDetails = emptyList()
            )
        }

        // Calculate with investment
        val monthlyRate = (1 + input.annualInvestmentRate).pow(1.0 / 12) - 1
        val initialInvestment = input.cashPrice - input.downPayment
        
        var currentBalance = initialInvestment
        val details = mutableListOf<MonthlyDetail>()
        var totalGrossYield = 0.0

        for (i in 1..input.installmentsCount) {
            val monthlyYield = currentBalance * monthlyRate
            totalGrossYield += monthlyYield
            currentBalance += monthlyYield
            currentBalance -= input.installmentValue
            
            details.add(MonthlyDetail(
                month = i,
                balance = maxOf(currentBalance, 0.0),
                installment = input.installmentValue,
                yield = monthlyYield
            ))
        }

        val taxAmount = if (input.hasProgressiveTax) {
            calculateIR(totalGrossYield, input.installmentsCount * 30)
        } else 0.0
        
        val netYield = totalGrossYield - taxAmount
        val installmentTotalCost = (input.installmentsCount * input.installmentValue) + input.downPayment - netYield
        val difference = cashTotalCost - installmentTotalCost
        
        val winner = when {
            difference > 0.1 -> WinnerType.INSTALLMENT
            difference < -0.1 -> WinnerType.CASH
            else -> WinnerType.NEUTRAL
        }

        return SimulationResult(
            cashTotalCost = cashTotalCost,
            installmentTotalCost = installmentTotalCost,
            difference = kotlin.math.abs(difference),
            winner = winner,
            grossYield = totalGrossYield,
            taxAmount = taxAmount,
            netYield = netYield,
            monthlyDetails = details
        )
    }

    private fun calculateIR(ganho: Double, diasAplicacao: Int): Double {
        val aliquota = when {
            diasAplicacao <= 180 -> 0.225
            diasAplicacao <= 360 -> 0.200
            diasAplicacao <= 720 -> 0.175
            else -> 0.150
        }
        return ganho * aliquota
    }
}
