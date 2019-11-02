package com.bblackbelt.domain

import com.bblackbelt.data.CurrencyService
import com.bblackbelt.domain.mapper.CurrencyRatesMapper
import com.bblackbelt.domain.model.Rates
import io.reactivex.Single

interface CurrencyUseCase {

    fun loadRates(base: String): Single<RatesState>

    class Impl(private val service: CurrencyService, private val mapper: CurrencyRatesMapper): CurrencyUseCase {
        override fun loadRates(base: String): Single<RatesState> =
            service.loadRates(base)
                .map { RatesState.OK(mapper.map(it)) as RatesState }
                .onErrorReturn { RatesState.ERROR(it) }
    }
}

sealed class RatesState {
    data class OK(val rates: Rates): RatesState()
    data class ERROR(val throwable: Throwable): RatesState()
}
