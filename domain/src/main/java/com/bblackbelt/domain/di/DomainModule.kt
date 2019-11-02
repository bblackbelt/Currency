package com.bblackbelt.domain.di

import com.bblackbelt.domain.CurrencyUseCase
import com.bblackbelt.domain.mapper.CurrencyRateMapper
import com.bblackbelt.domain.mapper.CurrencyRatesMapper
import org.koin.dsl.module

val domainModule = module {
    factory<CurrencyUseCase> { CurrencyUseCase.Impl(get(), get()) }
}

val mappersModule = module {
    factory<CurrencyRatesMapper> { CurrencyRatesMapper.Impl(get()) }
    factory<CurrencyRateMapper> { CurrencyRateMapper.Impl() }
}
