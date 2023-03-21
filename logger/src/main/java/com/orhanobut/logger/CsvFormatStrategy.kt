package com.orhanobut.logger

import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import com.orhanobut.logger.DiskLogStrategy.WriteHandler
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * CSV formatted file logging for Android.
 * Writes to CSV the following data:
 * epoch timestamp, ISO8601 timestamp (human-readable), log level, tag, log message.
 */
class CsvFormatStrategy private constructor(builder: Builder) : FormatStrategy {
  private val date: Date
  private val dateFormat: SimpleDateFormat
  private val logStrategy: LogStrategy
  private val tag: String?
  override fun log(priority: Int, onceOnlyTag: String?, message: String) {
    var message = message
    Utils.checkNotNull(message)
    val tag = formatTag(onceOnlyTag)
    date.time = System.currentTimeMillis()
    val builder = StringBuilder()

    // machine-readable date/time
    builder.append(date.time.toString())

    // human-readable date/time
    builder.append(SEPARATOR)
    builder.append(dateFormat.format(date))

    // level
    builder.append(SEPARATOR)
    builder.append(Utils.logLevel(priority))

    // tag
    builder.append(SEPARATOR)
    builder.append(tag)

    // message
    if (message.contains(NEW_LINE)) {
      // a new line would break the CSV format, so we replace it here
      message = message.replace(NEW_LINE.toRegex(), NEW_LINE_REPLACEMENT)
    }
    builder.append(SEPARATOR)
    builder.append(message)

    // new line
    builder.append(NEW_LINE)
    logStrategy.log(priority, tag, builder.toString())
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

    fun build(diskPath: String): CsvFormatStrategy {
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
        val handler: Handler = WriteHandler(ht.looper, folder, MAX_BYTES)
        logStrategy = DiskLogStrategy(handler)
      }
      return CsvFormatStrategy(this)
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