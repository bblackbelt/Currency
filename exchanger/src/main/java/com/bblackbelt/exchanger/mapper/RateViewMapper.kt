package com.bblackbelt.exchanger.mapper

import com.bblackbelt.domain.model.Rate
import com.bblackbelt.exchanger.model.RateView
import java.util.*

interface RateViewMapper {

    class Impl: RateViewMapper {

        private val currencyInstanceMap: MutableMap<String, String> = mutableMapOf()

        override fun map(input: Rate, valueToConvert: Float): RateView =
            with(input) {
                RateView(
                    currency,
                    rate,
                    getCurrencyName(currency),
                    rate * valueToConvert
                )
            }

        override fun map(input: List<Rate>, valueToConvert: Float): List<RateView> {
           return input.map { map(it, valueToConvert) }
        }

        private fun getCurrencyName(currencyCode: String): String {
            return currencyInstanceMap.getOrPut(currencyCode) {
                Currency.getInstance(currencyCode).displayName
            }
        }
    }

    fun map(input: Rate, valueToConvert: Float): RateView
    fun map(input: List<Rate>, valueToConvert: Float): List<RateView>
}
