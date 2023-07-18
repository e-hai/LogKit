package com.orhanobut.logger.format.strategy

/**
 * Used to determine how messages should be printed or saved.
 *
 * @see PrettyFormatStrategy
 *
 * @see CsvFormatStrategy
 *
 * 打印信息的展示样式
 */
interface FormatStrategy {
  fun log(priority: Int, tag: String?, message: String)
}