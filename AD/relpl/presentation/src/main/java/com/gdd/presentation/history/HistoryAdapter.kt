package com.gdd.presentation.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.gdd.domain.model.relay.Relay
import com.gdd.presentation.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.StringTokenizer

class HistoryAdapter(private val layoutInflater: LayoutInflater,
                     private val relayList: List<Relay>,
                     @param:LayoutRes private val rowLayout: Int,
                     private var itemClickListener: (Relay) -> Unit): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(item: Relay){
            if (item.projectIsdone) binding.clNotDone.visibility = View.GONE

            binding.tvTitle.text = item.projectName
            binding.tvDate.text = "${item.moveStart.toFormattedDate()} 부터 ${item.moveEnd.toFormattedDate()} 까지"
            binding.tvDuration.text = item.moveTime.toRelayDuration()
            binding.tvDistance.text = item.moveDistance.toRelayDistance()

            binding.item.setOnClickListener {
                itemClickListener(item)
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        holder.bind(relayList[position])
    }

    override fun getItemCount(): Int = relayList.size
}

fun String.toFormattedDate(): String{
    var date = ""
    val tk = StringTokenizer(this.substring(0, 10), "-")
    date += tk.nextToken() + "년 "
    date += tk.nextToken() + "월 "
    date += tk.nextToken() + "일"

    return date
}

fun Int.toRelayDuration(): String{
    val day = this/(24 * 60)
    val hour = (this - (24 * 60 * day)) / 60
    val min = (this - (24 * 60 * day)) % 60
    return "${day}일 ${hour}시간 ${min}분 동안"
}

fun Int.toRelayDistance(): String{
    val km = this/1000
    val m = this % 1000
    return "총 ${km}km ${m}m의 길을 깨끗하게 해주셨어요 :)"
}