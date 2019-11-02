package com.bblackbelt.exchanger.viewmodel

import android.icu.util.Currency
import androidx.lifecycle.*
import com.bblackbelt.domain.CurrencyUseCase
import com.bblackbelt.domain.model.Rate
import com.bblackbelt.exchanger.model.RateView

class ExchangerViewModel constructor(useCase: CurrencyUseCase) : ViewModel() {

    private val _baseRate = MutableLiveData<Rate>()
    val baseRate
        get(): LiveData<Rate> = _baseRate

    val rates = Transformations.switchMap(baseRate) {
        LiveDataReactiveStreams.fromPublisher(useCase.loadRates(it.currency)
            .map { ratesObj -> ratesObj.rates }
            .flattenAsObservable { rates -> rates }
            .map { current ->
                RateView(current.currency, current.rate, getCurrencyName(current.currency))
            }
            .toList()
            .onErrorReturn { listOf() }
            .toFlowable()
        )
    }

    private fun getCurrencyName(currencyCode: String): String {
        return java.util.Currency.getInstance(currencyCode).displayName
    }

    fun updateBaseRate(newBaseRate: Rate) {
        _baseRate.value = newBaseRate
    }

}
