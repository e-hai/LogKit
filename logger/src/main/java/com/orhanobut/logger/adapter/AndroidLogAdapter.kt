package com.orhanobut.logger.adapter

import com.orhanobut.logger.Logger
import com.orhanobut.logger.Utils
import com.orhanobut.logger.format.strategy.FormatStrategy
import com.orhanobut.logger.format.strategy.PrettyFormatStrategy
import com.orhanobut.logger.log.strategy.LogcatLogStrategy
import java.text.SimpleDateFormat
import java.util.*


/**
 * Android terminal log output implementation for [LogAdapter].
 *
 * Prints output to LogCat with pretty borders.
 *
 * <pre>
 * ┌──────────────────────────
 * │ Method stack history
 * ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 * │ Log message
 * └──────────────────────────
</pre> *
 */
class AndroidLogAdapter : LogAdapter {
  private val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.getDefault())

  private val formatStrategy: FormatStrategy

  constructor() {
    formatStrategy = PrettyFormatStrategy.newBuilder().build()
  }

  constructor(formatStrategy: FormatStrategy) {
    this.formatStrategy = Utils.checkNotNull(formatStrategy)
  }

  override fun isLoggable(priority: Int, tag: String?): Boolean {
    return true
  }

  override fun log(priority: Int, tag: String?, message: String) {
    formatStrategy.log(priority, tag, message)

    var newTag = tag
    if (newTag == null) {
      newTag = Logger.DEFAULT_TAG
    }
    if (cacheList.size > 200) {
      cacheList.removeLast()
    }
    val date = Date(System.currentTimeMillis())
    cacheList.add(
      0,
      CacheInfo(priority, newTag, message, dateFormat.format(date))
    )
  }


  companion object {
    val cacheList = mutableListOf<CacheInfo>()
  }

  data class CacheInfo(val priority: Int, val tag: String, val message: String, val date: String)
}