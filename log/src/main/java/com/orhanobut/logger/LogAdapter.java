package com.orhanobut.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 提供一个通用接口，用于输出日志。Logger 必须遵循该契约。
 *
 * @see AndroidLogAdapter
 * @see DiskLogAdapter
 */
 interface LogAdapter {

  /**
   * 用于判断日志是否需要输出。
   *
   * @param priority 日志级别，例如 DEBUG、WARNING
   * @param tag 日志消息的标签
   *
   * @return 是否需要输出日志。如果返回 true，则会输出日志；否则日志会被忽略。
   */
  boolean isLoggable(int priority, @Nullable String tag);

  /**
   * 每条日志都会通过这个管道进行输出。
   *
   * @param priority 日志级别，例如 DEBUG、WARNING
   * @param tag 日志消息的标签
   * @param message 日志消息的具体内容
   */
  void log(int priority, @Nullable String tag, @NonNull String message);
}
