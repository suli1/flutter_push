package io.flutter.plugins.firebase.messaging.core.receiver;

import android.content.Context;

import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

import io.flutter.plugins.firebase.messaging.core.FlutterFirebaseMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.LogUtils;
import io.flutter.plugins.firebase.messaging.core.PushType;

/**
 * Created by suli on 2020/12/7
 * <p>
 * vivo push receiver
 **/
public class VivoMessagingReceiver extends OpenClientPushMessageReceiver {

  @Override
  public void onReceiveRegId(Context context, String regId) {
    LogUtils.d("Vivo onNewToken:" + regId);
    FlutterFirebaseMessagingUtils.sendTokenBroadcast(context, regId, PushType.VIVO);
  }

  @Override
  public void onNotificationMessageClicked(Context context, UPSNotificationMessage message) {
    LogUtils.d("Vivo onNotificationMessageClicked:" + message.getMsgId());
  }
}
