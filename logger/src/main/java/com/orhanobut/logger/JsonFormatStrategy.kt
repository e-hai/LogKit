package com.orhanobut.logger

import android.os.Handler
import android.os.HandlerThread
import org.json.JSONObject
import java.io.File
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
  override fun log(priority: Int, onceOnlyTag: String?, message: String) {
    var message = message
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
      this.tag + "-" + tag
    } else this.tag
  }

  class Builder {
    var date: Date? = null
    var dateFormat: SimpleDateFormat? = null
    var logStrategy: LogStrategy? = null
    var tag: String? = "PRETTY_LOGGER"

    fun date(data: Date?): Builder {
      this.date = data
      return this
    }

    fun dateFormat(dateFormat: SimpleDateFormat?): Builder {
      this.dateFormat = dateFormat
      return this
    }

    fun logStrategy(logStrategy: LogStrategy?): Builder {
      this.logStrategy = logStrategy
      return this
    }

    fun tag(tag: String?): Builder {
      this.tag = tag
      return this
    }

    fun build(diskPath: String): JsonFormatStrategy {
      if (date == null) {
        date = Date()
      }
      if (dateFormat == null) {
        dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.getDefault())
      }
      if (logStrategy == null) {
        val folder = diskPath + File.separatorChar + "logger"
        val ht = HandlerThread("AndroidFileLogger.$folder")
        ht.start()
        val handler: Handler = DiskLogStrategy.WriteHandler(ht.looper, folder, MAX_BYTES)
        logStrategy = DiskLogStrategy(handler)
      }
      return JsonFormatStrategy(this)
    }

    companion object {
      private const val MAX_BYTES = 500 * 1024 // 500K averages to a 4000 lines per file
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

  init {
    Utils.checkNotNull(builder)
    date = builder.date!!
    dateFormat = builder.dateFormat!!
    logStrategy = builder.logStrategy!!
    tag = builder.tag
  }
}