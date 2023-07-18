package com.orhanobut.logger.format.strategy

import com.orhanobut.logger.Logger.DEFAULT_TAG
import com.orhanobut.logger.Utils
import com.orhanobut.logger.log.strategy.LogStrategy
import com.orhanobut.logger.log.strategy.LogcatLogStrategy
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * CSV formatted file logging for Android.
 * Writes to CSV the following data:
 * epoch timestamp, ISO8601 timestamp (human-readable), log level, tag, log message.
 */

const val JSON_NAME_DATE = "date"
const val JSON_NAME_LEVEL = "level"
const val JSON_NAME_TAG = "tag"
const val JSON_NAME_MSG = "msg"

class JsonFormatStrategy private constructor(builder: Builder) : FormatStrategy {
  private val date: Date
  private val dateFormat: SimpleDateFormat
  private val logStrategy: LogStrategy
  private val tag: String?

  init {
    Utils.checkNotNull(builder)
    date = builder.date
    dateFormat = builder.dateFormat
    logStrategy = builder.logStrategy
    tag = builder.tag
  }

  override fun log(priority: Int, onceOnlyTag: String?, msg: String) {
    var message = msg
    Utils.checkNotNull(message)
    val tag = formatTag(onceOnlyTag)
    date.time = System.currentTimeMillis()

    // message
    if (message.contains(NEW_LINE)) {
      // a new line would break the CSV format, so we replace it here
      message = message.replace(NEW_LINE.toRegex(), NEW_LINE_REPLACEMENT)
    }

    val rootJson = JSONObject()
    rootJson.put(JSON_NAME_TAG, tag)
    rootJson.put(JSON_NAME_LEVEL, Utils.logLevel(priority))
    rootJson.put(JSON_NAME_DATE, dateFormat.format(date))
    rootJson.put(JSON_NAME_MSG, message)
    val msg = rootJson.toString() + NEW_LINE
    logStrategy.log(priority, tag, msg)
  }

  private fun formatTag(tag: String?): String? {
    return if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
      tag
    } else {
      this.tag
    }
  }

  class Builder {
    var date: Date
      private set

    var dateFormat: SimpleDateFormat
      private set

    var logStrategy: LogStrategy
      private set

    var tag: String = DEFAULT_TAG
      private set

    init {
      date = Date(System.currentTimeMillis())
      dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.getDefault())
      logStrategy = LogcatLogStrategy()
    }

    fun date(data: Date): Builder {
      this.date = data
      return this
    }

    fun dateFormat(dateFormat: SimpleDateFormat): Builder {
      this.dateFormat = dateFormat
      return this
    }

    fun logStrategy(logStrategy: LogStrategy): Builder {
      this.logStrategy = logStrategy
      return this
    }

    fun tag(tag: String): Builder {
      this.tag = tag
      return this
    }

    fun build(): JsonFormatStrategy {
      return JsonFormatStrategy(this)
    }
  }

  companion object {
    private val NEW_LINE = System.getProperty("line.separator")
    private const val NEW_LINE_REPLACEMENT = " <br> "
    private const val SEPARATOR = ","
    @JvmStatic fun newBuilder(): Builder {
      return Builder()
    }
  }
}