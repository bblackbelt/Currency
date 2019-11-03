package com.bblackbelt.exchanger.viewmodel

import androidx.lifecycle.*
import com.bblackbelt.domain.CurrencyUseCase
import com.bblackbelt.domain.model.Rates
import com.bblackbelt.exchanger.mapper.RateViewMapper

import com.bblackbelt.exchanger.model.RateView
import io.reactivex.BackpressureStrategy

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

import java.util.concurrent.TimeUnit

class ExchangerViewModel constructor(useCase: CurrencyUseCase, mapper: RateViewMapper) :
    ViewModel() {

    private val _baseRate = MutableLiveData<RateView>()
    val baseRate
        get(): LiveData<RateView> = _baseRate

    private val amountToExchange: BehaviorSubject<Float> = BehaviorSubject.createDefault(0f)

    internal var lastSeenList: MutableList<RateView> = mutableListOf()

    val rates = Transformations.switchMap(baseRate) { rate ->
        val ratesFlowable: Observable<Rates> = Observable.timer(1, TimeUnit.SECONDS)
            .switchMap { useCase.loadRates(rate.currency).toObservable() }
            .onErrorReturn { Rates.INVALID }
            .filter { it != Rates.INVALID }
            .repeat()

        val amountFlowable = amountToExchange

        LiveDataReactiveStreams.fromPublisher(
            Observable.combineLatest(ratesFlowable,
                amountFlowable, BiFunction<Rates, Float, List<RateView>>
                { items, currentAmount ->
                    _loading.postValue(false)
                    return@BiFunction mapper.map(lastSeenList.toList(), items.rates, currentAmount)
                })
                .doOnNext {
                    synchronized(lastSeenList) {
                        lastSeenList.clear()
                        lastSeenList.addAll(it)
                    }
                }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading

    fun updateBaseRate(newBaseRate: RateView) {
        insertFirst(newBaseRate)
        _baseRate.value = newBaseRate
        _loading.value = true
    }

    private fun insertFirst(newBaseRate: RateView) {
        synchronized(lastSeenList) {
            if (lastSeenList.isEmpty()) {
                return
            }
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
    }

    fun convertCurrent(fl: Float) {
        amountToExchange.onNext(fl)
    }

}
