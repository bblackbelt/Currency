package com.bblackbelt.domain

import com.bblackbelt.data.CurrencyService
import com.bblackbelt.data.model.RatesDto
import com.bblackbelt.domain.mapper.CurrencyRateMapper
import com.bblackbelt.domain.mapper.CurrencyRatesMapper
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mockito


class CurrencyUseCaseTest {

    private val service = Mockito.mock(CurrencyService::class.java)
    private val singleRateMapper = CurrencyRateMapper.Impl()
    private val mapper: CurrencyRatesMapper = CurrencyRatesMapper.Impl(singleRateMapper)

    @Test
    fun `data loaded correctly`() {
        val baseRate = "EUR"
        val exchangeRates = mapOf("dkk" to 1.5f, "usd" to 0.89f)
        val rates = RatesDto(baseRate, exchangeRates)
        Mockito.`when`(service.loadRates(baseRate)).thenReturn(Single.just(rates))

        val useCase : CurrencyUseCase = CurrencyUseCase.Impl(service, mapper)

        useCase.loadRates(baseRate)
            .test()
            .assertValue { it == mapper.map(rates) }
            .assertComplete()
    }

    @Test
    fun `test error`() {
        val baseRate = "EUR"
        val error = Throwable()
        Mockito.`when`(service.loadRates(baseRate)).thenReturn(Single.error(error))

        val useCase : CurrencyUseCase = CurrencyUseCase.Impl(service, mapper)

        useCase.loadRates(baseRate)
            .test()
            .assertError(error)
            .assertNotComplete()
    }

}
