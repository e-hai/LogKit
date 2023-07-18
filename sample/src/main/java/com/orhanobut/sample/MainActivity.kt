package com.orhanobut.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.orhanobut.logger.adapter.AndroidLogAdapter
import com.orhanobut.logger.adapter.DiskLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.ui.LogActivity

class MainActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
//    Log.d("Tag", "I'm a log which you don't see easily, hehe")
//    Log.d("json content", "{ \"key\": 3, \n \"value\": something}")
//    Log.d("error", "There is a crash somewhere or any warning")
//    addLogAdapter(AndroidLogAdapter())
//    d("message")
//    clearLogAdapters()
//    var formatStrategy: FormatStrategy = newBuilder()
//      .showThreadInfo(false) // (Optional) Whether to show thread info or not. Default true
//      .methodCount(0) // (Optional) How many method line to show. Default 2
//      .methodOffset(3) // (Optional) Skips some method invokes in stack trace. Default 5
//      //        .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
//      .tag("My custom tag") // (Optional) Custom tag for each log. Default PRETTY_LOGGER
//      .build()
//    addLogAdapter(AndroidLogAdapter(formatStrategy))
//    addLogAdapter(object : AndroidLogAdapter() {
//      override fun isLoggable(priority: Int, tag: String?): Boolean {
//        return BuildConfig.DEBUG
//      }
//    })
//    addLogAdapter(DiskLogAdapter(filesDir.absolutePath))
//    w("no thread info and only 1 method")
//    clearLogAdapters()
//    formatStrategy = newBuilder()
//      .showThreadInfo(false)
//      .methodCount(0)
//      .build()
//    addLogAdapter(AndroidLogAdapter(formatStrategy))
//    i("no thread info and method info")
//    t("tag").e("Custom tag for only one use")
//    json("{ \"key\": 3, \"value\": something}")
//    d(listOf("foo", "bar"))
//    val map: MutableMap<String, String> = HashMap()
//    map["key"] = "value"
//    map["key1"] = "value2"
//    d(map)
//    clearLogAdapters()
//    formatStrategy = newBuilder()
//      .showThreadInfo(false)
//      .methodCount(0)
//      .tag("MyTag")
//      .build()
//    addLogAdapter(AndroidLogAdapter(formatStrategy))
//    w("my log message with my tag")

    Logger.clearLogAdapters()
    Logger.addLogAdapter(AndroidLogAdapter())
    Logger.t("NEW-TAG").v("my log message with JsonFormatStrategy")
    Logger.v("输出所有信息 verbose my log message")
    Logger.d("输出调试信息 debug my log message")
    Logger.i("输出必要信息 info my log message")
    Logger.w("输出警告信息 warn my log message")
    Logger.e("输出错误信息 error my log message")

    startActivity(Intent(this, LogActivity::class.java))
  }
}