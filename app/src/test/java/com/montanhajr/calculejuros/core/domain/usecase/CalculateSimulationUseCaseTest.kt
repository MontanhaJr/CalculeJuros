package com.montanhajr.calculejuros.core.domain.usecase

import com.montanhajr.calculejuros.core.domain.model.InvestmentType
import com.montanhajr.calculejuros.core.domain.model.SimulationInput
import com.montanhajr.calculejuros.core.domain.model.WinnerType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateSimulationUseCaseTest {

    private val useCase = CalculateSimulationUseCase()

    @Test
    fun `when no investment and no discount, cash should be neutral or cash depending on precision`() {
        val input = SimulationInput(
            productPrice = 1000.0,
            cashPrice = 1000.0,
            installmentsCount = 10,
            installmentValue = 100.0,
            investmentType = InvestmentType.NONE
        )

        val result = useCase(input)

        assertEquals(WinnerType.NEUTRAL, result.winner)
        assertEquals(1000.0, result.cashTotalCost, 0.01)
        assertEquals(1000.0, result.installmentTotalCost, 0.01)
    }

    @Test
    fun `when high investment rate, installment should win`() {
        val input = SimulationInput(
            productPrice = 1000.0,
            cashPrice = 1000.0, // No discount at sight
            installmentsCount = 12,
            installmentValue = 83.33, // approx 1000/12
            investmentType = InvestmentType.CUSTOM,
            annualInvestmentRate = 0.12 // 12% per year
        )

        val result = useCase(input)

        assertEquals(WinnerType.INSTALLMENT, result.winner)
        assertTrue("Installment cost should be less than 1000", result.installmentTotalCost < 1000.0)
    }

    @Test
    fun `when big discount at sight, cash should win`() {
        val input = SimulationInput(
            productPrice = 1000.0,
            cashPrice = 800.0, // 20% discount
            installmentsCount = 10,
            installmentValue = 100.0,
            investmentType = InvestmentType.SAVINGS, // low rate
            annualInvestmentRate = 0.06
        )

        val result = useCase(input)

        assertEquals(WinnerType.CASH, result.winner)
        assertEquals(800.0, result.cashTotalCost, 0.01)
    }
}
