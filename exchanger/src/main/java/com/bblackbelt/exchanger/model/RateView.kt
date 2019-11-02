package com.bblackbelt.exchanger.model

data class RateView(val currency: String, val exchangeRate: Float, val name: String, var exchangedRate: Float = 0f)
