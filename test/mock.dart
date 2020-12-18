// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:flutter_push/src/platform_interface/method_channel/platform_interface_messaging.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

typedef Callback(MethodCall call);

final String kTestString = 'Hello World';

final MockFirebaseMessaging kMockMessagingPlatform = MockFirebaseMessaging();

setupFirebaseMessagingMocks() {
  TestWidgetsFlutterBinding.ensureInitialized();

  // MethodChannelFirebase.channel.setMockMethodCallHandler((call) async {
  //   if (call.method == 'Firebase#initializeCore') {
  //     return [
  //       {
  //         'name': defaultFirebaseAppName,
  //         'options': {
  //           'apiKey': '123',
  //           'appId': '123',
  //           'messagingSenderId': '123',
  //           'projectId': '123',
  //         },
  //         'pluginConstants': {},
  //       }
  //     ];
  //   }
  //
  //   if (call.method == 'Firebase#initializeApp') {
  //     return {
  //       'name': call.arguments['appName'],
  //       'options': call.arguments['options'],
  //       'pluginConstants': {},
  //     };
  //   }
  //
  //   return null;
  // });
  //
  // // Mock Platform Interface Methods
  // // ignore: invalid_use_of_protected_member
  // when(kMockMessagingPlatform.delegateFor()).thenReturn(kMockMessagingPlatform);
}

// Platform Interface Mock Classes

// FirebaseMessagingPlatform Mock
class MockFirebaseMessaging extends Mock
    with MockPlatformInterfaceMixin
    implements FirebaseMessagingPlatform {
  MockFirebaseMessaging() {
    TestFirebaseMessagingPlatform();
  }
}

class TestFirebaseMessagingPlatform extends FirebaseMessagingPlatform {
  TestFirebaseMessagingPlatform() : super();
}
