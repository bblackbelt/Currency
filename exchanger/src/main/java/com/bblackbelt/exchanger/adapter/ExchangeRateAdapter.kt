package com.bblackbelt.exchanger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bblackbelt.exchanger.R
import com.bblackbelt.exchanger.model.RateView

class ExchangeRateAdapter(private val onRateClicked: (RateView) -> Unit) :
    RecyclerView.Adapter<ExchangeRateViewHolder>() {

    private val dataSet = AsyncListDiffer<RateView>(this, comparator)

    fun setItems(rates: List<RateView>) {
        dataSet.submitList(rates)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRateViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_row, parent, false)
        return ExchangeRateViewHolder(itemView, onRateClicked)
    }

    override fun getItemCount(): Int = dataSet.currentList.size

    override fun onBindViewHolder(holder: ExchangeRateViewHolder, position: Int) {
        holder.bindView(dataSet.currentList[position])
    }

    companion object {
        private val comparator: DiffUtil.ItemCallback<RateView> =
            object : DiffUtil.ItemCallback<RateView>() {
                override fun areItemsTheSame(oldItem: RateView, newItem: RateView): Boolean =
                    oldItem.currency == newItem.currency

                override fun areContentsTheSame(oldItem: RateView, newItem: RateView): Boolean =
                    oldItem == newItem
            }
    }
}
