package com.montanhajr.calculejuros.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "simulations")
data class SimulationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val productName: String,
    
    // Inputs
    val inputProductPrice: Double,
    val inputDiscountPercentage: Double,
    val inputCashPrice: Double,
    val inputUseDiscountToggle: Boolean,
    val inputInstallmentsCount: Int,
    val inputMonthlyCardRate: Double,
    val inputTotalInstallmentValue: Double,
    val inputUseMonthlyRateToggle: Boolean,
    val inputAnnualProfitability: Double,
    val inputMonthlyProfitability: Double,
    val inputUseAnnualProfitabilityToggle: Boolean,
    val inputDownPayment: Double = 0.0,
    val inputUseDownPayment: Boolean = false,
    val inputPrepaymentDiscountPercentage: Double = 0.0,
    val inputPrepaymentDiscountValue: Double = 0.0,
    val inputUsePrepaymentDiscount: Boolean = false,
    val inputPrepaymentDiscountIsPercentage: Boolean = true,
    val inputDownPaymentPercentage: Double = 0.0,
    val inputDownPaymentIsPercentage: Boolean = false,
    
    // Results (Summary)
    val productPrice: Double,
    val cashPrice: Double,
    val installmentsCount: Int,
    val installmentValue: Double,
    val winner: String,
    val difference: Double,
    
    // UI/Metadata
    val isFavorite: Boolean = false,
    val scenarioName: String? = null,
    val iconType: String = "default",
    val favoriteOrder: Int = 0
)
