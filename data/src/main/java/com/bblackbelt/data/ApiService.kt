package com.bblackbelt.data

import com.bblackbelt.data.model.RatesDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("latest")
    fun loadRates(@Query("base") base: String = "EUR"): Single<RatesDto>
}
