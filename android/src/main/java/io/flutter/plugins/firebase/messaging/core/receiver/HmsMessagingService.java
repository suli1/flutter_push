package io.flutter.plugins.firebase.messaging.core.receiver;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import io.flutter.plugins.firebase.messaging.core.FlutterMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.LogUtils;
import io.flutter.plugins.firebase.messaging.core.PushRemoteMessage;
import io.flutter.plugins.firebase.messaging.core.PushType;

/**
 * Created by suli on 2020/12/7
 **/
public class HmsMessagingService extends HmsMessageService {

  @Override
  public void onNewToken(String token) {
    super.onNewToken(token);
    LogUtils.d("HMS onNewToken:" + token);
    FlutterMessagingUtils.sendTokenBroadcast(getApplicationContext(), token, PushType.HMS);
  }

  @Override
  public void onTokenError(Exception e) {
    super.onTokenError(e);
    LogUtils.e("huawei push onTokenError:" + e.getMessage(), e);
  }


  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    LogUtils.d("HMS onMessageReceived:" + remoteMessage.getNotification().getTitle());
    FlutterMessagingUtils.sendMessageBroadcast(getApplicationContext(), PushRemoteMessage.buildFromHuawei(remoteMessage));
  }

}
