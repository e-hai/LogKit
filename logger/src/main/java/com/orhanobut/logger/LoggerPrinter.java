package com.orhanobut.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static com.orhanobut.logger.Logger.ASSERT;
import static com.orhanobut.logger.Logger.DEBUG;
import static com.orhanobut.logger.Logger.ERROR;
import static com.orhanobut.logger.Logger.INFO;
import static com.orhanobut.logger.Logger.VERBOSE;
import static com.orhanobut.logger.Logger.WARN;
import static com.orhanobut.logger.Utils.checkNotNull;

/**
 * `LoggerPrinter` 是 `Printer` 接口的具体实现，提供了一系列用于日志打印的功能，
 * 包括支持格式化的 JSON、XML 输出，支持异常日志的处理以及多种日志级别的记录。
 */
class LoggerPrinter implements Printer {

  /**
   * 用于格式化 JSON 输出时的缩进值
   */
  private static final int JSON_INDENT = 2;

  /**
   * 为日志消息提供一次性标签（线程安全）
   */
  private final ThreadLocal<String> localTag = new ThreadLocal<>();

  /**
   * 日志适配器列表，用于处理日志输出
   */
  private final List<LogAdapter> logAdapters = new ArrayList<>();

  /**
   * 设置一次性日志标签
   *
   * @param tag 自定义标签
   * @return 返回当前实例
   */
  @Override
  public Printer t(String tag) {
    if (tag != null) {
      localTag.set(tag);
    }
    return this;
  }

  /**
   * 打印调试级别的日志
   *
   * @param message 日志内容
   * @param args    格式化参数
   */
  @Override
  public void d(@NonNull String message, @Nullable Object... args) {
    log(DEBUG, null, message, args);
  }

  /**
   * 打印对象的调试日志
   *
   * @param object 要打印的对象
   */
  @Override
  public void d(@Nullable Object object) {
    log(DEBUG, null, Utils.toString(object));
  }

  /**
   * 打印错误级别的日志
   *
   * @param message 日志内容
   * @param args    格式化参数
   */
  @Override
  public void e(@NonNull String message, @Nullable Object... args) {
    e(null, message, args);
  }

  /**
   * 打印错误级别的日志，包含异常信息
   *
   * @param throwable 异常对象
   * @param message   日志内容
   * @param args      格式化参数
   */
  @Override
  public void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
    log(ERROR, throwable, message, args);
  }

  /**
   * 打印警告级别的日志
   *
   * @param message 日志内容
   * @param args    格式化参数
   */
  @Override
  public void w(@NonNull String message, @Nullable Object... args) {
    log(WARN, null, message, args);
  }

  /**
   * 打印信息级别的日志
   *
   * @param message 日志内容
   * @param args    格式化参数
   */
  @Override
  public void i(@NonNull String message, @Nullable Object... args) {
    log(INFO, null, message, args);
  }

  /**
   * 打印详细级别的日志
   *
   * @param message 日志内容
   * @param args    格式化参数
   */
  @Override
  public void v(@NonNull String message, @Nullable Object... args) {
    log(VERBOSE, null, message, args);
  }

  /**
   * 打印断言级别的日志（通常用于严重错误）
   *
   * @param message 日志内容
   * @param args    格式化参数
   */
  @Override
  public void wtf(@NonNull String message, @Nullable Object... args) {
    log(ASSERT, null, message, args);
  }

  /**
   * 格式化并打印 JSON 内容
   *
   * @param json JSON 字符串
   */
  @Override
  public void json(@Nullable String json) {
    if (Utils.isEmpty(json)) {
      d("Empty/Null json content");
      return;
    }
    try {
      json = json.trim();
      if (json.startsWith("{")) {
        JSONObject jsonObject = new JSONObject(json);
        String message = jsonObject.toString(JSON_INDENT);
        d(message);
        return;
      }
      if (json.startsWith("[")) {
        JSONArray jsonArray = new JSONArray(json);
        String message = jsonArray.toString(JSON_INDENT);
        d(message);
        return;
      }
      e("Invalid Json");
    } catch (JSONException e) {
      e("Invalid Json");
    }
  }

  /**
   * 格式化并打印 XML 内容
   *
   * @param xml XML 字符串
   */
  @Override
  public void xml(@Nullable String xml) {
    if (Utils.isEmpty(xml)) {
      d("Empty/Null xml content");
      return;
    }
    try {
      Source xmlInput = new StreamSource(new StringReader(xml));
      StreamResult xmlOutput = new StreamResult(new StringWriter());
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.transform(xmlInput, xmlOutput);
      d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
    } catch (TransformerException e) {
      e("Invalid xml");
    }
  }

  /**
   * 打印日志的主方法
   *
   * @param priority  日志优先级
   * @param tag       日志标签
   * @param message   日志内容
   * @param throwable 异常对象（可选）
   */
  @Override
  public synchronized void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
    if (throwable != null && message != null) {
      message += " : " + Utils.getStackTraceString(throwable);
    }
    if (throwable != null && message == null) {
      message = Utils.getStackTraceString(throwable);
    }
    if (Utils.isEmpty(message)) {
      message = "Empty/NULL log message";
    }

    for (LogAdapter adapter : logAdapters) {
      if (adapter.isLoggable(priority, tag)) {
        adapter.log(priority, tag, message);
      }
    }
  }

  /**
   * 清除所有日志适配器
   */
  @Override
  public void clearLogAdapters() {
    logAdapters.clear();
  }

  /**
   * 添加日志适配器
   *
   * @param adapter 日志适配器
   */
  @Override
  public void addAdapter(@NonNull LogAdapter adapter) {
    logAdapters.add(checkNotNull(adapter));
  }


  /**
   * This method is synchronized in order to avoid messy of logs' order.
   */
  private synchronized void log(int priority,
                                @Nullable Throwable throwable,
                                @NonNull String msg,
                                @Nullable Object... args) {
    checkNotNull(msg);

    String tag = getTag();
    String message = createMessage(msg, args);
    log(priority, tag, message, throwable);
  }

  /**
   * 获取当前日志标签（优先返回线程标签）
   *
   * @return 当前标签
   */
  @Nullable
  private String getTag() {
    String tag = localTag.get();
    if (tag != null) {
      localTag.remove();
      return tag;
    }
    return null;
  }

  /**
   * 格式化日志内容
   *
   * @param message 原始内容
   * @param args    格式化参数
   * @return 格式化后的内容
   */
  @NonNull
  private String createMessage(@NonNull String message, @Nullable Object... args) {
    return args == null || args.length == 0 ? message : String.format(message, args);
  }

}
