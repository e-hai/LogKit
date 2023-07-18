package com.orhanobut.logger.adapter

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import com.orhanobut.logger.Utils
import com.orhanobut.logger.format.strategy.CsvFormatStrategy.Companion.newBuilder
import com.orhanobut.logger.format.strategy.FormatStrategy
import com.orhanobut.logger.format.strategy.JsonFormatStrategy
import com.orhanobut.logger.log.strategy.DiskLogStrategy
import java.io.File

/**
 * This is used to saves log messages to the disk.
 * By default it uses [JsonFormatStrategy] to translates text message into Json format.
 */
class DiskLogAdapter : LogAdapter {
  private val formatStrategy: FormatStrategy

  constructor(context: Context) {
    val folder = getLogFolder(context)
    val ht = HandlerThread("AndroidFileLogger.$folder")
    ht.start()
    val handler: Handler = DiskLogStrategy.WriteHandler(ht.looper, folder, MAX_BYTES)
    val logStrategy = DiskLogStrategy(handler)
    formatStrategy = JsonFormatStrategy.newBuilder()
      .logStrategy(logStrategy)
      .build()
  }

  constructor(diskPath: String) {
    formatStrategy = newBuilder().build(diskPath)
  }

  constructor(formatStrategy: FormatStrategy) {
    this.formatStrategy = Utils.checkNotNull(formatStrategy)
  }

  override fun isLoggable(priority: Int, tag: String?): Boolean {
    return true
  }

  override fun log(priority: Int, tag: String?, message: String) {
    formatStrategy.log(priority, tag, message)
  }

  fun getLogFolder(context: Context): String {
    return context.filesDir.absolutePath + File.separatorChar + "logger"
  }

  companion object {
    const val MAX_BYTES = 500 * 1024 // 500K averages to a 4000 lines per file
  }
}