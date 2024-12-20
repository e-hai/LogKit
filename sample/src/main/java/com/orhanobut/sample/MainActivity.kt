package com.orhanobut.sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kit.log.LogKit

class MainActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    LogKit.initAllLog(this)
    for (i in 0..500){
      LogKit.v("A","hello %s", "world")
      LogKit.d("b","输出调试信息 debug my log message")
      LogKit.i("C","输出必要信息 info my log message")
      LogKit.w("d","输出警告信息 warn my log message")
      LogKit.e("E","输出错误信息 error my log message")
    }

    findViewById<View>(R.id.tv_view).post({
      LogKit.showLogUi(this)
      finish()
    })

  }

}