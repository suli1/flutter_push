package io.flutter.plugins.firebase.messaging.core.receiver;

import android.content.Context;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

import io.flutter.plugins.firebase.messaging.core.FlutterMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.LogUtils;
import io.flutter.plugins.firebase.messaging.core.PushRemoteMessage;
import io.flutter.plugins.firebase.messaging.core.PushType;

/**
 * Created by suli on 2020/12/7
 **/
public class XiaomiMessagingReceiver extends PushMessageReceiver {
  private String mRegId;

  @Override
  public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
    LogUtils.d("xiaomi through message arrived:" + message.getTitle());
    FlutterMessagingUtils.sendMessageBroadcast(context, PushRemoteMessage.buildFromXiaomi(message));
  }

  @Override
  public void onNotificationMessageClicked(Context context, MiPushMessage message) {
    LogUtils.d("xiaomi notification clicked:" + message.getMessageId());
  }

  @Override
  public void onNotificationMessageArrived(Context context, MiPushMessage message) {
    LogUtils.d("xiaomi notification message arrived:" + message.getTitle());
    FlutterMessagingUtils.sendMessageBroadcast(context, PushRemoteMessage.buildFromXiaomi(message));
  }

  @Override
  public void onCommandResult(Context context, MiPushCommandMessage message) {
    String command = message.getCommand();
    List<String> arguments = message.getCommandArguments();
    String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
    String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
    if (MiPushClient.COMMAND_REGISTER.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mRegId = cmdArg1;
        LogUtils.d("xiaomi onCommandResult newToken:" + mRegId);
        FlutterMessagingUtils.sendTokenBroadcast(context, mRegId, PushType.XIAO_MI);
      }
    } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        LogUtils.d("xiaomi onCommandResult alias:" + cmdArg1);
      }
    } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        LogUtils.d("xiaomi onCommandResult alias:" + cmdArg1);
      }
    } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        LogUtils.d("xiaomi onCommandResult topic:" + cmdArg1);
      }
    } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        LogUtils.d("xiaomi onCommandResult topic:" + cmdArg1);
      }
    } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        LogUtils.d("xiaomi onCommandResult startTime:" + cmdArg1 + ",endTime:" + cmdArg2);
      }
    }
  }

  @Override
  public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
    String command = message.getCommand();
    List<String> arguments = message.getCommandArguments();
    String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
//    String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
    if (MiPushClient.COMMAND_REGISTER.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mRegId = cmdArg1;
        LogUtils.d("xiaomi onReceiveRegisterResult newToken:" + mRegId);
        FlutterMessagingUtils.sendTokenBroadcast(context, mRegId, PushType.XIAO_MI);
      }
    }
  }
}
