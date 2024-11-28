package com.orhanobut.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.orhanobut.logger.Utils.checkNotNull;

/**
 * 用于将日志消息保存到磁盘。
 * 默认情况下，它使用 {@link CsvFormatStrategy} 将文本消息转换为 CSV 格式。
 */
public class DiskLogAdapter implements LogAdapter {

  @NonNull private final FormatStrategy formatStrategy;

  // 默认构造函数，使用 CsvFormatStrategy 格式化策略
  public DiskLogAdapter() {
    formatStrategy = CsvFormatStrategy.newBuilder().build();
  }

  // 构造函数，允许使用自定义的格式化策略
  public DiskLogAdapter(@NonNull FormatStrategy formatStrategy) {
    this.formatStrategy = checkNotNull(formatStrategy);
  }

  /**
   * 判断日志是否需要输出。
   *
   * @param priority 日志级别，例如 DEBUG、WARNING
   * @param tag 日志消息的标签
   * @return 是否需要输出日志，默认为始终输出 (返回 true)。
   */
  @Override
  public boolean isLoggable(int priority, @Nullable String tag) {
    return true;
  }

  /**
   * 根据格式化策略将日志保存到磁盘。
   *
   * @param priority 日志级别，例如 DEBUG、WARNING
   * @param tag 日志消息的标签
   * @param message 日志消息的具体内容
   */
  @Override
  public void log(int priority, @Nullable String tag, @NonNull String message) {
    formatStrategy.log(priority, tag, message);
  }

}

