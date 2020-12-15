package io.flutter.plugins.firebase.messaging.core.receiver;

import android.content.Context;

import com.heytap.msp.push.callback.ICallBackResultService;

import io.flutter.plugins.firebase.messaging.core.FlutterFirebaseMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.LogUtils;
import io.flutter.plugins.firebase.messaging.core.PushType;

/**
 * Created by suli on 2020/12/7
 **/
public class OppoMessagingService implements ICallBackResultService {
  private final Context context;

  public OppoMessagingService(Context context) {
    this.context = context;
  }

  @Override
  public void onRegister(int responseCode, String registerId) {
    LogUtils.d("OPPO onNewToken:" + registerId);
    FlutterFirebaseMessagingUtils.sendTokenBroadcast(context, registerId, PushType.OPPO);
  }

  @Override
  public void onUnRegister(int responseCode) {

  }

  @Override
  public void onSetPushTime(int responseCode, String pushTime) {

  }

  // PUSH_STATUS_START = 0, PUSH_STATUS_PAUSE = 1, PUSH_STATUS_STOP = 2
  @Override
  public void onGetPushStatus(int responseCode, int status) {

  }

  // STATUS_OPEN = 0, STATUS_CLOSE = 1
  @Override
  public void onGetNotificationStatus(int responseCode, int status) {

  }
}
