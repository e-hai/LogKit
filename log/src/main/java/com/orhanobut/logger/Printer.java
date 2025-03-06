package com.orhanobut.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 一个代理接口，用于启用额外的操作。
 * 包含所有可能的日志消息使用方式。
 */
 interface Printer {

  /**
   * 添加日志适配器
   *
   * @param adapter 日志适配器
   */
  void addAdapter(@NonNull LogAdapter adapter);

  /**
   * 设置日志的临时标签
   *
   * @param tag 自定义标签，可以为 null
   * @return 当前的打印器实例
   */
  Printer t(@Nullable String tag);

  /**
   * 打印调试级别的日志
   *
   * @param message 日志内容
   * @param args 可选的格式化参数
   */
  void d(@NonNull String message, @Nullable Object... args);

  /**
   * 打印调试级别的对象
   *
   * @param object 要打印的对象，可以为 null
   */
  void d(@Nullable Object object);

  /**
   * 打印错误级别的日志
   *
   * @param message 日志内容
   * @param args 可选的格式化参数
   */
  void e(@NonNull String message, @Nullable Object... args);

  /**
   * 打印错误级别的日志，并附加异常信息
   *
   * @param throwable 异常信息
   * @param message 日志内容
   * @param args 可选的格式化参数
   */
  void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args);

  /**
   * 打印警告级别的日志
   *
   * @param message 日志内容
   * @param args 可选的格式化参数
   */
  void w(@NonNull String message, @Nullable Object... args);

  /**
   * 打印信息级别的日志
   *
   * @param message 日志内容
   * @param args 可选的格式化参数
   */
  void i(@NonNull String message, @Nullable Object... args);

  /**
   * 打印详细级别的日志
   *
   * @param message 日志内容
   * @param args 可选的格式化参数
   */
  void v(@NonNull String message, @Nullable Object... args);

  /**
   * 打印严重错误级别的日志
   *
   * @param message 日志内容
   * @param args 可选的格式化参数
   */
  void wtf(@NonNull String message, @Nullable Object... args);

  /**
   * 格式化并打印 JSON 内容
   *
   * @param json JSON 字符串内容，可以为 null
   */
  void json(@Nullable String json);

  /**
   * 格式化并打印 XML 内容
   *
   * @param xml XML 字符串内容，可以为 null
   */
  void xml(@Nullable String xml);

  /**
   * 通用日志方法，接受所有配置作为参数
   *
   * @param priority 日志优先级
   * @param tag 日志标签，可以为 null
   * @param message 日志消息，可以为 null
   * @param throwable 异常信息，可以为 null
   */
  void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable);

  /**
   * 清除所有日志适配器
   */
  void clearLogAdapters();
}

