// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebase.messaging;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.heytap.msp.push.HeytapPushManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.FlutterShellArgs;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.NewIntentListener;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugins.firebase.core.FlutterFirebasePlugin;
import io.flutter.plugins.firebase.messaging.core.FlutterMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.IPush;
import io.flutter.plugins.firebase.messaging.core.LogUtils;
import io.flutter.plugins.firebase.messaging.core.PushConfig;
import io.flutter.plugins.firebase.messaging.core.PushRemoteMessage;
import io.flutter.plugins.firebase.messaging.core.PushType;
import io.flutter.plugins.firebase.messaging.core.client.FcmPush;
import io.flutter.plugins.firebase.messaging.core.client.HmsPush;
import io.flutter.plugins.firebase.messaging.core.client.OppoPush;
import io.flutter.plugins.firebase.messaging.core.client.VivoPush;
import io.flutter.plugins.firebase.messaging.core.client.XiaomiPush;

import static io.flutter.plugins.firebase.core.FlutterFirebasePluginRegistry.registerPlugin;

/**
 * FlutterFirebaseMessagingPlugin
 */
public class FlutterFirebaseMessagingPlugin extends BroadcastReceiver implements
    FlutterFirebasePlugin,
    MethodCallHandler,
    NewIntentListener, FlutterPlugin, ActivityAware {

  public static HashMap<String, PushRemoteMessage> notifications = new HashMap<>();

  private Context applicationContext;
  private final HashMap<String, Boolean> consumedInitialMessages = new HashMap<>();
  private MethodChannel channel;
  private Activity mainActivity;
  private PushRemoteMessage initialMessage;
  @Nullable
  private IPush pushClient;

  private final Map<String, String> newTokenMap = new HashMap<>();

  @SuppressWarnings("unused")
  public static void registerWith(Registrar registrar) {
    FlutterFirebaseMessagingPlugin instance = new FlutterFirebaseMessagingPlugin();
    instance.setActivity(registrar.activity());
    registrar.addNewIntentListener(instance);
    instance.initInstance(registrar.messenger());
  }

  private void initInstance(BinaryMessenger messenger) {
    String channelName = "plugins.flutter.io/flutter_push";
    channel = new MethodChannel(messenger, channelName);
    channel.setMethodCallHandler(this);

    // Register broadcast receiver
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(FlutterMessagingUtils.ACTION_TOKEN);
    intentFilter.addAction(FlutterMessagingUtils.ACTION_REMOTE_MESSAGE);
    LocalBroadcastManager manager =
        LocalBroadcastManager.getInstance(ContextHolder.getApplicationContext());
    manager.registerReceiver(this, intentFilter);

    registerPlugin(channelName, this);
  }

  private void onAttachedToEngine(Context context, BinaryMessenger binaryMessenger) {
    applicationContext = context;
    initInstance(binaryMessenger);
  }

  private void setActivity(Activity flutterActivity) {
    this.mainActivity = flutterActivity;
  }

  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    applicationContext = null;
    LocalBroadcastManager.getInstance(ContextHolder.getApplicationContext()).unregisterReceiver(this);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    binding.addOnNewIntentListener(this);
    this.mainActivity = binding.getActivity();
    if (mainActivity.getIntent() != null && mainActivity.getIntent().getExtras() != null) {
      if ((mainActivity.getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) {
        onNewIntent(mainActivity.getIntent());
      }
    }
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    this.mainActivity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    binding.addOnNewIntentListener(this);
    this.mainActivity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    this.mainActivity = null;
  }

  // BroadcastReceiver implementation.
  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();

    if (action == null) {
      return;
    }

    if (action.equals(FlutterMessagingUtils.ACTION_TOKEN)) {
      String token = intent.getStringExtra(FlutterMessagingUtils.EXTRA_TOKEN);
      String pushType = intent.getStringExtra(FlutterMessagingUtils.EXTRA_PUSH_TYPE);
      Map<String, Object> resultMap = new HashMap<>();
      resultMap.put("token", token);
      resultMap.put("type", pushType);
      newTokenMap.put(pushType, token);
      channel.invokeMethod("Messaging#onTokenRefresh", resultMap);
    } else if (action.equals(FlutterMessagingUtils.ACTION_REMOTE_MESSAGE)) {
      PushRemoteMessage message = intent.getParcelableExtra(FlutterMessagingUtils.EXTRA_REMOTE_MESSAGE);
      if (message == null) return;
      Map<String, Object> content = FlutterMessagingUtils.remoteMessageToMap(message);
      channel.invokeMethod("Messaging#onMessage", content);
    }
  }

  private Task<Void> initPush() {
    return Tasks.call(
        cachedThreadPool,
        () -> {
          HeytapPushManager.init(applicationContext, LogUtils.debuggable);
          PushType pushType = FlutterMessagingUtils.getSupportedPush(applicationContext);
          if (pushType == null) {
            pushType = PushType.XIAO_MI;
          }
          LogUtils.d("Support push type:" + pushType.name());

          PushConfig pushConfig = initConfig(pushType);

          // 可能某个支持的推送未配置，选择一个默认的推送
          if (pushConfig.client == null) {
            if (FlutterMessagingUtils.isSupportFcm(applicationContext)) {
              pushConfig = initConfig(PushType.FCM);
            } else {
              pushConfig = initConfig(PushType.XIAO_MI);
            }
          }
          LogUtils.d("init push type:" + pushConfig.type);

          if (pushConfig.client != null) {
            Constructor<?> constructor = pushConfig.client.getConstructor(PushConfig.class);
            pushClient = (IPush) constructor.newInstance(pushConfig);
            pushClient.register();
          }
          return null;
        });
  }

  private PushConfig initConfig(PushType type) {
    PushConfig config = new PushConfig();
    config.type = type;
    try {
      switch (type) {
        case FCM:
          config.client = FcmPush.class;
          break;
        case OPPO: {
          Bundle metaData = getMetaData();
          String appKey = metaData.getString("com.oppo.push.app_key");
          String appSecret = metaData.getString("com.oppo.push.app_secret");
          if (appKey != null && appSecret != null) {
            config.appKey = appKey;
            config.appSecret = appSecret;
            config.client = OppoPush.class;
          }
          break;
        }
        case VIVO: {
          Bundle metaData = getMetaData();
          if (metaData.containsKey("com.vivo.push.app_id")
              && metaData.containsKey("com.vivo.push.app_id")) {
            config.client = VivoPush.class;
          }
        }
        break;
        case XIAO_MI: {
          Bundle metaData = getMetaData();
          String appId = metaData.getString("com.xiaomi.push.app_id");
          String appKey = metaData.getString("com.xiaomi.push.app_key");
          if (appId != null && appKey != null) {
            config.appId = appId;
            config.appKey = appKey;
            config.client = XiaomiPush.class;
          }
          break;
        }
        case HMS: {
          Bundle metaData = getMetaData();
          String appId = metaData.getString("com.huawei.push.app_id");
          if (appId != null) {
            config.appId = appId;
            config.client = HmsPush.class;
          }
          break;
        }
        default:
          break;
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return config;
  }

  private Bundle getMetaData() throws PackageManager.NameNotFoundException {
    return applicationContext.getPackageManager()
        .getApplicationInfo(applicationContext.getPackageName(),
            PackageManager.GET_META_DATA).metaData;
  }

  private Task<Map<String, Object>> requestPermission() {
    return Tasks.call(
        cachedThreadPool,
        () -> {
          boolean isEnabled =
              NotificationManagerCompat.from(applicationContext).areNotificationsEnabled();
          if ((pushClient != null && pushClient.getType() == PushType.OPPO)
              || HeytapPushManager.isSupportPush()) {
            LogUtils.d("HeytapPushManager.requestNotificationPermission");
            HeytapPushManager.requestNotificationPermission();
          }
          return new HashMap<String, Object>() {
            {
              put("authorizationStatus", isEnabled ? 1 : 0);
            }
          };
        });
  }

  private Task<Void> deleteToken() {
    return Tasks.call(
        cachedThreadPool,
        () -> {
          if (pushClient != null) {
            pushClient.deleteToken();
          }
          return null;
        });
  }

  private Task<Object> getToken() {
    return Tasks.call(
        cachedThreadPool,
        () -> {
          if (pushClient != null) {
            String pushType = pushClient.getType().name();
            String token = newTokenMap.get(pushType);
            if (TextUtils.isEmpty(token)) {
              token = pushClient.getToken();
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("type", pushType);
            map.put("token", token);
            return map;
          }
          return null;
        });
  }

  private Task<Void> subscribeToTopic(Map<String, Object> arguments) {
    return Tasks.call(
        cachedThreadPool,
        () -> {
          if (pushClient != null) {
            pushClient.subscribeToTopic(arguments);
          }
          return null;
        });
  }

  private Task<Void> unsubscribeFromTopic(Map<String, Object> arguments) {
    return Tasks.call(
        cachedThreadPool,
        () -> {
          if (pushClient != null) {
            pushClient.unsubscribeFromTopic(arguments);
          }
          return null;
        });
  }

  private Task<Map<String, Object>> getInitialMessage(Map<String, Object> arguments) {
    return Tasks.call(
        cachedThreadPool,
        () -> {
          if (initialMessage != null) {
            Map<String, Object> remoteMessageMap =
                FlutterMessagingUtils.remoteMessageToMap(initialMessage);
            initialMessage = null;
            return remoteMessageMap;
          }

          if (mainActivity == null) {
            return null;
          }

          Intent intent = mainActivity.getIntent();

          if (intent == null || intent.getExtras() == null) {
            return null;
          }

          // Remote Message ID can be either one of the following...
          String messageId = intent.getExtras().getString("google.message_id");
          if (messageId == null) messageId = intent.getExtras().getString("message_id");

          // We only want to handle non-consumed initial messages.
          if (messageId == null || consumedInitialMessages.get(messageId) != null) {
            return null;
          }

          PushRemoteMessage remoteMessage = FlutterFirebaseMessagingPlugin.notifications.get(messageId);

          // If we can't find a copy of the remote message in memory then check from our persisted store.
          if (remoteMessage == null) {
            remoteMessage =
                FlutterFirebaseMessagingStore.getInstance().getFirebaseMessage(messageId);
            FlutterFirebaseMessagingStore.getInstance().removeFirebaseMessage(messageId);
          }

          if (remoteMessage == null) {
            return null;
          }

          consumedInitialMessages.put(messageId, true);
          return FlutterMessagingUtils.remoteMessageToMap(remoteMessage);
        });
  }

  @Override
  public void onMethodCall(final MethodCall call, @NonNull final Result result) {
    Task<?> methodCallTask;

    switch (call.method) {
      // This message is sent when the Dart side of this plugin is told to initialize.
      // In response, this (native) side of the plugin needs to spin up a background
      // Dart isolate by using the given pluginCallbackHandle, and then setup a background
      // method channel to communicate with the new background isolate. Once completed,
      // this onMethodCall() method will receive messages from both the primary and background
      // method channels.
      case "Messaging#startBackgroundIsolate":
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = ((Map<String, Object>) call.arguments);

        long pluginCallbackHandle;
        long userCallbackHandle;

        Object arg1 = arguments.get("pluginCallbackHandle");
        Object arg2 = arguments.get("userCallbackHandle");

        if (arg1 instanceof Long) {
          pluginCallbackHandle = (Long) arg1;
        } else {
          pluginCallbackHandle = Long.valueOf((Integer) arg1);
        }

        if (arg2 instanceof Long) {
          userCallbackHandle = (Long) arg2;
        } else {
          userCallbackHandle = Long.valueOf((Integer) arg2);
        }

        FlutterShellArgs shellArgs = null;
        if (mainActivity != null) {
          shellArgs =
              ((io.flutter.embedding.android.FlutterActivity) mainActivity).getFlutterShellArgs();
        }

        FlutterFirebaseMessagingBackgroundService.setCallbackDispatcher(pluginCallbackHandle);
        FlutterFirebaseMessagingBackgroundService.setUserCallbackHandle(userCallbackHandle);
        FlutterFirebaseMessagingBackgroundService.startBackgroundIsolate(
            pluginCallbackHandle, shellArgs);
        methodCallTask = initPush();
        break;
      case "Messaging#requestPermission":
        methodCallTask = requestPermission();
        break;
      case "Messaging#getInitialMessage":
        methodCallTask = getInitialMessage(call.arguments());
        break;
      case "Messaging#deleteToken":
        methodCallTask = deleteToken();
        break;
      case "Messaging#getToken":
        methodCallTask = getToken();
        break;
      case "Messaging#subscribeToTopic":
        methodCallTask = subscribeToTopic(call.arguments());
        break;
      case "Messaging#unsubscribeFromTopic":
        methodCallTask = unsubscribeFromTopic(call.arguments());
        break;
      //      case "Messaging#sendMessage":
      //        methodCallTask = sendMessage(call.arguments());
      //        break;
      default:
        result.notImplemented();
        return;
    }

    methodCallTask.addOnCompleteListener(
        task -> {
          if (task.isSuccessful()) {
            result.success(task.getResult());
          } else {
            Exception exception = task.getException();
            result.error(
                "firebase_messaging",
                exception != null ? exception.getMessage() : null,
                getExceptionDetails(exception));
          }
        });
  }

  private Map<String, Object> getExceptionDetails(@Nullable Exception exception) {
    Map<String, Object> details = new HashMap<>();
    details.put("code", "unknown");
    if (exception != null) {
      details.put("message", exception.getMessage());
    } else {
      details.put("message", "An unknown error has occurred.");
    }
    details.put("additionalData", new HashMap<>());
    return details;
  }

  @Override
  public boolean onNewIntent(Intent intent) {
    if (intent != null) {
      LogUtils.d("intent extras:" + intent.getExtras() + ",action:" + intent.getDataString());
    }
    if (intent == null || intent.getExtras() == null) {
      return false;
    }
    // Remote Message ID can be either one of the following...
    String messageId = intent.getExtras().getString("google.message_id");
    if (messageId == null) messageId = intent.getExtras().getString("message_id");
    if (messageId == null) {
      return false;
    }

    PushRemoteMessage remoteMessage = FlutterFirebaseMessagingPlugin.notifications.get(messageId);

    // If we can't find a copy of the remote message in memory then check from our persisted store.
    if (remoteMessage == null) {
      remoteMessage = FlutterFirebaseMessagingStore.getInstance().getFirebaseMessage(messageId);
      // Note we don't remove it here as the user may still call getInitialMessage.
    }

    if (remoteMessage == null) {
      return false;
    }

    // Store this message for later use by getInitialMessage.
    initialMessage = remoteMessage;

    FlutterFirebaseMessagingPlugin.notifications.remove(messageId);
    channel.invokeMethod(
        "Messaging#onMessageOpenedApp",
        FlutterMessagingUtils.remoteMessageToMap(remoteMessage));
    mainActivity.setIntent(intent);
    return true;
  }

  @Override
  public Task<Map<String, Object>> getPluginConstantsForFirebaseApp(FirebaseApp firebaseApp) {
    return Tasks.call(
        cachedThreadPool,
        () -> {
          Map<String, Object> constants = new HashMap<>();
          FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
          constants.put("AUTO_INIT_ENABLED", firebaseMessaging.isAutoInitEnabled());
          return constants;
        });
  }

  @Override
  public Task<Void> didReinitializeFirebaseCore() {
    return Tasks.call(cachedThreadPool, () -> null);
  }
}
