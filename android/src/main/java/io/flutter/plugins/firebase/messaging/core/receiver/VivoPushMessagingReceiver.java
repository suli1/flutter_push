package io.flutter.plugins.firebase.messaging.core.receiver;

import android.content.Context;

import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

import io.flutter.plugins.firebase.messaging.core.FlutterFirebaseMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.LogUtils;

/**
 * Created by suli on 2020/12/7
 * <p>
 * vivo push receiver
 **/
public class VivoPushMessagingReceiver extends OpenClientPushMessageReceiver {

  @Override
  public void onReceiveRegId(Context context, String regId) {
    LogUtils.d("Vivo onNewToken:" + regId);
    FlutterFirebaseMessagingUtils.sendTokenBroadcast(context, regId);
  }

  @Override
  public void onNotificationMessageClicked(Context context, UPSNotificationMessage message) {

  }
}
