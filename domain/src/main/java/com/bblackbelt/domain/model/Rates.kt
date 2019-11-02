package com.bblackbelt.domain.model

data class Rates(val baseRate: Rate, val rates: Map<String, Float>)
