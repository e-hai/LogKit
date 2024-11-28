package com.orhanobut.logger;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.orhanobut.logger.Utils.checkNotNull;

/**
 * 用于 Android 的 CSV 格式日志记录。
 * 将以下数据写入 CSV 文件：
 * 1. 时间戳（epoch 时间戳）
 * 2. ISO8601 时间戳（人类可读格式）
 * 3. 日志级别
 * 4. 标签
 * 5. 日志消息
 */
public class CsvFormatStrategy implements FormatStrategy {

  private static final String NEW_LINE = System.getProperty("line.separator");
  private static final String NEW_LINE_REPLACEMENT = " <br> ";
  private static final String SEPARATOR = ",";

  @NonNull private final Date date; // 当前时间
  @NonNull private final SimpleDateFormat dateFormat; // 格式化日期
  @NonNull private final LogStrategy logStrategy; // 日志策略
  @Nullable private final String tag; // 默认标签

  private CsvFormatStrategy(@NonNull Builder builder) {
    checkNotNull(builder);

    date = builder.date;
    dateFormat = builder.dateFormat;
    logStrategy = builder.logStrategy;
    tag = builder.tag;
  }

  /**
   * 创建一个新的构建器实例。
   *
   * @return Builder 实例
   */
  @NonNull public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * 输出日志信息。
   *
   * @param priority    日志级别
   * @param onceOnlyTag 临时标签
   * @param message     日志消息
   */
  @Override public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {
    checkNotNull(message);

    // 格式化标签
    String tag = formatTag(onceOnlyTag);

    // 设置当前时间
    date.setTime(System.currentTimeMillis());

    StringBuilder builder = new StringBuilder();

    // 机器可读的时间戳
    builder.append(Long.toString(date.getTime()));

    // 人类可读的时间戳
    builder.append(SEPARATOR);
    builder.append(dateFormat.format(date));

    // 日志级别
    builder.append(SEPARATOR);
    builder.append(Utils.logLevel(priority));

    // 标签
    builder.append(SEPARATOR);
    builder.append(tag);

    // 日志消息（处理换行符以保持 CSV 格式）
    if (message.contains(NEW_LINE)) {
      message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
    }
    builder.append(SEPARATOR);
    builder.append(message);

    // 添加换行符
    builder.append(NEW_LINE);

    // 使用日志策略写入日志
    logStrategy.log(priority, tag, builder.toString());
  }

  /**
   * 格式化日志标签。
   *
   * @param tag 临时标签
   * @return 格式化后的标签
   */
  @Nullable private String formatTag(@Nullable String tag) {
    if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
      return tag;
    }
    return this.tag;
  }

  /**
   * 构建 CsvFormatStrategy 的 Builder。
   */
  public static final class Builder {
    private static final int MAX_BYTES = 500 * 1024; // 每个文件最大 500KB，约 4000 行

    Date date; // 当前时间
    SimpleDateFormat dateFormat; // 日期格式化
    LogStrategy logStrategy; // 日志策略
    String tag = "PRETTY_LOGGER"; // 默认标签

    private Builder() {
    }

    @NonNull public Builder date(@Nullable Date val) {
      date = val;
      return this;
    }

    @NonNull public Builder dateFormat(@Nullable SimpleDateFormat val) {
      dateFormat = val;
      return this;
    }

    @NonNull public Builder logStrategy(@Nullable LogStrategy val) {
      logStrategy = val;
      return this;
    }

    @NonNull public Builder tag(@Nullable String tag) {
      this.tag = tag;
      return this;
    }

    /**
     * 构建 CsvFormatStrategy 实例。
     * 如果未提供参数，则使用默认值。
     *
     * @return CsvFormatStrategy 实例
     */
    @NonNull public CsvFormatStrategy build() {
      if (date == null) {
        date = new Date();
      }
      if (dateFormat == null) {
        dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.UK);
      }
      if (logStrategy == null) {
        String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folder = diskPath + File.separatorChar + "logger";

        HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
        ht.start();
        Handler handler = new DiskLogStrategy.WriteHandler(ht.getLooper(), folder, MAX_BYTES);
        logStrategy = new DiskLogStrategy(handler);
      }
      return new CsvFormatStrategy(this);
    }
  }
}

