package io.flutter.plugins.firebase.messaging.core.client;

import android.content.Context;

import io.flutter.plugins.firebase.messaging.ContextHolder;
import io.flutter.plugins.firebase.messaging.core.IPush;
import io.flutter.plugins.firebase.messaging.core.PushConfig;

/**
 * Created by suli on 2020/12/7
 **/
abstract class BasePushClient implements IPush {
  protected PushConfig config;

  public BasePushClient(PushConfig config) {
    this.config = config;
  }

  @Override
  public Context getContext() {
    return ContextHolder.getApplicationContext();
  }
}
