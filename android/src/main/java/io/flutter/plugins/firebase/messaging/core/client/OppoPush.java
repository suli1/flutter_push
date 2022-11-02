package io.flutter.plugins.firebase.messaging.core.client;

//import com.heytap.msp.push.HeytapPushManager;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.flutter.plugins.firebase.messaging.core.PushConfig;
//import io.flutter.plugins.firebase.messaging.core.receiver.OppoMessagingService;

/**
 * Created by suli on 2020/12/7
 * <p>
 * OPPO push
 **/
public class OppoPush extends BasePushClient {

  public OppoPush(PushConfig config) {
    super(config);
//    HeytapPushManager.init(getContext(), LogUtils.debuggable);
  }

  @Override
  public void register() {
    //HeytapPushManager.register(getContext(), config.appKey, config.appSecret, new OppoMessagingService(getContext()));
  }

  @Override
  public void unregister() {
    //HeytapPushManager.unRegister();
  }

  @Override
  public String getToken() throws Exception {
    //return HeytapPushManager.getRegisterID();
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
