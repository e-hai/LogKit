package com.orhanobut.logger;

import static com.orhanobut.logger.Utils.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



/**
 * <pre>
 *  ┌────────────────────────────────────────────
 *  │ LOGGER
 *  ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 *  │ Standard logging mechanism
 *  ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 *  │ But more pretty, simple and powerful
 *  └────────────────────────────────────────────
 * </pre>
 *
 * <h3>How to use it</h3>
 * Initialize it first
 * <pre><code>
 *   Logger.addLogAdapter(new AndroidLogAdapter());
 * </code></pre>
 * <p>
 * And use the appropriate static Logger methods.
 *
 * <pre><code>
 *   Logger.d("debug");
 *   Logger.e("error");
 *   Logger.w("warning");
 *   Logger.v("verbose");
 *   Logger.i("information");
 *   Logger.wtf("What a Terrible Failure");
 * </code></pre>
 *
 * <h3>String format arguments are supported</h3>
 * <pre><code>
 *   Logger.d("hello %s", "world");
 * </code></pre>
 *
 * <h3>Collections are support ed(only available for debug logs)</h3>
 * <pre><code>
 *   Logger.d(MAP);
 *   Logger.d(SET);
 *   Logger.d(LIST);
 *   Logger.d(ARRAY);
 * </code></pre>
 *
 * <h3>Json and Xml support (output will be in debug level)</h3>
 * <pre><code>
 *   Logger.json(JSON_CONTENT);
 *   Logger.xml(XML_CONTENT);
 * </code></pre>
 *
 * <h3>Customize Logger</h3>
 * Based on your needs, you can change the following settings:
 * <ul>
 *   <li>Different {@link LogAdapter}</li>
 *   <li>Different {@link FormatStrategy}</li>
 *   <li>Different {@link LogStrategy}</li>
 * </ul>
 *
 * @see LogAdapter
 * @see FormatStrategy
 * @see LogStrategy
 */
public final class Logger {

  // 定义日志的优先级常量
  public static final int UNKNOWN = -1;

  public static final int VERBOSE = 2;

  public static final int DEBUG = 3;
  public static final int INFO = 4;
  public static final int WARN = 5;
  public static final int ERROR = 6;
  public static final int ASSERT = 7;

  @NonNull private static Printer printer = new LoggerPrinter(); // 默认打印机

  private Logger() {
    // 禁止实例化
  }

  /**
   * 设置自定义的 Printer
   *
   * @param printer 自定义打印机
   */
  public static void printer(@NonNull Printer printer) {
    Logger.printer = checkNotNull(printer);
  }

  /**
   * 添加日志适配器
   *
   * @param adapter 日志适配器
   */
  public static void addLogAdapter(@NonNull LogAdapter adapter) {
    printer.addAdapter(checkNotNull(adapter));
  }

  /**
   * 清除所有日志适配器
   */
  public static void clearLogAdapters() {
    printer.clearLogAdapters();
  }

  /**
   * 临时设置日志标签。这个标签只在当前方法调用中有效，之后会恢复为默认标签。
   *
   * @param tag 自定义日志标签
   * @return 打印机
   */
  public static Printer t(@Nullable String tag) {
    return printer.t(tag);
  }

  /**
   * 通用日志方法，接受所有配置作为参数
   *
   * @param priority  日志优先级
   * @param tag       日志标签
   * @param message   日志消息
   * @param throwable 可选的异常信息
   */
  public static void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
    printer.log(priority, tag, message, throwable);
  }

  /**
   * 打印调试级别的日志信息
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  public static void d(@NonNull String message, @Nullable Object... args) {
    printer.d(message, args);
  }

  /**
   * 打印调试级别的对象
   *
   * @param object 要打印的对象
   */
  public static void d(@Nullable Object object) {
    printer.d(object);
  }

  /**
   * 打印错误级别的日志信息
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  public static void e(@NonNull String message, @Nullable Object... args) {
    printer.e(null, message, args);
  }

  /**
   * 打印错误级别的日志信息，并附带异常
   *
   * @param throwable 异常信息
   * @param message   日志内容
   * @param args      可选的参数
   */
  public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
    printer.e(throwable, message, args);
  }

  /**
   * 打印信息级别的日志
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  public static void i(@NonNull String message, @Nullable Object... args) {
    printer.i(message, args);
  }

  /**
   * 打印详细级别的日志
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  public static void v(@NonNull String message, @Nullable Object... args) {
    printer.v(message, args);
  }

  /**
   * 打印警告级别的日志
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  public static void w(@NonNull String message, @Nullable Object... args) {
    printer.w(message, args);
  }

  /**
   * 用于处理异常情况的日志
   * 比如：不可预料的错误等
   *
   * @param message 日志内容
   * @param args    可选的参数
   */
  public static void wtf(@NonNull String message, @Nullable Object... args) {
    printer.wtf(message, args);
  }

  /**
   * 格式化并打印给定的 JSON 内容
   *
   * @param json JSON 内容
   */
  public static void json(@Nullable String json) {
    printer.json(json);
  }

  /**
   * 格式化并打印给定的 XML 内容
   *
   * @param xml XML 内容
   */
  public static void xml(@Nullable String xml) {
    printer.xml(xml);
  }
}
