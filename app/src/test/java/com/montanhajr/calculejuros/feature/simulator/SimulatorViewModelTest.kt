package com.montanhajr.calculejuros.feature.simulator

import androidx.lifecycle.SavedStateHandle
import com.montanhajr.calculejuros.core.data.db.SimulationEntity
import com.montanhajr.calculejuros.core.data.repository.CurrencyPreferencesRepository
import com.montanhajr.calculejuros.core.data.repository.SimulationRepository
import com.montanhajr.calculejuros.core.domain.model.RecommendationType
import com.montanhajr.calculejuros.core.domain.usecase.CalculateSimulationUseCase
import com.montanhajr.calculejuros.core.domain.usecase.GetSimulationByIdUseCase
import com.montanhajr.calculejuros.core.domain.usecase.SaveSimulationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimulatorViewModelTest {

    private lateinit var viewModel: SimulatorViewModel
    private val calculateUseCase = CalculateSimulationUseCase()
    private val fakeRepository = object : SimulationRepository {
        override fun getAllSimulations(): Flow<List<SimulationEntity>> = emptyFlow()
        override fun getFavoriteSimulations(): Flow<List<SimulationEntity>> = emptyFlow()
        override suspend fun getSimulationById(id: Long): SimulationEntity? = null
        override suspend fun insertSimulation(simulation: SimulationEntity): Long = 1L
        override suspend fun updateSimulations(simulations: List<SimulationEntity>) {}
        override suspend fun deleteSimulation(simulation: SimulationEntity) {}
    }
    private val fakeCurrencyRepository = object : CurrencyPreferencesRepository {
        override val currencySymbol: Flow<String> = flowOf("R$")
        override suspend fun saveCurrencySymbol(symbol: String) {}
    }
    private val saveUseCase = SaveSimulationUseCase(fakeRepository)
    private val getByIdUseCase = GetSimulationByIdUseCase(fakeRepository)
    private val savedStateHandle = SavedStateHandle()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SimulatorViewModel(calculateUseCase, saveUseCase, getByIdUseCase, fakeCurrencyRepository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when useDiscount is true, result should have correct cashPrice`() {
        viewModel.onProductPriceChange("100000") // 1000,00
        viewModel.onDiscountChange("1000") // 10,00 %
        viewModel.onUseDiscountToggle(true)
        
        viewModel.onCalculate()
        
        val result = viewModel.uiState.value.simulationResult
        assertEquals(900.0, result?.cashPrice ?: 0.0, 0.01)
    }

    @Test
    fun `when switching toggle to cash price, it should prefill based on discount`() {
        viewModel.onProductPriceChange("100000") // 1000,00
        viewModel.onDiscountChange("1000") // 10,00 %
        viewModel.onUseDiscountToggle(true)
        
        viewModel.onUseDiscountToggle(false)
        
        assertEquals("90000", viewModel.uiState.value.cashPrice)
    }

    @Test
    fun `when switching toggle to monthly rate, it should prefill based on total value`() {
        viewModel.onProductPriceChange("100000") // 1000,00
        viewModel.onInstallmentsChange(12)
        // First switch to Total Value mode
        viewModel.onUseMonthlyRateToggle(false)
        // Then set the total value
        viewModel.onTotalInstallmentValueChange("120555") // 1205,55
        
        // Now switch back to Monthly Rate mode to see if it converts 1205,55 back to 3,00%
        viewModel.onUseMonthlyRateToggle(true)
        
        assertEquals("300", viewModel.uiState.value.cardTaxRate)
    }

    @Test
    fun `when switching to annual profitability, it should convert monthly rate`() {
        // First switch to Monthly mode
        viewModel.onUseAnnualProfitabilityToggle(false)
        // Then set the monthly rate
        viewModel.onInvestmentMonthlyRateChange("100") // 1,00 %
        
        // Now switch back to Annual mode to see if it converts 1,00% monthly to ~12,68% annual
        viewModel.onUseAnnualProfitabilityToggle(true)
        
        assertEquals("1268", viewModel.uiState.value.investmentAnnualRate)
    }
}
