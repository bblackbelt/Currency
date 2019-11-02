package com.bblackbelt.domain.mapper

import com.bblackbelt.data.model.RatesDto
import com.bblackbelt.domain.model.Rate
import com.bblackbelt.domain.model.Rates

interface Mapper<T, V> {

    fun map(input: T): V
}


interface CurrencyRateMapper : Mapper<Map.Entry<String, Float>, Rate> {

    class Impl : CurrencyRateMapper {

        override fun map(input: Map.Entry<String, Float>): Rate =
            with(input) {
                Rate(key, value)
            }
    }
}

interface CurrencyRatesMapper : Mapper<RatesDto, Rates> {

    class Impl(private val currencyRateMapper: CurrencyRateMapper) : CurrencyRatesMapper {

        override fun map(input: RatesDto): Rates {
            val baseRate = Rate(input.base, 1f)
           // val rates = input.rates.map { currencyRateMapper.map(it) }
            return Rates(baseRate, input.rates)
        }
    }
}
