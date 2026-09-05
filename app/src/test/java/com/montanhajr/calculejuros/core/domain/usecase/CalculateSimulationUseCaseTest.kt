package com.montanhajr.calculejuros.core.domain.usecase

import com.montanhajr.calculejuros.core.domain.model.RecommendationType
import com.montanhajr.calculejuros.core.domain.model.SimulationInput
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateSimulationUseCaseTest {

    private val useCase = CalculateSimulationUseCase()

    @Test
    fun `Example A - no discount, no interest, positive profitability`() {
        val input = SimulationInput(
            productPrice = 1000.0,
            discountPercentage = 0.0,
            useDiscountToggle = true,
            installmentsCount = 12,
            monthlyCardRate = 0.0,
            useMonthlyRateToggle = true,
            monthlyProfitability = 1.0,
            useAnnualProfitabilityToggle = false
        )

        val result = useCase(input)

        assertEquals(83.33, result.installmentValue, 0.01)
        assertEquals(0.00, result.netGainCash, 0.01)
        assertEquals(69.95, result.netGainInstallment, 0.05)
        assertEquals(RecommendationType.PARCELADO, result.recommendation)
    }

    @Test
    fun `Example B - 10 percent discount, no interest, 1 percent monthly profitability`() {
        val input = SimulationInput(
            productPrice = 1000.0,
            discountPercentage = 10.0,
            useDiscountToggle = true,
            installmentsCount = 12,
            monthlyCardRate = 0.0,
            useMonthlyRateToggle = true,
            monthlyProfitability = 1.0,
            useAnnualProfitabilityToggle = false
        )

        val result = useCase(input)

        assertEquals(900.0, result.cashPrice, 0.01)
        assertEquals(100.0, result.discountValue, 0.01)
        assertEquals(112.68, result.netGainCash, 0.01)
        assertEquals(69.95, result.netGainInstallment, 0.05)
        assertEquals(RecommendationType.A_VISTA, result.recommendation)
    }

    @Test
    fun `Example C - no discount, 3 percent card rate, 1 percent monthly profitability`() {
        val input = SimulationInput(
            productPrice = 1000.0,
            discountPercentage = 0.0,
            useDiscountToggle = true,
            installmentsCount = 12,
            monthlyCardRate = 3.0,
            useMonthlyRateToggle = true,
            monthlyProfitability = 1.0,
            useAnnualProfitabilityToggle = false
        )

        val result = useCase(input)

        assertEquals(100.46, result.installmentValue, 0.01)
        assertEquals(1205.55, result.totalInstallmentValue, 0.1)
        assertEquals(0.00, result.netGainCash, 0.01)
        assertEquals(-146.82, result.netGainInstallment, 0.1)
        assertEquals(RecommendationType.A_VISTA, result.recommendation)
    }

    @Test
    fun `Example D - everything zero (neutral)`() {
        val input = SimulationInput(
            productPrice = 1000.0,
            discountPercentage = 0.0,
            useDiscountToggle = true,
            installmentsCount = 12,
            monthlyCardRate = 0.0,
            useMonthlyRateToggle = true,
            monthlyProfitability = 0.0,
            useAnnualProfitabilityToggle = false
        )

        val result = useCase(input)

        assertEquals(0.00, result.netGainCash, 0.01)
        assertEquals(0.00, result.netGainInstallment, 0.01)
        assertEquals(RecommendationType.EMPATE, result.recommendation)
    }

    @Test
    fun `Example E - implicit rate from total value`() {
        val input = SimulationInput(
            productPrice = 1000.0,
            installmentsCount = 12,
            totalInstallmentValue = 1205.55,
            useMonthlyRateToggle = false,
            monthlyProfitability = 1.0,
            useAnnualProfitabilityToggle = false
        )

        val result = useCase(input)

        assertEquals(3.0, result.monthlyCardRate, 0.01)
    }
}
