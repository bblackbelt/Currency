package com.bblackbelt.currency.di


import com.bblackbelt.exchanger.mapper.RateViewMapper
import com.bblackbelt.exchanger.viewmodel.ExchangerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val mvvmModule = module {
    factory<RateViewMapper> { RateViewMapper.Impl() }
    viewModel { ExchangerViewModel(get(), get() ) }
}

