package com.gdd.presentation.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.gdd.domain.model.history.History
import com.gdd.presentation.base.Constants
import com.gdd.presentation.databinding.ItemHistoryBinding
import java.util.StringTokenizer

class HistoryAdapter(private val layoutInflater: LayoutInflater,
                     private val historyList: List<History>,
                     @param:LayoutRes private val rowLayout: Int,
                     private var itemClickListener: (History) -> Unit): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(item: History){
            if (item.projectIsDone) binding.clNotDone.visibility = View.GONE

            binding.tvTitle.text = item.projectName
            binding.tvDateFrom.text = "${item.createDate.toFormattedDate()} 부터"
            binding.tvDateTo.text = "${item.endDate.toFormattedDate()} 까지"
            binding.tvDistance.text = item.totalDistance.toRelayDistance()
            binding.tvContributor.text = item.totalContributor.toKoreanNumber()


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
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size
}

fun String.toFormattedDate(): String{
    var date = ""
    val tk = StringTokenizer(this, "-")
    date += tk.nextToken() + "년 "
    date += tk.nextToken() + "월 "
    date += tk.nextToken() + "일"

    return date
}

fun Int.toKoreanNumber(): String{
    if (this >= 60){
        return this.toString()
    }
    val n = this%10
    val m = this-n

    return "${Constants.numToKoreanMap[m]} ${Constants.numToKoreanMap[n]} 분께서 깨끗하게 해주셨어요 :)"
}

fun Int.toRelayDistance(): String{
    val km = if(this/1000 > 0) "${this/1000}km" else ""
    val m = this % 1000
    return "총 $km ${m}m의 길을"
}