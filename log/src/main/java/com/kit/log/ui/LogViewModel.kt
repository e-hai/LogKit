package com.kit.log.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kit.log.LogKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LogViewModel : ViewModel() {

  //log数据
  private val logs = mutableListOf<LogInfo>()

  //tag数据
  private val tags = linkedSetOf<String>()

  //需要展示的标签
  val tagMSF: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

  //需要展示的Log
  val logMSF: MutableStateFlow<List<LogInfo>> = MutableStateFlow(emptyList())

  init {
    LogKit.readLog { result ->
      logs.clear()
      tags.clear()
      tags.add(TAG_SHOW_ALL_LOG)
      result.forEach { line ->
        val oneLogLine = line.split(",")
        val oneLogInfo = LogInfo(
          oneLogLine[0].toLong(),
          oneLogLine[1],
          oneLogLine[2],
          oneLogLine[3],
          oneLogLine[4]
        )
        logs.add(oneLogInfo)
        tags.add(oneLogInfo.tag)
      }
      loadTag()
      loadLog()
    }
  }

  fun loadLog(filterTag: String = TAG_SHOW_ALL_LOG) {
    viewModelScope.launch(Dispatchers.IO) {
      if (filterTag == TAG_SHOW_ALL_LOG) {
        logMSF.emit(logs)
      } else {
        logMSF.emit(logs.filter { it.tag == filterTag })
      }
    }
  }

  fun loadTag() {
    viewModelScope.launch(Dispatchers.IO) {
      tagMSF.emit(tags.toList())
    }
  }


  companion object {
    const val TAG_SHOW_ALL_LOG = "ALL TAG"
  }
}