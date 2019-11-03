package com.bblackbelt.exchanger.viewmodel

import androidx.lifecycle.*
import com.bblackbelt.domain.CurrencyUseCase
import com.bblackbelt.domain.model.Rate
import com.bblackbelt.exchanger.mapper.RateViewMapper

import com.bblackbelt.exchanger.model.RateView
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

import java.util.concurrent.TimeUnit

class ExchangerViewModel constructor
    (useCase: CurrencyUseCase, mapper: RateViewMapper) : ViewModel() {

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
                    mapper.map(items, value)
                })
        )
    }

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading

    fun updateBaseRate(newBaseRate: RateView) {
        _baseRate.value = newBaseRate
        _loading.value = true
    }

    fun convertCurrent(fl: Float) {
        amountToExchange.onNext(fl)
    }

}
