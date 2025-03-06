package com.orhanobut.logger;


import static com.orhanobut.logger.Utils.checkNotNull;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

 class DiskLogWriteReadStrategy implements LogStrategy {

  private static final int MAX_READ_LINES = 3000; // 最大可读3000 行
  private static final int MAX_BYTES = 500 * 1024; // 每个文件最大 500KB，约 4000 行
  private static final int WHAT_WRITE = 1;
  private static final int WHAT_READ = 2;

  @NonNull private final Handler handler;

  // 构造函数，接收一个 Handler 实例，用于后台线程处理
  private DiskLogWriteReadStrategy(@NonNull Handler handler) {
    this.handler = checkNotNull(handler);
  }

  /**
   * 日志输出方法。
   *
   * @param level   日志级别
   * @param tag     日志标签
   * @param message 日志消息内容
   */
  @Override public void log(int level, @Nullable String tag, @NonNull String message) {
    checkNotNull(message);

    // 在调用线程中不做任何处理，直接将标签和消息传递给后台线程
    handler.sendMessage(handler.obtainMessage(WHAT_WRITE, message));
  }

  public void readLog(@NonNull LogCallback callback) {
    handler.sendMessage(handler.obtainMessage(WHAT_READ, callback));
  }

  public static DiskLogWriteReadStrategy build(Context context) {
    String diskPath = context.getFilesDir().getAbsolutePath();
    String folder = diskPath + File.separatorChar + "logger";
    HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
    ht.start();
    Handler handler = new DiskLogWriteReadStrategy.WriteReadHandler(ht.getLooper(), folder, MAX_BYTES);
    return new DiskLogWriteReadStrategy(handler);
  }

  /**
   * 后台线程处理类，用于实际写入日志文件。
   */
  static class WriteReadHandler extends Handler {

    @NonNull private final String folder; // 存储日志的文件夹路径
    private final int maxFileSize; // 文件大小限制

    // 构造函数，初始化 Handler 和相关参数
    WriteReadHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize) {
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
      switch (msg.what) {
        case WHAT_WRITE: {
          String content = (String) msg.obj;
          try {
            writeLog(folder, "logs", content);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        break;
        case WHAT_READ: {
          LogCallback callback = (LogCallback) msg.obj;
          try {
            readLog(folder, "logs", callback);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        break;
      }

    }

    /**
     * 在后台线程中写入日志。
     * 实现类只负责写入文件，其他的由抽象类处理，包括关闭流和捕获 IO 异常。
     *
     * @param content 日志内容
     */
    private void writeLog(@NonNull String folderName, @NonNull String fileName, @NonNull String content) throws IOException {
      File logFile = getWriteLogFile(folderName, fileName); // 获取日志文件
      FileWriter fileWriter = new FileWriter(logFile, true); // 以追加模式打开文件
      fileWriter.append(content); // 写入日志内容
      fileWriter.flush();
      fileWriter.close(); // 完成后关闭文件流
    }

    /**
     * 获取日志文件，如果文件夹不存在则创建。
     * 如果文件已经存在且文件大小超出限制，则创建新的日志文件。
     *
     * @param folderName 文件夹路径
     * @param fileName   基础文件名
     * @return 日志文件
     */
    private File getWriteLogFile(@NonNull String folderName, @NonNull String fileName) {
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

    /**
     * 从日志文件中读取最近的日志行。
     * 如果日志内容超过一个文件，则跨文件整合最新日志。
     */
    private void readLog(@NonNull String folderName, @NonNull String fileName, LogCallback callback) throws Exception {
      File folder = new File(folderName);
      if (!folder.exists()) {
        callback.onLogsRead(null);// 如果文件夹不存在，返回空内容
        return;
      }

      // 获取所有日志文件并按编号排序
      File[] files = folder.listFiles((dir, name) -> name.startsWith(fileName) && name.endsWith(".csv"));
      if (files == null || files.length == 0) {
        callback.onLogsRead(null);//  // 没有日志文件
        return;
      }
      Arrays.sort(files, (f1, f2) -> {
        String num1 = f1.getName().replace(fileName + "_", "").replace(".csv", "");
        String num2 = f2.getName().replace(fileName + "_", "").replace(".csv", "");
        return Integer.compare(Integer.parseInt(num2), Integer.parseInt(num1)); // 降序排序
      });

      LinkedList<String> recentLogs = new LinkedList<>();

      // 从最新文件开始读取日志
      for (File file : files) {

        //把文件中所有内容读取出来
        BufferedReader reader = new BufferedReader(new FileReader(file));
        LinkedList<String> currentFileLines = new LinkedList<>();
        String line;
        while ((line = reader.readLine()) != null) {
          currentFileLines.add(line);
        }

        // 将当前文件的内容添加到总结果中
        while (!currentFileLines.isEmpty() && recentLogs.size() < MAX_READ_LINES) {
          recentLogs.add(currentFileLines.pollLast());
        }
        // 如果已经读取足够的行，退出循环
        if (recentLogs.size() >= MAX_READ_LINES) {
          break;
        }
      }

       callback.onLogsRead(recentLogs);
    }
  }


  // 用于日志读取完成后的回调接口
  public interface LogCallback {
    void onLogsRead(List<String> logs);
  }
}
