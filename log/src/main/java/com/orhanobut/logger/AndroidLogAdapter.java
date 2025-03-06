package com.orhanobut.logger;

import static com.orhanobut.logger.Utils.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 针对 {@link LogAdapter} 的 Android 终端日志输出实现。
 * <p>
 * 以漂亮的边框格式将日志输出到 LogCat。
 *
 * <pre>
 *  ┌──────────────────────────
 *  │ 方法调用栈历史
 *  ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 *  │ 日志消息
 *  └──────────────────────────
 * </pre>
 */
class AndroidLogAdapter implements LogAdapter {

    @NonNull
    private final FormatStrategy formatStrategy;

    // 默认构造函数，使用 PrettyFormatStrategy 格式化策略
    public AndroidLogAdapter() {
        this.formatStrategy = PrettyFormatStrategy.newBuilder().build();
    }

    // 构造函数，允许使用自定义的格式化策略
    public AndroidLogAdapter(@NonNull FormatStrategy formatStrategy) {
        this.formatStrategy = checkNotNull(formatStrategy);
    }

    /**
     * 判断日志是否需要输出。
     *
     * @param priority 日志级别，例如 DEBUG、WARNING
     * @param tag      日志消息的标签
     * @return 是否需要输出日志，默认为始终输出 (返回 true)。
     */
    @Override
    public boolean isLoggable(int priority, @Nullable String tag) {
        return true;
    }

    /**
     * 根据格式化策略输出日志。
     *
     * @param priority 日志级别，例如 DEBUG、WARNING
     * @param tag      日志消息的标签
     * @param message  日志消息的具体内容
     */
    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        formatStrategy.log(priority, tag, message);
    }

}

