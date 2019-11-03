package com.bblackbelt.currency.di


import com.bblackbelt.exchanger.mapper.RateViewMapper
import com.bblackbelt.exchanger.viewmodel.ExchangerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val mvvmModule = module {
    viewModel { ExchangerViewModel(get(), get()) }

    factory<RateViewMapper> { RateViewMapper.Impl() }
}

