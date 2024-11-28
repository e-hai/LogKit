package com.orhanobut.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Arrays;

import static com.orhanobut.logger.Logger.ASSERT;
import static com.orhanobut.logger.Logger.DEBUG;
import static com.orhanobut.logger.Logger.ERROR;
import static com.orhanobut.logger.Logger.INFO;
import static com.orhanobut.logger.Logger.UNKNOWN;
import static com.orhanobut.logger.Logger.VERBOSE;
import static com.orhanobut.logger.Logger.WARN;

/**
 * 提供一些常见操作的便利方法
 */
public class Utils {

  // 私有构造函数，隐藏工具类的实例化
  private Utils() {
  }

  /**
   * 判断字符串是否为 null 或长度为 0。
   *
   * @param str 要检查的字符串
   * @return 如果字符串为 null 或长度为 0，返回 true
   */
  static boolean isEmpty(CharSequence str) {
    return str == null || str.length() == 0;
  }

  /**
   * 判断两个 CharSequence 是否相等，包括它们同时为 null 的情况。
   *
   * <p><i>注意：在 1.1 及更早的版本中，该方法仅对 String 类型的参数有效。</i></p>
   *
   * @param a 第一个要比较的 CharSequence
   * @param b 第二个要比较的 CharSequence
   * @return 如果 a 和 b 相等，返回 true
   */
  static boolean equals(CharSequence a, CharSequence b) {
    if (a == b) return true; // 同一引用，直接返回 true
    if (a != null && b != null) {
      int length = a.length();
      if (length == b.length()) {
        if (a instanceof String && b instanceof String) {
          return a.equals(b); // String 类型直接使用 equals 比较
        } else {
          for (int i = 0; i < length; i++) { // 按字符逐一比较
            if (a.charAt(i) != b.charAt(i)) return false;
          }
          return true;
        }
      }
    }
    return false; // 不相等的情况
  }

  /**
   * 从异常对象中获取堆栈跟踪信息。
   *
   * <p>方法拷贝自 "android.util.Log.getStackTraceString()"，避免在单元测试中依赖 Android 堆栈。</p>
   *
   * @param tr 要处理的异常对象
   * @return 异常堆栈的字符串形式
   */
  static String getStackTraceString(Throwable tr) {
    if (tr == null) {
      return ""; // 异常为空时返回空字符串
    }

    // 如果异常是网络未连接的常见情况，减少日志输出
    Throwable t = tr;
    while (t != null) {
      if (t instanceof UnknownHostException) {
        return ""; // 网络异常直接返回空字符串
      }
      t = t.getCause(); // 获取下一层异常
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    tr.printStackTrace(pw); // 将堆栈信息写入字符串
    pw.flush();
    return sw.toString();
  }

  /**
   * 根据日志级别值返回对应的字符串表示。
   *
   * @param value 日志级别值
   * @return 日志级别的字符串表示
   */
  public static String logLevel(int value) {
    switch (value) {
      case VERBOSE:
        return "VERBOSE";
      case DEBUG:
        return "DEBUG";
      case INFO:
        return "INFO";
      case WARN:
        return "WARN";
      case ERROR:
        return "ERROR";
      case ASSERT:
        return "ASSERT";
      default:
        return "UNKNOWN";
    }
  }

  public static int logLevel(String value) {
    switch (value) {
      case "VERBOSE":
        return VERBOSE;
      case "DEBUG":
        return DEBUG;
      case "INFO":
        return INFO;
      case "WARN":
        return WARN;
      case "ERROR":
        return ERROR;
      case "ASSERT":
        return ASSERT;
      default:
        return UNKNOWN;
    }
  }

  /**
   * 将对象转换为字符串表示。
   *
   * @param object 要转换的对象
   * @return 对象的字符串表示
   */
  public static String toString(Object object) {
    if (object == null) {
      return "null"; // 对象为 null 时返回 "null"
    }
    if (!object.getClass().isArray()) {
      return object.toString(); // 非数组类型直接调用 toString()
    }
    // 根据数组类型调用对应的转换方法
    if (object instanceof boolean[]) {
      return Arrays.toString((boolean[]) object);
    }
    if (object instanceof byte[]) {
      return Arrays.toString((byte[]) object);
    }
    if (object instanceof char[]) {
      return Arrays.toString((char[]) object);
    }
    if (object instanceof short[]) {
      return Arrays.toString((short[]) object);
    }
    if (object instanceof int[]) {
      return Arrays.toString((int[]) object);
    }
    if (object instanceof long[]) {
      return Arrays.toString((long[]) object);
    }
    if (object instanceof float[]) {
      return Arrays.toString((float[]) object);
    }
    if (object instanceof double[]) {
      return Arrays.toString((double[]) object);
    }
    if (object instanceof Object[]) {
      return Arrays.deepToString((Object[]) object);
    }
    return "Couldn't find a correct type for the object"; // 未知类型
  }

  /**
   * 检查对象是否为 null。如果为 null，抛出 NullPointerException。
   *
   * @param obj 要检查的对象
   * @param <T> 对象的类型
   * @return 检查后的对象
   */
  @NonNull static <T> T checkNotNull(@Nullable final T obj) {
    if (obj == null) {
      throw new NullPointerException(); // 对象为 null 时抛出异常
    }
    return obj;
  }
}

