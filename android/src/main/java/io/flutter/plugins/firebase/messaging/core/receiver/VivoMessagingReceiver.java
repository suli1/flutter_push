//package io.flutter.plugins.firebase.messaging.core.receiver;
//
//import android.content.Context;
//
//import com.vivo.push.sdk.OpenClientPushMessageReceiver;
//
//import io.flutter.plugins.firebase.messaging.core.FlutterMessagingUtils;
//import io.flutter.plugins.firebase.messaging.core.LogUtils;
//import io.flutter.plugins.firebase.messaging.core.PushType;
//
///**
// * Created by suli on 2020/12/7
// * <p>
// * vivo push receiver
// **/
//public class VivoMessagingReceiver extends OpenClientPushMessageReceiver {
//
//  @Override
//  public void onReceiveRegId(Context context, String regId) {
//    LogUtils.d("Vivo onNewToken:" + regId);
//    FlutterMessagingUtils.sendTokenBroadcast(context, regId, PushType.VIVO);
//  }
//}
