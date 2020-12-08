package io.flutter.plugins.firebase.messaging.core.client;

import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.flutter.plugins.firebase.messaging.core.PushConfig;

/**
 * Created by suli on 2020/12/7
 * <p>
 * hms push
 **/
public class HuaweiPush extends BasePushClient {
  // 输入token标识"HCM"
  final String tokenScope = "HCM";

  public HuaweiPush(PushConfig config) {
    super(config);
  }

  @Override
  public void register() {
  }

  @Override
  public void unregister() {
    try {
      HmsInstanceId.getInstance(getContext()).deleteToken(config.appId, tokenScope);
    } catch (ApiException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getToken() throws ApiException {
    return HmsInstanceId.getInstance(getContext()).getToken(config.appId, tokenScope);
  }

  @Override
  public void deleteToken() throws ApiException {
    HmsInstanceId.getInstance(getContext()).deleteToken(config.appId, tokenScope);
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
