package com.orhanobut.logger.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.*
import com.orhanobut.logger.adapter.AndroidLogAdapter
import com.orhanobut.logger.format.strategy.JSON_NAME_DATE
import com.orhanobut.logger.format.strategy.JSON_NAME_LEVEL
import com.orhanobut.logger.format.strategy.JSON_NAME_MSG
import com.orhanobut.logger.format.strategy.JSON_NAME_TAG
import com.orhanobut.logger.log.strategy.DiskLogStrategy
import com.orhanobut.logger.log.strategy.LogcatLogStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject

class LogViewModel : ViewModel() {
  val tags: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
  val logs: MutableStateFlow<List<AndroidLogAdapter.CacheInfo>> = MutableStateFlow(emptyList())

  fun loadLogs() {
    viewModelScope.launch {
      flowOf(AndroidLogAdapter.cacheList)
        .flowOn(Dispatchers.IO)
        .onEach { result ->
          logs.emit(result)

          result.map {
            it.tag
          }.distinct().let {
            Log.d("YYY", "" + it.size)
            tags.emit(it)
          }
        }
        .catch { t -> t.printStackTrace() }
        .collect()
    }
  }
}