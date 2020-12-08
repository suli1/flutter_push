package io.flutter.plugins.firebase.messaging.example;

import androidx.multidex.MultiDex;

import io.flutter.app.FlutterApplication;

/**
 * Created by suli on 2020/12/7
 **/
public class ExampleApplication extends FlutterApplication {

  @Override
  public void onCreate() {
    MultiDex.install(this);
    super.onCreate();
  }
}
