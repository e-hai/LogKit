package com.orhanobut.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 用于确定如何打印或保存日志消息。
 *
 * @see PrettyFormatStrategy
 * @see CsvFormatStrategy
 */
public interface FormatStrategy {

  /**
   * 记录日志信息。
   *
   * @param priority 日志优先级（如 DEBUG、INFO 等）
   * @param tag 日志标签
   * @param message 日志消息内容
   */
  void log(int priority, @Nullable String tag, @NonNull String message);
}

