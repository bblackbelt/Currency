package com.bblackbelt.data.di

import com.bblackbelt.data.ApiService
import com.bblackbelt.data.CurrencyService
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://revolut.duckdns.org/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.computation()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ApiService> { get<Retrofit>().create(ApiService::class.java) }

    factory<CurrencyService> { CurrencyService.Impl(get()) }
}
