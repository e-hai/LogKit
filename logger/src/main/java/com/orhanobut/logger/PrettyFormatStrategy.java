package com.orhanobut.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.orhanobut.logger.Utils.checkNotNull;

/**
 * Draws borders around the given log message along with additional information such as :
 *
 * <ul>
 *   <li>Thread information</li>
 *   <li>Method stack trace</li>
 * </ul>
 *
 * <pre>
 *  ┌──────────────────────────
 *  │ Method stack history
 *  ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 *  │ Thread information
 *  ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 *  │ Log message
 *  └──────────────────────────
 * </pre>
 *
 * <h3>Customize</h3>
 * <pre><code>
 *   FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
 *       .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
 *       .methodCount(0)         // (Optional) How many method line to show. Default 2
 *       .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
 *       .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
 *       .tag("My custom tag")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
 *       .build();
 * </code></pre>
 */

/**
 * 一个实现了 FormatStrategy 接口的格式化日志输出策略，提供友好的日志输出格式。
 */
public class PrettyFormatStrategy implements FormatStrategy {

  /**
   * Android 日志单条记录的最大字节数限制约为 4076 字节，
   * 因此使用 4000 字节作为默认分块大小，避免超限。
   */
  private static final int CHUNK_SIZE = 4000;

  /**
   * 栈跟踪中最小的有效偏移量，从本类开始跳过两次本地调用。
   */
  private static final int MIN_STACK_OFFSET = 5;

  /**
   * 绘图工具：日志边框和分隔符。
   */
  private static final char TOP_LEFT_CORNER = '┌';
  private static final char BOTTOM_LEFT_CORNER = '└';
  private static final char MIDDLE_CORNER = '├';
  private static final char HORIZONTAL_LINE = '│';
  private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
  private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
  private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
  private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
  private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

  private final int methodCount; // 显示调用方法的数量
  private final int methodOffset; // 栈跟踪偏移量
  private final boolean showThreadInfo; // 是否显示线程信息
  @NonNull private final LogStrategy logStrategy; // 日志策略
  @Nullable private final String tag; // 默认日志标签

  /**
   * 构造函数，使用 Builder 初始化。
   */
  private PrettyFormatStrategy(@NonNull Builder builder) {
    checkNotNull(builder);

    methodCount = builder.methodCount;
    methodOffset = builder.methodOffset;
    showThreadInfo = builder.showThreadInfo;
    logStrategy = builder.logStrategy;
    tag = builder.tag;
  }

  /**
   * 创建 Builder 的静态方法。
   *
   * @return 返回一个 Builder 实例。
   */
  @NonNull public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {
    checkNotNull(message);

    String tag = formatTag(onceOnlyTag);

    // 输出日志边框和内容
    logTopBorder(priority, tag);
    logHeaderContent(priority, tag, methodCount);

    byte[] bytes = message.getBytes();
    int length = bytes.length;
    if (length <= CHUNK_SIZE) {
      if (methodCount > 0) {
        logDivider(priority, tag);
      }
      logContent(priority, tag, message);
      logBottomBorder(priority, tag);
      return;
    }
    if (methodCount > 0) {
      logDivider(priority, tag);
    }
    for (int i = 0; i < length; i += CHUNK_SIZE) {
      int count = Math.min(length - i, CHUNK_SIZE);
      logContent(priority, tag, new String(bytes, i, count));
    }
    logBottomBorder(priority, tag);
  }

  /**
   * 输出顶部边框。
   */
  private void logTopBorder(int logType, @Nullable String tag) {
    logChunk(logType, tag, TOP_BORDER);
  }

  /**
   * 输出日志头部内容，包括线程信息和方法调用堆栈。
   */
  private void logHeaderContent(int logType, @Nullable String tag, int methodCount) {
    StackTraceElement[] trace = Thread.currentThread().getStackTrace();
    if (showThreadInfo) {
      logChunk(logType, tag, HORIZONTAL_LINE + " Thread: " + Thread.currentThread().getName());
      logDivider(logType, tag);
    }
    String level = "";

    int stackOffset = getStackOffset(trace) + methodOffset;

    if (methodCount + stackOffset > trace.length) {
      methodCount = trace.length - stackOffset - 1;
    }

    for (int i = methodCount; i > 0; i--) {
      int stackIndex = i + stackOffset;
      if (stackIndex >= trace.length) {
        continue;
      }
      StringBuilder builder = new StringBuilder();
      builder.append(HORIZONTAL_LINE)
          .append(' ')
          .append(level)
          .append(getSimpleClassName(trace[stackIndex].getClassName()))
          .append(".")
          .append(trace[stackIndex].getMethodName())
          .append(" ")
          .append(" (")
          .append(trace[stackIndex].getFileName())
          .append(":")
          .append(trace[stackIndex].getLineNumber())
          .append(")");
      level += "   ";
      logChunk(logType, tag, builder.toString());
    }
  }

  /**
   * 输出底部边框。
   */
  private void logBottomBorder(int logType, @Nullable String tag) {
    logChunk(logType, tag, BOTTOM_BORDER);
  }

  /**
   * 输出分隔符。
   */
  private void logDivider(int logType, @Nullable String tag) {
    logChunk(logType, tag, MIDDLE_BORDER);
  }

  /**
   * 输出日志内容。
   */
  private void logContent(int logType, @Nullable String tag, @NonNull String chunk) {
    checkNotNull(chunk);

    String[] lines = chunk.split(System.getProperty("line.separator"));
    for (String line : lines) {
      logChunk(logType, tag, HORIZONTAL_LINE + " " + line);
    }
  }

  /**
   * 输出日志块。
   */
  private void logChunk(int priority, @Nullable String tag, @NonNull String chunk) {
    checkNotNull(chunk);

    logStrategy.log(priority, tag, chunk);
  }

  /**
   * 获取类名的简写。
   */
  private String getSimpleClassName(@NonNull String name) {
    checkNotNull(name);

    int lastIndex = name.lastIndexOf(".");
    return name.substring(lastIndex + 1);
  }

  /**
   * 确定堆栈跟踪的起始索引，跳过本类的方法调用。
   */
  private int getStackOffset(@NonNull StackTraceElement[] trace) {
    checkNotNull(trace);

    for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
      StackTraceElement e = trace[i];
      String name = e.getClassName();
      if (!name.equals(LoggerPrinter.class.getName()) && !name.equals(Logger.class.getName())) {
        return --i;
      }
    }
    return -1;
  }

  /**
   * 格式化日志标签。
   */
  @Nullable private String formatTag(@Nullable String tag) {
    if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
      return tag;
    }
    return this.tag;
  }

  /**
   * Builder 类，用于构建 PrettyFormatStrategy 实例。
   */
  public static class Builder {
    int methodCount = 2;
    int methodOffset = 0;
    boolean showThreadInfo = true;
    @Nullable LogStrategy logStrategy;
    @Nullable String tag = "PRETTY_LOGGER";

    private Builder() {
    }

    @NonNull public Builder methodCount(int val) {
      methodCount = val;
      return this;
    }

    @NonNull public Builder methodOffset(int val) {
      methodOffset = val;
      return this;
    }

    @NonNull public Builder showThreadInfo(boolean val) {
      showThreadInfo = val;
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

    @NonNull public PrettyFormatStrategy build() {
      if (logStrategy == null) {
        logStrategy = new LogcatLogStrategy();
      }
      return new PrettyFormatStrategy(this);
    }
  }

}

