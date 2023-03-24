package com.orhanobut.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.orhanobut.logger.*
import com.orhanobut.logger.Logger.addLogAdapter
import com.orhanobut.logger.Logger.d
import com.orhanobut.logger.Logger.clearLogAdapters
import com.orhanobut.logger.PrettyFormatStrategy.Companion.newBuilder
import com.orhanobut.logger.Logger.w
import com.orhanobut.logger.Logger.i
import com.orhanobut.logger.Logger.t
import com.orhanobut.logger.Logger.json
import com.orhanobut.logger.ui.LogActivity
import java.util.*

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

    clearLogAdapters()
    addLogAdapter(AndroidLogAdapter())
    val csv = JsonFormatStrategy.newBuilder()
      .build(filesDir?.absolutePath ?: "")
    addLogAdapter(object : DiskLogAdapter(csv) {
    })
    d("my log message with JsonFormatStrategy")
    for (i in 0..100) {
      d("my log message $i")
    }
    startActivity(Intent(this, LogActivity::class.java))
  }
}