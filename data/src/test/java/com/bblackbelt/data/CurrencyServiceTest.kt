package com.bblackbelt.data

import com.bblackbelt.data.model.RatesDto
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mockito
class CurrencyServiceTest {

    private val apiServiceMock = Mockito.mock(ApiService::class.java)

    @Test
    fun `test rates loaded`() {
        val baseRate = "EUR"
        val exchangeRates = mapOf("dkk" to 1.5f, "usd" to 0.89f)
        val rates = RatesDto(baseRate, exchangeRates)
        Mockito.`when`(apiServiceMock.loadRates(baseRate)).thenReturn(Single.just(rates))

        val service: CurrencyService = CurrencyService.Impl(apiServiceMock)

        service.loadRates(baseRate).test()
            .assertValue { it == rates }
            .assertComplete()
    }

    @Test
    fun `test error`() {
        val baseRate = "EUR"
        val error = Throwable()
        Mockito.`when`(apiServiceMock.loadRates(baseRate)).thenReturn(Single.error(error))


        val service: CurrencyService = CurrencyService.Impl(apiServiceMock)

        service.loadRates(baseRate).test()
            .assertError(error)
            .assertNotComplete()
    }

}
