package com.orhanobut.logger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.adapter.AndroidLogAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LogViewModel : ViewModel() {
  //标签列表
  val tags: MutableStateFlow<List<String>> = MutableStateFlow(listOf(TAG_ALL))

  //全部Log
  val logs: MutableStateFlow<List<AndroidLogAdapter.CacheInfo>> = MutableStateFlow(emptyList())

  init {
    loadLogs()
  }

  fun loadLogs(tag: String = TAG_ALL) {
    viewModelScope.launch {
      flowOf(AndroidLogAdapter.cacheList)
        .map { infoList ->
          if (tag == TAG_ALL) {
            //返回全部log
            return@map infoList
          } else {
            //返回符合tag的log
            return@map infoList.filter {
              it.tag == tag
            }
          }
        }
        .flowOn(Dispatchers.IO)
        .onEach { result ->
          logs.emit(result)
        }
        .catch { t -> t.printStackTrace() }
        .collect()
    }
  }

  fun loadTags() {
    viewModelScope.launch {
      flowOf(AndroidLogAdapter.cacheList)
        .map { infoList ->
          return@map tagFilter(infoList)
        }
        .flowOn(Dispatchers.IO)
        .onEach {
          tags.emit(it)
        }
        .catch { t -> t.printStackTrace() }
        .collect()
    }
  }

  private fun tagFilter(infoList: List<AndroidLogAdapter.CacheInfo>): List<String> {
    val result = infoList.map {
      it.tag
    }.distinct()
      .sorted()
      .toMutableList()
      .apply {
        add(0, TAG_ALL)
      }
    return result
  }

  companion object {
    const val TAG_ALL = "ALL TAG"
  }
}