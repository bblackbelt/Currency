package com.bblackbelt.exchanger.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bblackbelt.domain.CurrencyUseCase
import com.bblackbelt.domain.model.Rate
import com.bblackbelt.domain.model.Rates
import com.bblackbelt.exchanger.mapper.RateViewMapper
import com.bblackbelt.exchanger.model.RateView
import io.reactivex.Single
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ExchangerViewModelTest {

    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private val mapper: RateViewMapper = RateViewMapper.Impl()
    private val useCase = Mockito.mock(CurrencyUseCase::class.java)

    @Test
    fun `test loading default false`() {
        val viewModel = ExchangerViewModel(useCase, mapper)

        Assert.assertEquals(viewModel.loading.getValueTimeout(), false)
    }

    @Test
    fun `data loaded success`() {
        val baseRate = "EUR"
        val bR = Rate(baseRate, 1f)
        val exchangeRates = mapOf("USD" to 0.89f, "DKK" to 1.2324f)
        Mockito.`when`(useCase.loadRates(baseRate)).thenReturn(Single.just(Rates(bR, exchangeRates)))

        val viewModel = ExchangerViewModel(useCase, mapper)
        viewModel.updateBaseRate(RateView(baseRate, 1f, ""))

        val v = viewModel.rates.getValueTimeout(time = 10)

        Assert.assertEquals(v, mapper.map(listOf(),exchangeRates, 0f))
    }


    @Test
    fun `exchanged value updated`() {
        val baseRate = "EUR"
        val bR = Rate(baseRate, 1f)
        val exchangeRates = mapOf("USD" to 0.89f, "DKK" to 1.2324f)
        Mockito.`when`(useCase.loadRates(baseRate)).thenReturn(Single.just(Rates(bR, exchangeRates)))

        val viewModel = ExchangerViewModel(useCase, mapper)
        viewModel.updateBaseRate(RateView(baseRate, 1f, ""))

        val valueToConvert = 5f
        viewModel.convertCurrent(valueToConvert)
        Assert.assertEquals(viewModel.rates.getValueTimeout() , mapper.map(viewModel.lastSeenList, exchangeRates, valueToConvert))
    }

    fun <T> LiveData<T>.getValueTimeout(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getValueTimeout.removeObserver(this)
            }
        }
        this.observeForever(observer)

        afterObserve.invoke()

        if (!latch.await(time, timeUnit)) {
            this.removeObserver(observer)
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

}
