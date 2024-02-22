package com.montanhajr.calculejuros.model

import com.squareup.moshi.Json

data class CDI(
    @field:Json(name = "SERCODIGO")
    val serCodigo: String,
    @field:Json(name = "VALDATA")
    val valData: String,
    @field:Json(name = "VALVALOR")
    val valValor: Double,
    @field:Json(name = "NIVNOME")
    val nivNome: String,
    @field:Json(name = "TERCODIGO")
    val terCodigo: String
)
