package com.orhanobut.logger.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.R

class TagsAdapter(private val callback: (tag: String) -> Unit) :
  RecyclerView.Adapter<TagsAdapter.VH>() {

  private var dataList: List<String> = emptyList()
  private var selectPosition = 0

  fun update(data: List<String>) {
    dataList = data
    selectPosition = 0
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val binding = LayoutInflater.from(parent.context).inflate(R.layout.adapter_tags, parent, false)
    return VH(binding)
  }

  override fun onBindViewHolder(holder: VH, @SuppressLint("RecyclerView") position: Int) {
    val tag = dataList[position]
    holder.tagView.isSelected = (selectPosition == position)
    holder.tagView.text = tag
    holder.tagView.setOnClickListener {
      selectPosition = position
      callback.invoke(tag)
      notifyDataSetChanged()
    }
  }

  override fun getItemCount(): Int {
    return dataList.size
  }

  class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tagView: TextView

    init {
      tagView = itemView.findViewById(R.id.tv_tag)
    }
  }
}