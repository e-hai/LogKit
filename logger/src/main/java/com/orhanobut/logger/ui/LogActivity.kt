package com.orhanobut.logger.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger.ASSERT
import com.orhanobut.logger.Logger.DEBUG
import com.orhanobut.logger.Logger.ERROR
import com.orhanobut.logger.Logger.INFO
import com.orhanobut.logger.Logger.VERBOSE
import com.orhanobut.logger.Logger.WARN
import com.orhanobut.logger.R
import com.orhanobut.logger.adapter.AndroidLogAdapter
import com.orhanobut.logger.log.strategy.LogcatLogStrategy
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LogActivity : ComponentActivity() {

  private val viewModel by viewModels<LogViewModel>()
  private val tagsAdapter = TagsAdapter()
  private val logsAdapter = LogsAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_log)
    val rcyLogs = findViewById<RecyclerView>(R.id.rcy_logs)
    val rcyTags = findViewById<RecyclerView>(R.id.rcy_tags)

    findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }
    findViewById<ImageView>(R.id.iv_menu).setOnClickListener {
      if (rcyTags.visibility == View.VISIBLE) {
        rcyTags.visibility = View.GONE
      } else {
        rcyTags.visibility = View.VISIBLE
      }
    }
    rcyLogs.apply {
      layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
      adapter = logsAdapter
    }
    rcyTags.apply {
      layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
      adapter = tagsAdapter
    }
    lifecycleScope.launch {
      viewModel.tags.collectLatest {
        tagsAdapter.update(it)
      }
    }
    lifecycleScope.launch {
      viewModel.logs.collectLatest {
        logsAdapter.update(it)
      }
    }
    viewModel.loadLogs()
  }
}

private class LogsAdapter : RecyclerView.Adapter<LogsAdapter.VH>() {

  private var dataList: List<AndroidLogAdapter.CacheInfo> = emptyList()

  fun update(data: List<AndroidLogAdapter.CacheInfo>) {
    dataList = data
    notifyItemRangeChanged(0, dataList.size - 1)
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
    fun updateItemView(data: AndroidLogAdapter.CacheInfo) {
      itemView.findViewById<TextView>(R.id.tv_tag).text = data.tag
      itemView.findViewById<TextView>(R.id.tv_date).text = data.date
      itemView.findViewById<TextView>(R.id.tv_msg).text = data.message
      val color = when (data.priority) {
        VERBOSE -> {
          itemView.resources.getColor(R.color.verbose)
        }
        DEBUG -> {
          itemView.resources.getColor(R.color.debug)
        }
        INFO -> {
          itemView.resources.getColor(R.color.info)
        }
        WARN -> {
          itemView.resources.getColor(R.color.warning)
        }
        ERROR -> {
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

private class TagsAdapter : RecyclerView.Adapter<TagsAdapter.VH>() {

  private var dataList: List<String> = emptyList()

  fun update(data: List<String>) {
    dataList = data
    notifyItemRangeChanged(0, dataList.size - 1)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val binding = LayoutInflater.from(parent.context).inflate(R.layout.adapter_tags, parent, false)
    return VH(binding)
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.updateItemView(dataList[position])
  }

  override fun getItemCount(): Int {
    return dataList.size
  }

  class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun updateItemView(data: String) {
      itemView.findViewById<TextView>(R.id.tv_tag).text = data
    }
  }
}
