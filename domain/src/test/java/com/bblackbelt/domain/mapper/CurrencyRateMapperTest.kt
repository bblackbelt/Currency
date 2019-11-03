package com.bblackbelt.domain.mapper

import com.bblackbelt.domain.model.Rate
import org.junit.Assert
import org.junit.Test

class CurrencyRateMapperTest {

    private val mapper: CurrencyRateMapper = CurrencyRateMapper.Impl()

    @Test
    fun `data mapped correctly`() {
        val map = mapOf("AUD" to 2f, "USD" to 3f)
        val expectedValues = map.map { mapper.map(it) }

        Assert.assertEquals(expectedValues, listOf(Rate("AUD", 2f), Rate("USD", 3f)))
    }
}
