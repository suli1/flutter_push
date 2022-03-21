package io.flutter.plugins.firebase.messaging.core.client;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.messaging.FirebaseMessaging;
import io.flutter.plugins.firebase.messaging.core.FlutterMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.PushConfig;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Created by suli on 2020/12/8
 **/
public class FcmPush extends BasePushClient {

  public FcmPush(PushConfig config) {
    super(config);
  }

  @Override
  public void register() {
    FirebaseMessaging.getInstance().setAutoInitEnabled(true);
  }

  @Override
  public void unregister() {
    FirebaseMessaging.getInstance().deleteToken();
  }

  @Override
  public String getToken() {
    try {
      Task<String> tokenTask = FirebaseMessaging.getInstance().getToken();
      Tasks.await(tokenTask);
      return tokenTask.getResult();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void deleteToken() throws ExecutionException, InterruptedException {
    Tasks.await(FirebaseMessaging.getInstance().deleteToken());
  }

  @Override
  public void subscribeToTopic(Map<String, Object> arguments)
      throws ExecutionException, InterruptedException {
    FirebaseMessaging firebaseMessaging =
        FlutterMessagingUtils.getFirebaseMessagingForArguments(arguments);
    String topic = (String) Objects.requireNonNull(arguments.get("topic"));
    Tasks.await(firebaseMessaging.subscribeToTopic(topic));
  }

  @Override
  public void unsubscribeFromTopic(Map<String, Object> arguments)
      throws ExecutionException, InterruptedException {
    FirebaseMessaging firebaseMessaging =
        FlutterMessagingUtils.getFirebaseMessagingForArguments(arguments);
    String topic = (String) Objects.requireNonNull(arguments.get("topic"));
    Tasks.await(firebaseMessaging.unsubscribeFromTopic(topic));
  }
}
