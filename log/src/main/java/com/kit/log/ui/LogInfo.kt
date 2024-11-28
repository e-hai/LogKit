package com.kit.log.ui

data class LogInfo(
  val date: Long,         //时间戳
  val dateString: String, //时间
  val priority: String,      //日志级别
  val tag: String,        //标签
  val message: String     //内容
)