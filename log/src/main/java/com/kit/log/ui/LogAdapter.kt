package com.kit.log.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kit.log.R
import com.orhanobut.logger.Logger
import com.orhanobut.logger.Utils

class LogAdapter : RecyclerView.Adapter<LogAdapter.VH>() {

  private var dataList: List<LogInfo> = emptyList()

  fun update(data: List<LogInfo>) {
    dataList = data
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val binding = LayoutInflater.from(parent.context).inflate(R.layout.adapter_logs, parent, false)
    return VH(binding)
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.updateItemView(dataList[position])
  }

  override fun getItemCount(): Int {
    return dataList.size
  }


  class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun updateItemView(data: LogInfo) {
      itemView.findViewById<TextView>(R.id.tv_tag).text = data.tag
      itemView.findViewById<TextView>(R.id.tv_date).text = data.dateString
      itemView.findViewById<TextView>(R.id.tv_msg).text = data.message
      val color = when (Utils.logLevel(data.priority)) {
        Logger.VERBOSE -> {
          itemView.resources.getColor(R.color.verbose)
        }

        Logger.DEBUG -> {
          itemView.resources.getColor(R.color.debug)
        }

        Logger.INFO -> {
          itemView.resources.getColor(R.color.info)
        }

        Logger.WARN -> {
          itemView.resources.getColor(R.color.warning)
        }

        Logger.ERROR -> {
          itemView.resources.getColor(R.color.error)
        }

        else -> {
          Color.BLACK
        }
      }
      itemView.setBackgroundColor(color)
    }
  }
}
