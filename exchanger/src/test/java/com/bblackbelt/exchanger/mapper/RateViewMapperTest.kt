package com.bblackbelt.exchanger.mapper

import com.bblackbelt.domain.model.Rate
import com.bblackbelt.exchanger.model.RateView
import org.junit.Assert
import org.junit.Test
import java.util.*

class RateViewMapperTest {

    private val rateViewMapper: RateViewMapper = RateViewMapper.Impl()

    @Test
    fun `test data mapped correctly`() {

        val currencies = listOf("AUD", "USD")

        val rates = currencies.mapIndexed { index, s -> Rate(s, (index + 1).toFloat()) }

        val valueToConvert = 0f
        val mappedValues = rateViewMapper.map(rates, valueToConvert)

        val expectedValues = currencies.mapIndexed { index, s -> RateView(s, (index + 1).toFloat(), Currency.getInstance(s).displayName, 0f) }

        Assert.assertEquals(expectedValues, mappedValues)
    }

    @Test
    fun `test data mapped correctly with value`() {

        val currencies = listOf("AUD", "USD")

        val rates = currencies.mapIndexed { index, s -> Rate(s, (index + 1).toFloat()) }

        val valueToConvert = 2f
        val mappedValues = rateViewMapper.map(rates, valueToConvert)

        val expectedValues = currencies.mapIndexed { index, s -> RateView(s, (index + 1).toFloat(),
            Currency.getInstance(s).displayName, valueToConvert * (index + 1).toFloat()) }

        Assert.assertEquals(expectedValues, mappedValues)
    }

}
