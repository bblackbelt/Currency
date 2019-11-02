package com.bblackbelt.exchanger.viewmodel

import androidx.lifecycle.*
import com.bblackbelt.domain.CurrencyUseCase
import com.bblackbelt.domain.model.Rate

import com.bblackbelt.exchanger.model.RateView
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import java.util.*

import java.util.concurrent.TimeUnit

class ExchangerViewModel constructor(useCase: CurrencyUseCase) : ViewModel() {

    private val _baseRate = MutableLiveData<RateView>()
    val baseRate
        get(): LiveData<RateView> = _baseRate

    private val amountToExchange: BehaviorSubject<Float> = BehaviorSubject.createDefault(0f)

    val rates = Transformations.switchMap(baseRate) { rate ->
        val ratesFlowable: Flowable<List<Rate>> = Observable.timer(1, TimeUnit.SECONDS)
            .switchMap { useCase.loadRates(rate.currency).toObservable() }
            .map { ratesObj -> ratesObj.rates }
            .flatMapIterable { it }
            .toList()
            .onErrorReturn { listOf() }
            .repeat()

        val amountFlowable = amountToExchange.toFlowable(BackpressureStrategy.LATEST)

        LiveDataReactiveStreams.fromPublisher(
            Flowable.combineLatest(ratesFlowable,
                amountFlowable, BiFunction<List<Rate>, Float, List<RateView>>
                { items, value ->
                    _loading.postValue(false)
                    items.map {
                        RateView(
                            it.currency,
                            it.rate,
                            getCurrencyName(it.currency),
                            it.rate * value
                        )
                    }
                })
        )
    }

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private fun getCurrencyName(currencyCode: String): String {
        return Currency.getInstance(currencyCode).displayName
    }

    fun updateBaseRate(newBaseRate: RateView) {
        val tmp = rates.value?.toMutableList()
        tmp?.let {
            val toRemove = tmp.find { it.currency == newBaseRate.currency }
            tmp.remove(toRemove)
            (rates as? MutableLiveData)?.value = tmp
        }

        _baseRate.value = newBaseRate
        _loading.value = true
    }

    fun convertCurrent(fl: Float) {
        amountToExchange.onNext(fl)
    }

}
