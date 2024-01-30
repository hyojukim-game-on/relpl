package com.gdd.presentation.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.gdd.domain.model.history.HistoryDetail
import com.gdd.presentation.databinding.ItemHistoryDetailBinding

class HistoryDetailAdapter(private val layoutInflater: LayoutInflater,
                         private val detailList: List<HistoryDetail>,
                         @param:LayoutRes private val rowLayout: Int,)
    : RecyclerView.Adapter<HistoryDetailAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHistoryDetailBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(item: HistoryDetail){
            binding.tvDate.text = "${item.moveStart.format()} ~ ${item.moveEnd.format()}"
            binding.tvDistance.text = item.moveDistance.toDetailDistance()
            binding.tvCont.text = "${item.moveContribution}%"
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryDetailAdapter.ViewHolder {
        val binding = ItemHistoryDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryDetailAdapter.ViewHolder, position: Int) {
        holder.bind(detailList[position])
    }

    override fun getItemCount(): Int = detailList.size
}

fun String.format(): String{
    return this.substring(5)
}

fun Int.toDetailDistance(): String{
    val km = this/1000
    val m = this % 1000
    return "${km}km ${m}m"
}