package com.bblackbelt.exchanger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bblackbelt.domain.model.Rate
import com.bblackbelt.exchanger.adapter.ExchangeRateAdapter
import com.bblackbelt.exchanger.model.RateView
import com.bblackbelt.exchanger.viewmodel.ExchangerViewModel
import kotlinx.android.synthetic.main.activity_exchanger.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ExchangerActivity : AppCompatActivity() {

    private val exchangerViewModel: ExchangerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchanger)

        val adapter = ExchangeRateAdapter {}

        exchangerViewModel.updateBaseRate(Rate(DEFAULT_BASE_CURRENCY, 1f))
        exchangerViewModel.rates.observe(this, Observer { items: List<RateView> ->
            adapter.setItems(items)
        })
        exchangerViewModel.baseRate
        currenciesRV.adapter = adapter
    }

    companion object {
        private const val DEFAULT_BASE_CURRENCY = "EUR"
    }
}
