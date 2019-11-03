package com.bblackbelt.exchanger.mapper

import com.bblackbelt.exchanger.model.RateView
import org.junit.Assert
import org.junit.Test
import java.util.*

class RateViewMapperTest {

    private val mapper: RateViewMapper = RateViewMapper.Impl()

    @Test
    fun `test data load correctly`() {
        val exchangeRates = mapOf("AUD" to 2f, "USD" to 3f)
        val value = mapper.map(listOf(), exchangeRates, 0f)
        val expectation: List<RateView> = exchangeRates.map {
            RateView(it.key, it.value, Currency.getInstance(it.key).displayName, 0f)
        }

        Assert.assertEquals(value, expectation)
    }

    @Test
    fun `test data load correctly value to exchange`() {
        val exchangeRates = mapOf("AUD" to 2f, "USD" to 3f)
        val value = mapper.map(listOf(), exchangeRates, 2f)
        val expectation: List<RateView> = exchangeRates.map {
            RateView(it.key, it.value, Currency.getInstance(it.key).displayName, it.value * 2f)
        }

        Assert.assertEquals(value, expectation)
    }


    @Test
    fun `update exchange rates`() {
        val current = listOf<RateView>(
            RateView("USD", 2f, Currency.getInstance("USD").displayName, 0f),
            RateView("EUR", 2f, "Euro", 0f),
            RateView("AUD", 3f, Currency.getInstance("AUD").displayName, 0f))

        val exchangeRates = mapOf("AUD" to 2.2f, "USD" to 3.2f, "EUR" to 1.4f)
        val valutToConvert = 2f

        val value = mapper.map(current, exchangeRates, valutToConvert)

        val tmp: List<RateView> = listOf(
            RateView("USD", 3.2f, Currency.getInstance("USD").displayName, 0f),
            RateView("EUR", 1.4f, "Euro", 0f),
            RateView("AUD", 2.2f, Currency.getInstance("AUD").displayName, 0f))

        val expectation = tmp.map {  RateView(it.currency, it.exchangeRate, it.name, it.exchangeRate * valutToConvert) }

        Assert.assertEquals(value, expectation)
    }


}
