package com.gdd.presentation.point

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gdd.domain.model.point.PointRecordListItem
import com.gdd.presentation.R
import com.gdd.presentation.databinding.ItemPointRecordBinding
import com.gdd.presentation.mapper.DateFormatter

class PointRecordListAdapter(
    private val pointRecordList: List<PointRecordListItem>
) : RecyclerView.Adapter<PointRecordListAdapter.PointRecordListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointRecordListViewHolder {
        return PointRecordListViewHolder(
            ItemPointRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PointRecordListViewHolder, position: Int) {
        holder.bind(pointRecordList[position])
    }

    override fun getItemCount(): Int {
        return pointRecordList.size
    }

    class PointRecordListViewHolder(val binding: ItemPointRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recordItem: PointRecordListItem) {
            binding.tvUseData.text = DateFormatter.recordResponseFormatToUi(recordItem.eventDate)
            binding.tvUseAmount.text = "${if (recordItem.amount >= 0) "+" else ""}${recordItem.amount}P"
            binding.tvUseDetail.text = recordItem.eventDetail
            if (recordItem.amount >= 0){
                binding.tvUseAmount.setTextColor(binding.root.context.getColor(R.color.sage_green_dark))
            }
        }
    }
}