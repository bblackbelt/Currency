package com.bblackbelt.data

import com.bblackbelt.data.model.Rates
import io.reactivex.Single
import retrofit2.Retrofit

interface CurrencyService : ApiService {

    class Impl(private val retrofit: Retrofit): CurrencyService {

        private val service by lazy {
            retrofit.create(ApiService::class.java)
        }

        override fun loadRates(base: String): Single<Rates> = service.loadRates(base)
    }

}
