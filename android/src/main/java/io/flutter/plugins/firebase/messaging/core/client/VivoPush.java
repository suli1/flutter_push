package io.flutter.plugins.firebase.messaging.core.client;

//import com.vivo.push.PushClient;

import java.util.Map;
import java.util.concurrent.ExecutionException;

//import io.flutter.plugins.firebase.messaging.core.LogUtils;
import io.flutter.plugins.firebase.messaging.core.PushConfig;

/**
 * Created by suli on 2020/12/7
 * <p>
 * Vivo push
 **/
public class VivoPush extends BasePushClient {

  public VivoPush(PushConfig config) {
    super(config);
  }

  @Override
  public void register() {
    //PushClient.getInstance(getContext()).initialize();
    //
    //PushClient.getInstance(getContext()).turnOnPush(state -> {
    //  LogUtils.d("vivo push status changed:" + state);
    //});
  }

  @Override
  public void unregister() {
    //PushClient.getInstance(getContext()).turnOffPush(state -> LogUtils.d("vivo push status changed:" + state));
  }

  @Override
  public String getToken() throws Exception {
    //return PushClient.getInstance(getContext()).getRegId();
    return  null;
  }

  @Override
  public void deleteToken() throws Exception {

  }

  @Override
  public void subscribeToTopic(Map<String, Object> arguments) throws ExecutionException, InterruptedException {
    throw new RuntimeException("Not implement");
  }

  @Override
  public void unsubscribeFromTopic(Map<String, Object> arguments) throws ExecutionException, InterruptedException {
    throw new RuntimeException("Not implement");
  }
}
