package com.bblackbelt.domain.mapper

import com.bblackbelt.data.model.RatesDto
import com.bblackbelt.domain.model.Rate
import com.bblackbelt.domain.model.Rates
import org.junit.Assert
import org.junit.Test

class CurrencyRatesMapperTest {
    private val currencyRateMapper: CurrencyRateMapper = CurrencyRateMapper.Impl()
    private val currencyRatesMapper: CurrencyRatesMapper = CurrencyRatesMapper.Impl(currencyRateMapper)

    @Test
    fun `data mapped correctly`() {
        val map = mapOf("AUD" to 2f, "USD" to 3f)
        val ratesDto = RatesDto("EUR", map)

        val expectedValues = currencyRatesMapper.map(ratesDto)

        val rates = Rates(Rate(ratesDto.base, 1f), listOf(Rate("AUD", 2f), Rate("USD", 3f)))

        Assert.assertEquals(expectedValues, rates)
    }
}
