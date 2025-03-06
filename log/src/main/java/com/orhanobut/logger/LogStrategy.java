package com.orhanobut.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 确定日志的目标输出，例如磁盘、Logcat 等。
 *
 * @see LogcatLogStrategy 用于将日志输出到 Logcat
 * @see DiskLogStrategy 用于将日志输出到磁盘
 */
 interface LogStrategy {

  /**
   * 该方法由 Logger 在每次处理日志消息时调用。
   * 可以将此方法视为整个日志处理流程的最终输出目标。
   *
   * @param priority 日志级别，例如 DEBUG、WARNING
   * @param tag 日志消息的标签
   * @param message 日志消息的具体内容
   */
  void log(int priority, @Nullable String tag, @NonNull String message);
}

