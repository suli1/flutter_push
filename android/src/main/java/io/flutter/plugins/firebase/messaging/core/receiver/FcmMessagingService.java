// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebase.messaging.core.receiver;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.flutter.plugins.firebase.messaging.core.FlutterMessagingUtils;
import io.flutter.plugins.firebase.messaging.core.LogUtils;
import io.flutter.plugins.firebase.messaging.core.PushType;

public class FcmMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        LogUtils.d("FMC onNewToken:" + token);
        FlutterMessagingUtils.sendTokenBroadcast(getApplicationContext(), token, PushType.FCM);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Added for commenting purposes;
        // We don't handle the message here as we already handle it in the receiver and don't want to duplicate.
    }
}
