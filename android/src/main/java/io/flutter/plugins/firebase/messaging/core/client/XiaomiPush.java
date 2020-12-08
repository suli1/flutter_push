package io.flutter.plugins.firebase.messaging.core.client;

import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.flutter.plugins.firebase.messaging.core.PushConfig;

/**
 * Created by suli on 2020/12/7
 **/
public class XiaomiPush extends BasePushClient {

  public XiaomiPush(PushConfig config) {
    super(config);
  }

  @Override
  public void register() {
    MiPushClient.registerPush(getContext(), config.appId, config.appKey);
  }

  @Override
  public void unregister() {
    MiPushClient.unregisterPush(getContext());
  }

  @Override
  public String getToken() throws Exception {
    return MiPushClient.getRegId(getContext());
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
