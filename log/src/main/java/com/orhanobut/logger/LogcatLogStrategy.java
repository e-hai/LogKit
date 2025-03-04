package com.orhanobut.logger;

import static com.orhanobut.logger.Utils.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;


/**
 * LogCat 实现类，符合 {@link LogStrategy} 接口。
 *
 * 该类通过标准的 {@link Log} 类将所有日志打印到 Logcat。
 */
public class LogcatLogStrategy implements LogStrategy {

  static final String DEFAULT_TAG = "NO_TAG"; // 默认的日志标签

  /**
   * 记录日志到 Logcat。
   *
   * @param priority 日志优先级（如 DEBUG、INFO 等）
   * @param tag 日志标签，如果为 null 则使用默认标签
   * @param message 日志内容
   */
  @Override public void log(int priority, @Nullable String tag, @NonNull String message) {
    checkNotNull(message);

    // 如果 tag 为 null，则使用默认标签
    if (tag == null) {
      tag = DEFAULT_TAG;
    }

    // 使用 Log 类的 println 方法打印日志
    Log.println(priority, tag, message);
  }
}

