package com.montanhajr.calculejuros.network

import com.montanhajr.calculejuros.model.CDI
import com.montanhajr.calculejuros.model.CDIResponse
import retrofit2.http.GET

interface Api {
    @GET("odata4/ValoresSerie(SERCODIGO='BM12_TJCDI12')")
    suspend fun getCDI(): CDIResponse
}