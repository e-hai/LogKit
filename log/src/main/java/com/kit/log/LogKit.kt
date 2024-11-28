package com.kit.log

import android.app.Activity
import android.content.Context
import com.kit.log.ui.LogActivity.Companion.startActivity
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.CsvFormatStrategy
import com.orhanobut.logger.DiskLogAdapter
import com.orhanobut.logger.DiskLogWriteReadStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.Printer

object LogKit {

  fun initOnlyAndroidLog() {
    Logger.clearLogAdapters()
    Logger.addLogAdapter(AndroidLogAdapter())
  }

  fun initOnlyDiskLog() {
    Logger.clearLogAdapters()
    Logger.addLogAdapter(DiskLogAdapter())
  }

  private var diskLogWriteReadStrategy: DiskLogWriteReadStrategy? = null

  fun initAllLog(context: Context) {
    Logger.clearLogAdapters()
    Logger.addLogAdapter(AndroidLogAdapter())
    diskLogWriteReadStrategy = DiskLogWriteReadStrategy.build(context)
    val csvFormatStrategy = CsvFormatStrategy.newBuilder()
      .logStrategy(diskLogWriteReadStrategy)
      .build()
    Logger.addLogAdapter(DiskLogAdapter(csvFormatStrategy))
  }

  fun showLogUi(activity: Activity) {
    startActivity(activity)
  }

  fun readLog(callback: (List<String>) -> Unit) {
    diskLogWriteReadStrategy?.readLog {
      callback.invoke(it)
    }
  }

  /**
   * 打印调试级别的日志信息
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  fun d(tag: String, message: String, vararg args: Any?) {
    Logger.t(tag).d(message, *args)
  }

  /**
   * 打印错误级别的日志信息
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  fun e(tag: String, message: String, vararg args: Any?) {
    Logger.t(tag).e(null, message, *args)
  }

  /**
   * 打印错误级别的日志信息，并附带异常
   *
   * @param throwable 异常信息
   * @param message   日志内容
   * @param args      可选的参数
   */
  fun e(tag: String, throwable: Throwable?, message: String, vararg args: Any?) {
    Logger.t(tag).e(throwable, message, *args)
  }

  /**
   * 打印信息级别的日志
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  fun i(tag: String, message: String, vararg args: Any?) {
    Logger.t(tag).i(message, *args)
  }

  /**
   * 打印详细级别的日志
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  fun v(tag: String, message: String, vararg args: Any?) {
    Logger.t(tag).v(message, *args)
  }

  /**
   * 打印警告级别的日志
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  fun w(tag: String, message: String, vararg args: Any?) {
    Logger.t(tag).w(message, *args)
  }

  /**
   * 用于处理异常情况的日志
   * 比如：不可预料的错误等
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  fun wtf(tag: String, message: String, vararg args: Any?) {
    Logger.t(tag).wtf(message, *args)
  }

  /**
   * 格式化并打印给定的 JSON 内容
   *
   * @param json JSON 内容
   */
  fun json(tag: String, json: String?) {
    Logger.t(tag).json(json)
  }

  /**
   * 格式化并打印给定的 XML 内容
   *
   * @param xml XML 内容
   */
  fun xml(tag: String, xml: String?) {
    Logger.t(tag).xml(xml)
  }
}