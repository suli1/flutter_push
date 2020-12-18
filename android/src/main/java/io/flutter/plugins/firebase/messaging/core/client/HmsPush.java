package io.flutter.plugins.firebase.messaging.core.client;

import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.flutter.plugins.firebase.messaging.core.PushConfig;

/**
 * Created by suli on 2020/12/7
 * <p>
 * hms push
 **/
public class HmsPush extends BasePushClient {

  public HmsPush(PushConfig config) {
    super(config);
  }

  @Override
  public void register() {
    HmsMessaging.getInstance(getContext()).setAutoInitEnabled(true);
  }

  @Override
  public void unregister() {
    try {
      HmsInstanceId.getInstance(getContext()).deleteToken(config.appId, HmsMessaging.DEFAULT_TOKEN_SCOPE);
    } catch (ApiException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getToken() throws Exception {
    return HmsInstanceId.getInstance(getContext()).getToken(config.appId, HmsMessaging.DEFAULT_TOKEN_SCOPE);
  }

  @Override
  public void deleteToken() throws Exception {
    HmsInstanceId.getInstance(getContext()).deleteToken(config.appId, HmsMessaging.DEFAULT_TOKEN_SCOPE);
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
