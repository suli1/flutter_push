package io.flutter.plugins.firebase.messaging.core.client;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.Metadata;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import io.flutter.plugins.firebase.messaging.core.FlutterFirebaseMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.PushConfig;

/**
 * Created by suli on 2020/12/8
 **/
public class FcmPush extends BasePushClient {

  public FcmPush(PushConfig config) {
    super(config);
  }

  @Override
  public void register() {
    FirebaseMessaging.getInstance();
  }

  @Override
  public void unregister() {
    FirebaseMessaging.getInstance().deleteToken();
  }

  @Override
  public String getToken() throws IOException {
    String senderId = Metadata.getDefaultSenderId(FirebaseApp.getInstance());
    return FirebaseInstanceId.getInstance().getToken(senderId, "*");
  }

  @Override
  public void deleteToken() throws IOException {
    String senderId = Metadata.getDefaultSenderId(FirebaseApp.getInstance());
    FirebaseInstanceId.getInstance().deleteToken(senderId, "*");
  }

  @Override
  public void subscribeToTopic(Map<String, Object> arguments) throws ExecutionException, InterruptedException {
    FirebaseMessaging firebaseMessaging =
      FlutterFirebaseMessagingUtils.getFirebaseMessagingForArguments(arguments);
    String topic = (String) Objects.requireNonNull(arguments.get("topic"));
    Tasks.await(firebaseMessaging.subscribeToTopic(topic));
  }

  @Override
  public void unsubscribeFromTopic(Map<String, Object> arguments) throws ExecutionException, InterruptedException {
    FirebaseMessaging firebaseMessaging =
      FlutterFirebaseMessagingUtils.getFirebaseMessagingForArguments(arguments);
    String topic = (String) Objects.requireNonNull(arguments.get("topic"));
    Tasks.await(firebaseMessaging.unsubscribeFromTopic(topic));
  }
}
