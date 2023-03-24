package com.orhanobut.logger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject

class LogViewModel : ViewModel() {
  val logs = MutableStateFlow(emptyList<LogModel>())

  fun loadLogs() {
    viewModelScope.launch {
      flowOf(DiskLogStrategy.readLog())
        .map {
          val logModelList = mutableListOf<LogModel>()
          it.forEach { item ->
            val rootOb = JSONObject(item)
            val model = LogModel(
              rootOb.getString(JSON_NAME_TAG),
              rootOb.getString(JSON_NAME_LEVEL),
              rootOb.getString(JSON_NAME_DATE),
              rootOb.getString(JSON_NAME_MSG)
            )
            logModelList.add(model)
          }
          return@map logModelList
        }
        .catch { t -> t.printStackTrace() }
        .flowOn(Dispatchers.IO)
        .collectLatest {
          logs.emit(it)
        }
    }
  }
}