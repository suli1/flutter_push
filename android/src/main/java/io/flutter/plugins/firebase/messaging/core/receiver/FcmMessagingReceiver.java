// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebase.messaging.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

import io.flutter.plugins.firebase.messaging.core.FlutterFirebaseMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.LogUtils;
import io.flutter.plugins.firebase.messaging.core.PushRemoteMessage;

public class FcmMessagingReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    LogUtils.d("broadcast received for FCM message");

    PushRemoteMessage remoteMessage = PushRemoteMessage.buildFromFcm(new RemoteMessage(intent.getExtras()));
    FlutterFirebaseMessagingUtils.sendMessageBroadcast(context, remoteMessage);
  }
}