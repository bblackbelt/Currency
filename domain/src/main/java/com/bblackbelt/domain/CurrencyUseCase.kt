package com.bblackbelt.domain

import com.bblackbelt.data.CurrencyService
import com.bblackbelt.domain.mapper.CurrencyRatesMapper
import com.bblackbelt.domain.model.Rates
import io.reactivex.Single

interface CurrencyUseCase {

    fun loadRates(base: String): Single<Rates>

    class Impl(private val service: CurrencyService, private val mapper: CurrencyRatesMapper): CurrencyUseCase {
        override fun loadRates(base: String): Single<Rates> =
            service.loadRates(base).map { mapper.map(it )}
    }
}
