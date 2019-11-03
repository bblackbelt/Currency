package com.bblackbelt.exchanger.mapper

import com.bblackbelt.exchanger.model.RateView
import java.util.*

interface RateViewMapper {

    class Impl : RateViewMapper {

        private val currencyInstanceMap: MutableMap<String, String> = mutableMapOf()

        override fun map(
            current: List<RateView>,
            newRates: Map<String, Float>,
            valueToConvert: Float
        ): List<RateView> {
            return if (current.isEmpty()) {
                createRates(newRates, valueToConvert)
            } else {
                updateRates(current, newRates, valueToConvert)
            }
        }

        private fun updateRates(
            current: List<RateView>,
            rates: Map<String, Float>,
            currentAmount: Float
        ): List<RateView> {
            return current.map {
                val tmpRate = rates[it.currency] ?: 0f
                RateView(
                    it.currency,
                    tmpRate,
                    getCurrencyName(it.currency),
                    tmpRate * currentAmount
                )
            }
        }

        private fun createRates(
            rates: Map<String, Float>,
            currentAmount: Float
        ): List<RateView> {
            return rates.map { rate ->
                RateView(
                    rate.key,
                    rate.value,
                    getCurrencyName(rate.key),
                    rate.value * currentAmount
                )
            }
        }


        private fun getCurrencyName(currencyCode: String): String {
            return currencyInstanceMap.getOrPut(currencyCode) {
                Currency.getInstance(currencyCode).displayName
            }
        }

    }

    fun map(
        current: List<RateView>,
        newRates: Map<String, Float>,
        valueToConvert: Float
    ): List<RateView>
}
