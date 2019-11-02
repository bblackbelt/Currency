package com.bblackbelt.exchanger.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bblackbelt.exchanger.R
import com.bblackbelt.exchanger.model.RateView

class ExchangeRateViewHolder(itemView: View, private val onRateClicked: (RateView) -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    private val currency = itemView.findViewById<TextView>(R.id.currency)
    private val convertedValue = itemView.findViewById<TextView>(R.id.value)
    private val currencyName = itemView.findViewById<TextView>(R.id.currencyName)

    fun bindView(r: RateView) {

        currency.text = r.currency
        convertedValue.text = if (r.exchangedRate != 0f) {
            "%.2f".format(r.exchangedRate)
        } else {
            ""
        }
        currencyName.text = r.name
        itemView.setOnClickListener { onRateClicked(r) }
    }

}
