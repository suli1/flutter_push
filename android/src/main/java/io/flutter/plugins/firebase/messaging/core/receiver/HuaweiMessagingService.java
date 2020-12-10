package io.flutter.plugins.firebase.messaging.core.receiver;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import io.flutter.plugins.firebase.messaging.core.FlutterFirebaseMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.LogUtils;
import io.flutter.plugins.firebase.messaging.core.PushRemoteMessage;

/**
 * Created by suli on 2020/12/7
 **/
public class HuaweiMessagingService extends HmsMessageService {

  @Override
  public void onNewToken(String token) {
    super.onNewToken(token);
    LogUtils.d("HMS onNewToken:" + token);
    FlutterFirebaseMessagingUtils.sendTokenBroadcast(getApplicationContext(), token);
  }

  @Override
  public void onTokenError(Exception e) {
    super.onTokenError(e);
    LogUtils.e("huawei push onTokenError:" + e.getMessage(), e);
  }


  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    FlutterFirebaseMessagingUtils.sendMessageBroadcast(getApplicationContext(), PushRemoteMessage.buildFromHuawei(remoteMessage));
  }

}
