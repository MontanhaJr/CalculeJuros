package com.montanhajr.calculejuros.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "simulations")
data class SimulationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val productName: String,
    val productPrice: Double,
    val cashPrice: Double,
    val installmentsCount: Int,
    val installmentValue: Double,
    val winner: String,
    val difference: Double
)
