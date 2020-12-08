package io.flutter.plugins.firebase.messaging.core.client;

import com.vivo.push.PushClient;
import com.vivo.push.ups.VUpsManager;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.flutter.plugins.firebase.messaging.core.LogUtils;
import io.flutter.plugins.firebase.messaging.core.PushConfig;
import io.flutter.plugins.firebase.messaging.core.client.BasePushClient;

/**
 * Created by suli on 2020/12/7
 **/
public class VivoPush extends BasePushClient {

  public VivoPush(PushConfig config) {
    super(config);
  }

  @Override
  public void register() {
    PushClient.getInstance(getContext()).turnOnPush(state -> {
      // TODO: 开关状态处理， 0代表成功
      LogUtils.d("vivo push status changed:" + state);
    });

    // 统一推送联盟接入
    VUpsManager.getInstance().turnOnPush(getContext(), codeResult -> LogUtils.d("vivo ups result:" + codeResult.getReturnCode()));

    String regId = PushClient.getInstance(getContext()).getRegId();
    LogUtils.d("vivo push regId:" + regId);
  }

  @Override
  public void unregister() {
    PushClient.getInstance(getContext()).turnOffPush(state -> LogUtils.d("vivo push status changed:" + state));
  }

  @Override
  public String getToken() throws Exception {
    return PushClient.getInstance(getContext()).getRegId();
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
