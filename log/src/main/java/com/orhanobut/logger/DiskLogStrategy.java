package com.orhanobut.logger;

import static com.orhanobut.logger.Utils.checkNotNull;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * 负责在 Android 中处理文件日志操作的后台线程。
 * 实现类可以直接在此类中执行 I/O 操作。
 *
 * 将所有日志以 CSV 格式写入磁盘。
 */
public class DiskLogStrategy implements LogStrategy {

  @NonNull private final Handler handler;

  // 构造函数，接收一个 Handler 实例，用于后台线程处理
  public DiskLogStrategy(@NonNull Handler handler) {
    this.handler = checkNotNull(handler);
  }

  /**
   * 日志输出方法。
   *
   * @param level 日志级别
   * @param tag 日志标签
   * @param message 日志消息内容
   */
  @Override public void log(int level, @Nullable String tag, @NonNull String message) {
    checkNotNull(message);

    // 在调用线程中不做任何处理，直接将标签和消息传递给后台线程
    handler.sendMessage(handler.obtainMessage(level, message));
  }

  /**
   * 后台线程处理类，用于实际写入日志文件。
   */
  static class WriteHandler extends Handler {

    @NonNull private final String folder; // 存储日志的文件夹路径
    private final int maxFileSize; // 文件大小限制

    // 构造函数，初始化 Handler 和相关参数
    WriteHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize) {
      super(checkNotNull(looper));
      this.folder = checkNotNull(folder);
      this.maxFileSize = maxFileSize;
    }

    /**
     * 处理接收到的消息，实际写入日志文件。
     *
     * @param msg 包含日志内容的消息对象
     */
    @SuppressWarnings("checkstyle:emptyblock")
    @Override public void handleMessage(@NonNull Message msg) {
      String content = (String) msg.obj;

      FileWriter fileWriter = null;
      File logFile = getLogFile(folder, "logs"); // 获取日志文件

      try {
        fileWriter = new FileWriter(logFile, true); // 以追加模式打开文件

        writeLog(fileWriter, content); // 写入日志

        fileWriter.flush();
        fileWriter.close(); // 完成后关闭文件流
      } catch (IOException e) {
        if (fileWriter != null) {
          try {
            fileWriter.flush();
            fileWriter.close();
          } catch (IOException e1) { /* 静默失败 */ }
        }
      }
    }

    /**
     * 在后台线程中写入日志。
     * 实现类只负责写入文件，其他的由抽象类处理，包括关闭流和捕获 IO 异常。
     *
     * @param fileWriter 已初始化的 FileWriter 实例
     * @param content 日志内容
     */
    private void writeLog(@NonNull FileWriter fileWriter, @NonNull String content) throws IOException {
      checkNotNull(fileWriter);
      checkNotNull(content);

      fileWriter.append(content); // 写入日志内容
    }

    /**
     * 获取日志文件，如果文件夹不存在则创建。
     * 如果文件已经存在且文件大小超出限制，则创建新的日志文件。
     *
     * @param folderName 文件夹路径
     * @param fileName 基础文件名
     * @return 日志文件
     */
    private File getLogFile(@NonNull String folderName, @NonNull String fileName) {
      checkNotNull(folderName);
      checkNotNull(fileName);

      File folder = new File(folderName);
      if (!folder.exists()) {
        // 如果文件夹不存在，尝试创建文件夹
        folder.mkdirs();
      }

      int newFileCount = 0; // 新文件计数
      File newFile;
      File existingFile = null;

      // 生成新的文件名，直到文件名不重复
      newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
      while (newFile.exists()) {
        existingFile = newFile;
        newFileCount++;
        newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
      }

      // 如果已存在文件，且文件大小超过最大限制，则返回新文件
      if (existingFile != null) {
        if (existingFile.length() >= maxFileSize) {
          return newFile;
        }
        return existingFile;
      }

      return newFile; // 如果没有找到现有文件，返回新文件
    }
  }
}

