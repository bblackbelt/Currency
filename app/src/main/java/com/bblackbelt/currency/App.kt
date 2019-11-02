package com.bblackbelt.currency

import android.app.Application

import com.bblackbelt.currency.di.mvvmModule
import com.bblackbelt.data.di.dataModule
import com.bblackbelt.domain.di.domainModule
import com.bblackbelt.domain.di.mappersModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(listOf(domainModule, mappersModule, mvvmModule, dataModule))
        }
    }

}
