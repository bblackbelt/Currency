package com.bblackbelt.domain.model


data class Rates(val baseRate: Rate, val rates: Map<String, Float>) {

    companion object {
        val INVALID = Rates(Rate("", -1f), mapOf())
    }
}
