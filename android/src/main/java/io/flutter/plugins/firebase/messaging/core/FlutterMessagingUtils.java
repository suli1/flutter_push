// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebase.messaging.core;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
//import com.heytap.msp.push.HeytapPushManager;
//import com.vivo.push.PushClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.flutter.plugins.firebase.messaging.ContextHolder;
import io.flutter.plugins.firebase.messaging.FlutterFirebaseMessagingBackgroundService;
import io.flutter.plugins.firebase.messaging.FlutterFirebaseMessagingPlugin;
import io.flutter.plugins.firebase.messaging.FlutterFirebaseMessagingStore;

public class FlutterMessagingUtils {
  public static final String SHARED_PREFERENCES_KEY = "io.flutter.firebase.messaging.callback";
  public static final String ACTION_REMOTE_MESSAGE = "io.flutter.plugins.firebase.messaging.NOTIFICATION";
  public static final String EXTRA_REMOTE_MESSAGE = "notification";
  public static final String ACTION_TOKEN = "io.flutter.plugins.firebase.messaging.TOKEN";
  public static final String EXTRA_TOKEN = "token";
  public static final String EXTRA_PUSH_TYPE = "pushType";
  public static final int JOB_ID = 2020;

  private static final String KEY_COLLAPSE_KEY = "collapseKey";
  private static final String KEY_DATA = "data";
  private static final String KEY_FROM = "from";
  private static final String KEY_MESSAGE_ID = "messageId";
  private static final String KEY_MESSAGE_TYPE = "messageType";
  private static final String KEY_SENT_TIME = "sentTime";
  private static final String KEY_TO = "to";
  private static final String KEY_TTL = "ttl";

  @Nullable
  public static PushType getSupportedPush(Context context) {
    //if (HeytapPushManager.isSupportPush()) {
    //  return PushType.OPPO;
    //} else if (RomUtils.isEmui()) {
    //  return PushType.HMS;
    //} else if (PushClient.getInstance(context).isSupport()) {
    //  return PushType.VIVO;
    //} else
    if (RomUtils.isMiui()) {
      return PushType.XIAO_MI;
    } else if (isSupportFcm(context)) {
      return PushType.FCM;
    } else {
      return null;
    }
  }

  public static boolean isSupportFcm(Context context) {
    return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == 0;
  }

  public static void sendTokenBroadcast(Context context, String token, PushType pushType) {
    Intent onMessageIntent = new Intent(FlutterMessagingUtils.ACTION_TOKEN);
    onMessageIntent.putExtra(FlutterMessagingUtils.EXTRA_TOKEN, token);
    onMessageIntent.putExtra(FlutterMessagingUtils.EXTRA_PUSH_TYPE, pushType.name());
    LocalBroadcastManager.getInstance(context).sendBroadcast(onMessageIntent);
  }

  public static void sendMessageBroadcast(Context context, PushRemoteMessage remoteMessage) {
    if (ContextHolder.getApplicationContext() == null) {
      ContextHolder.setApplicationContext(context.getApplicationContext());
    }

    // Store the RemoteMessage if the message contains a notification payload.
    if (remoteMessage.notification != null) {
      FlutterFirebaseMessagingPlugin.notifications.put(remoteMessage.messageId, remoteMessage);
      FlutterFirebaseMessagingStore.getInstance().storeFirebaseMessage(remoteMessage);
    }

    //  |-> ---------------------
    //      App in Foreground
    //   ------------------------
    if (FlutterMessagingUtils.isApplicationForeground(context)) {
      Intent onMessageIntent = new Intent(FlutterMessagingUtils.ACTION_REMOTE_MESSAGE);
      onMessageIntent.putExtra(FlutterMessagingUtils.EXTRA_REMOTE_MESSAGE, remoteMessage);
      LocalBroadcastManager.getInstance(context).sendBroadcast(onMessageIntent);
      return;
    }

    //  |-> ---------------------
    //    App in Background/Quit
    //   ------------------------
    Intent onBackgroundMessageIntent =
        new Intent(context, FlutterFirebaseMessagingBackgroundService.class);
    onBackgroundMessageIntent.putExtra(
        FlutterMessagingUtils.EXTRA_REMOTE_MESSAGE, remoteMessage);
    FlutterFirebaseMessagingBackgroundService.enqueueMessageProcessing(
        context, onBackgroundMessageIntent);

  }


  public static Map<String, Object> remoteMessageToMap(PushRemoteMessage remoteMessage) {
    Map<String, Object> messageMap = new HashMap<>();
    Map<String, Object> dataMap = new HashMap<>();

    if (remoteMessage.collapseKey != null) {
      messageMap.put(KEY_COLLAPSE_KEY, remoteMessage.collapseKey);
    }

    if (remoteMessage.from != null) {
      messageMap.put(KEY_FROM, remoteMessage.from);
    }

    if (remoteMessage.to != null) {
      messageMap.put(KEY_TO, remoteMessage.to);
    }

    if (remoteMessage.messageId != null) {
      messageMap.put(KEY_MESSAGE_ID, remoteMessage.messageId);
    }

    if (remoteMessage.messageType != null) {
      messageMap.put(KEY_MESSAGE_TYPE, remoteMessage.messageType);
    }

    if (remoteMessage.data.size() > 0) {
      Set<Map.Entry<String, String>> entries = remoteMessage.data.entrySet();
      for (Map.Entry<String, String> entry : entries) {
        dataMap.put(entry.getKey(), entry.getValue());
      }
    }

    messageMap.put(KEY_DATA, dataMap);
    messageMap.put(KEY_TTL, remoteMessage.ttl);
    messageMap.put(KEY_SENT_TIME, remoteMessage.sentTime);

    if (remoteMessage.notification != null) {
      messageMap.put(
          "notification", remoteMessageNotificationToMap(remoteMessage.notification));
    }

    return messageMap;
  }

  private static Map<String, Object> remoteMessageNotificationToMap(PushRemoteMessage.Notification notification) {
    Map<String, Object> notificationMap = new HashMap<>();

    if (notification.title != null) {
      notificationMap.put("title", notification.title);
    }

    if (notification.titleLocKey != null) {
      notificationMap.put("titleLocKey", notification.titleLocKey);
    }

    if (notification.titleLocArgs != null) {
      notificationMap.put("titleLocArgs", Arrays.asList(notification.titleLocArgs));
    }

    if (notification.body != null) {
      notificationMap.put("body", notification.body);
    }

    if (notification.bodyLocKey != null) {
      notificationMap.put("bodyLocKey", notification.bodyLocKey);
    }

    if (notification.bodyLocArgs != null) {
      notificationMap.put("bodyLocArgs", Arrays.asList(notification.bodyLocArgs));
    }

    if (notification.android != null) {
      Map<String, Object> androidNotificationMap = new HashMap<>();

      if (notification.android.channelId != null) {
        androidNotificationMap.put("channelId", notification.android.channelId);
      }

      if (notification.android.clickAction != null) {
        androidNotificationMap.put("clickAction", notification.android.clickAction);
      }

      if (notification.android.color != null) {
        androidNotificationMap.put("color", notification.android.color);
      }

      if (notification.android.smallIcon != null) {
        androidNotificationMap.put("smallIcon", notification.android.smallIcon);
      }

      if (notification.android.imageUrl != null) {
        androidNotificationMap.put("imageUrl", notification.android.imageUrl);
      }

      if (notification.android.link != null) {
        androidNotificationMap.put("link", notification.android.link);
      }

      if (notification.android.priority != null) {
        androidNotificationMap.put("priority", notification.android.priority);
      }

      if (notification.android.sound != null) {
        androidNotificationMap.put("sound", notification.android.sound);
      }

      if (notification.android.ticker != null) {
        androidNotificationMap.put("ticker", notification.android.ticker);
      }

      if (notification.android.visibility != null) {
        androidNotificationMap.put("visibility", notification.android.visibility);
      }

      notificationMap.put("android", androidNotificationMap);
    }
    return notificationMap;
  }

  /**
   * Identify if the application is currently in a state where user interaction is possible. This
   * method is called when a remote message is received to determine how the incoming message should
   * be handled.
   *
   * @param context context.
   * @return True if the application is currently in a state where user interaction is possible,
   * false otherwise.
   */
  public static boolean isApplicationForeground(Context context) {
    KeyguardManager keyguardManager =
        (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

    if (keyguardManager != null && keyguardManager.isKeyguardLocked()) {
      return false;
    }

    ActivityManager activityManager =
        (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    if (activityManager == null) return false;

    List<ActivityManager.RunningAppProcessInfo> appProcesses =
        activityManager.getRunningAppProcesses();
    if (appProcesses == null) return false;

    final String packageName = context.getPackageName();
    for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
      if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
          && appProcess.processName.equals(packageName)) {
        return true;
      }
    }

    return false;
  }

  // Extracted to handle multi-app support in the future.
  // arguments.get("appName") - to get the Firebase app name.
  public static FirebaseMessaging getFirebaseMessagingForArguments(Map<String, Object> arguments) {
    return FirebaseMessaging.getInstance();
  }

  /**
   * Builds an instance of {@link RemoteMessage} from Flutter method channel call arguments.
   *
   * @param arguments Method channel call arguments.
   * @return RemoteMessage
   */
  public static PushRemoteMessage getRemoteMessageForArguments(Map<String, Object> arguments) {
    @SuppressWarnings("unchecked")
    Map<String, Object> messageMap =
        (Map<String, Object>) Objects.requireNonNull(arguments.get("message"));

    PushRemoteMessage remoteMessage = new PushRemoteMessage();

    remoteMessage.to = (String) messageMap.get("to");
    remoteMessage.collapseKey = (String) messageMap.get("collapseKey");
    remoteMessage.messageId = (String) messageMap.get("messageId");
    remoteMessage.messageType = (String) messageMap.get("messageType");
    remoteMessage.ttl = (Integer) messageMap.get("ttl");
    remoteMessage.data = (Map<String, String>) messageMap.get("data");

    return remoteMessage;
  }
}
