package io.flutter.plugins.firebase.messaging.core;

import android.util.Log;

import io.flutter.plugins.firebase.messaging.BuildConfig;

/**
 * Created by suli on 2020/12/7
 *
 * Logging tool
 **/
public class LogUtils {
  public final static boolean debuggable = BuildConfig.DEBUG;
  private final static String TAG = "PushMessagingPlugin";

  public static void d(String message) {
    Log.d(TAG, message);
  }

  public static void e(String message) {
    Log.e(TAG, message);
  }

  public static void e(String message, Throwable tr) {
    Log.e(TAG, message, tr);
  }
}
