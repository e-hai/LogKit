package com.orhanobut.logger.log.strategy

import android.util.Log
import com.orhanobut.logger.Logger.DEFAULT_TAG
import java.text.SimpleDateFormat
import java.util.*

/**
 * LogCat implementation for [LogStrategy]
 *
 * This simply prints out all logs to Logcat by using standard [Log] class.
 *
 * 使用Android自带的Log来打印
 */
class LogcatLogStrategy : LogStrategy {
  override fun log(priority: Int, tag: String?, message: String) {
    var newTag = tag
    if (newTag == null) {
      newTag = DEFAULT_TAG
    }
    Log.println(priority, newTag, message)
  }
}