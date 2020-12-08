package io.flutter.plugins.firebase.messaging.core;

import android.content.Context;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by suli on 2020/12/7
 **/
public interface IPush {

  Context getContext();

  void register();

  void unregister();

  String getToken() throws Exception;

  void deleteToken() throws Exception;

  void subscribeToTopic(Map<String, Object> arguments) throws ExecutionException, InterruptedException;

  void unsubscribeFromTopic(Map<String, Object> arguments) throws ExecutionException, InterruptedException;
}
