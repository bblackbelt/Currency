package com.bblackbelt.exchanger.viewmodel

import androidx.lifecycle.*
import com.bblackbelt.domain.CurrencyUseCase
import com.bblackbelt.domain.RatesState
import com.bblackbelt.domain.model.Rate
import com.bblackbelt.domain.model.Rates

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

    private var lastSeenList: MutableList<RateView> = mutableListOf()

    val rates = Transformations.switchMap(baseRate) { rate ->
        val ratesFlowable: Observable<Rates> = Observable.timer(1, TimeUnit.SECONDS)
            .switchMap { useCase.loadRates(rate.currency).toObservable() }
            .filter { it is RatesState.OK }
            .map { ratesObj -> (ratesObj as RatesState.OK).rates }
            .repeat()


        val amountFlowable = amountToExchange

        LiveDataReactiveStreams.fromPublisher(
            Observable.combineLatest(ratesFlowable,
                amountFlowable, BiFunction<Rates, Float, List<RateView>>
                { items, currentAmount ->
                    _loading.postValue(false)
                    return@BiFunction if (lastSeenList.isEmpty()) {
                        lastSeenList.addAll(createRates(items.rates, currentAmount))
                        lastSeenList
                    } else {
                        updateRates(lastSeenList.toList(), items.rates, currentAmount)
                    }

                }).toFlowable(BackpressureStrategy.LATEST)
        )
    }

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private fun getCurrencyName(currencyCode: String): String {
        return Currency.getInstance(currencyCode).displayName
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

    fun updateBaseRate(newBaseRate: RateView) {

        synchronized(lastSeenList) {
            val item = lastSeenList.find { it.currency == newBaseRate.currency }
            if (item != null) {
                lastSeenList.remove(item)
                val currentBaseRate = _baseRate.value
                if (currentBaseRate != null) {
                    lastSeenList.add(0, currentBaseRate)
                }
            }
            (rates as? MutableLiveData)?.value = lastSeenList
        }

        _baseRate.value = newBaseRate
        _loading.value = true
    }

    fun convertCurrent(fl: Float) {
        amountToExchange.onNext(fl)
    }

}
