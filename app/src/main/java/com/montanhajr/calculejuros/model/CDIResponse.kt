package com.montanhajr.calculejuros.model

import com.squareup.moshi.Json

data class CDIResponse(
    @field:Json(name = "@odata.context")
    val odataContext: String,
    @field:Json(name = "value")
    val value: Array<CDI>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CDIResponse

        if (odataContext != other.odataContext) return false
        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        var result = odataContext.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}