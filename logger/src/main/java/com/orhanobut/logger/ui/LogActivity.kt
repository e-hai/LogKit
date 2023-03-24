package com.orhanobut.logger.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.databinding.ActivityLogBinding
import com.orhanobut.logger.databinding.AdapterLogsBinding
import kotlinx.coroutines.flow.collectLatest

class LogActivity : AppCompatActivity() {

  private lateinit var binding: ActivityLogBinding
  private val viewModel by viewModels<LogViewModel>()
  private val adapter = LogsAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLogBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.topAppBar.setNavigationOnClickListener { finish() }
    binding.logsView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    binding.logsView.adapter = adapter
    lifecycleScope.launchWhenStarted {
      viewModel.logs.collectLatest {
        updateLogsView(it)
      }
    }
    viewModel.loadLogs()

  }

  private fun updateLogsView(logs: List<LogModel>) {
    adapter.update(logs)
  }

}

private class LogsAdapter : RecyclerView.Adapter<LogsAdapter.VH>() {

  private var datas: List<LogModel> = emptyList()

  fun update(data: List<LogModel>) {
    datas = data
    notifyItemMoved(0, datas.size - 1)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val binding = AdapterLogsBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    )
    return VH(binding)
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.updateItemView(datas[position])
  }

  override fun getItemCount(): Int {
    return datas.size
  }


  class VH(val binding: AdapterLogsBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun updateItemView(data: LogModel) {
      binding.tvTag.text = data.tag
      binding.tvLevel.text = data.level
      binding.tvDate.text = data.date
      binding.tvMsg.text = data.msg
    }
  }
}

data class LogModel(val tag: String, val level: String, val date: String, val msg: String)