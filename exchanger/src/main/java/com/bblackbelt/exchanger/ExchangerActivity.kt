package com.bblackbelt.exchanger

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bblackbelt.domain.model.Rate
import com.bblackbelt.exchanger.adapter.ExchangeRateAdapter
import com.bblackbelt.exchanger.model.RateView
import com.bblackbelt.exchanger.viewmodel.ExchangerViewModel
import kotlinx.android.synthetic.main.activity_exchanger.*
import kotlinx.android.synthetic.main.currency_row.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ExchangerActivity : AppCompatActivity() {

    private val exchangerViewModel: ExchangerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchanger)

        setSupportActionBar(toolbar)

        val adapter = ExchangeRateAdapter {
            exchangerViewModel.updateBaseRate(it)
        }

        exchangerViewModel.updateBaseRate(RateView(DEFAULT_BASE_CURRENCY, 1f, ""))
        exchangerViewModel.rates.observe(this, Observer { items: List<RateView> ->
            adapter.setItems(items)
        })

        exchangerViewModel.baseRate.observe(this, Observer {
            updateBaseRateView(it)
        })

        exchangerViewModel.loading.observe(this, Observer {
            progressBar.visibility = if(it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })

        currenciesRV.adapter = adapter
        val margin = resources.getDimension(R.dimen.margin_16).toInt()
        currenciesRV.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.set(margin, margin, margin, 0)
                if (parent.getChildAdapterPosition(view) == adapter.itemCount -1) {
                    outRect.bottom = margin
                }
            }
        })
    }

    private fun updateBaseRateView(rateView: RateView) {
        currencyName.text = rateView.currency
        currency.text = rateView.currency
        convertedCurrencyValue.isEnabled = true
        convertedCurrencyValue.afterTextChanged {
            exchangerViewModel.convertCurrent(it.toFloatOrNull() ?: 0f)
        }
    }

    companion object {
        private const val DEFAULT_BASE_CURRENCY = "EUR"
    }
}
