package io.flutter.plugins.firebase.messaging.core;

/**
 * Created by suli on 2020/12/7
 **/
public class PushConfig {
  public PushType type;
  public String appId;
  public String appKey;
  public String appSecret;
  public Class<? extends IPush> client;
}
