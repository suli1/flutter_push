// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';

import 'package:async/async.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:firebase_messaging/src/platform_interface/message_token.dart';
import 'package:firebase_messaging/src/platform_interface/platform_interface/platform_interface_messaging.dart';
import 'package:firebase_messaging/src/platform_interface/utils.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';

import './mock.dart';

void main() {
  setupFirebaseMessagingMocks();
  FirebaseMessaging messaging;

  group('$FirebaseMessaging', () {
    setUpAll(() async {
      // await Firebase.initializeApp();
      FirebaseMessagingPlatform.instance = kMockMessagingPlatform;
      messaging = FirebaseMessaging.instance;
    });
    group('instance', () {
      test('returns an instance', () async {
        expect(messaging, isA<FirebaseMessaging>());
      });

      // test('returns the correct $FirebaseApp', () {
      //   expect(messaging.app, isA<FirebaseApp>());
      //   expect(messaging.app.name, defaultFirebaseAppName);
      // });
    });

    group('initialNotification', () {
      test('verify delegate method is called', () async {
        const senderId = 'test-notification';
        RemoteMessage message = RemoteMessage(senderId: senderId);
        when(kMockMessagingPlatform.getInitialMessage())
            .thenAnswer((_) => Future.value(message));

        final result = await messaging.getInitialMessage();

        expect(result, isA<RemoteMessage>());
        expect(result.senderId, senderId);

        verify(kMockMessagingPlatform.getInitialMessage());
      });
    });

    group('deleteToken', () {
      test('verify delegate method is called with correct args', () async {
        when(kMockMessagingPlatform.deleteToken())
            .thenAnswer((_) => Future.value(null));

        await messaging.deleteToken();

        verify(kMockMessagingPlatform.deleteToken());
      });
    });

    group('getAPNSToken', () {
      test('verify delegate method is called', () async {
        const apnsToken = 'test-apns';
        when(kMockMessagingPlatform.getAPNSToken())
            .thenAnswer((_) => Future.value(apnsToken));

        await messaging.getAPNSToken();

        verify(kMockMessagingPlatform.getAPNSToken());
      });
    });
    group('getToken', () {
      test('verify delegate method is called with correct args', () async {
        when(kMockMessagingPlatform.getToken()).thenReturn(null);

        await messaging.getToken();

        verify(kMockMessagingPlatform.getToken());
      });
    });

    group('onTokenRefresh', () {
      test('verify delegate method is called', () async {
        const token = 'test-token';

        when(kMockMessagingPlatform.onTokenRefresh)
            .thenAnswer((_) => Stream<MessageToken>.fromIterable(<MessageToken>[
                  MessageToken(
                    type: 'APNS',
                    token: token,
                  )
                ]));

        final StreamQueue<MessageToken> changes =
            StreamQueue<MessageToken>(messaging.onTokenRefresh);
        expect(await changes.next, isA<MessageToken>());

        verify(kMockMessagingPlatform.onTokenRefresh);
      });
    });
    group('requestPermission', () {
      test('verify delegate method is called with correct args', () async {
        when(kMockMessagingPlatform.requestPermission(
          alert: anyNamed('alert'),
          announcement: anyNamed('announcement'),
          badge: anyNamed('badge'),
          carPlay: anyNamed('carPlay'),
          criticalAlert: anyNamed('criticalAlert'),
          provisional: anyNamed('provisional'),
          sound: anyNamed('sound'),
        )).thenAnswer((_) => Future.value(androidNotificationSettings));

        // true values
        await messaging.requestPermission(
            alert: true,
            announcement: true,
            badge: true,
            carPlay: true,
            criticalAlert: true,
            provisional: true,
            sound: true);

        verify(kMockMessagingPlatform.requestPermission(
            alert: true,
            announcement: true,
            badge: true,
            carPlay: true,
            criticalAlert: true,
            provisional: true,
            sound: true));

        // false values
        await messaging.requestPermission(
            alert: false,
            announcement: false,
            badge: false,
            carPlay: false,
            criticalAlert: false,
            provisional: false,
            sound: false);

        verify(kMockMessagingPlatform.requestPermission(
            alert: false,
            announcement: false,
            badge: false,
            carPlay: false,
            criticalAlert: false,
            provisional: false,
            sound: false));

        // default values
        await messaging.requestPermission();

        verify(kMockMessagingPlatform.requestPermission(
            alert: true,
            announcement: false,
            badge: true,
            carPlay: false,
            criticalAlert: false,
            provisional: false,
            sound: true));
      });
    });

    group('subscribeToTopic', () {
      setUpAll(() {
        when(kMockMessagingPlatform.subscribeToTopic(any))
            .thenAnswer((_) => null);
      });

      test('throws AssertionError if topic is invalid', () async {
        final invalidTopic = 'test invalid = topic';

        expect(() => messaging.subscribeToTopic(invalidTopic),
            throwsAssertionError);
      });

      test('verify delegate method is called with correct args', () async {
        final topic = 'test-topic';

        await messaging.subscribeToTopic(topic);
        verify(kMockMessagingPlatform.subscribeToTopic(topic));
      });

      test('throws AssertionError for invalid topic name', () {
        expect(
            () => messaging.unsubscribeFromTopic(null), throwsAssertionError);
        verifyNever(kMockMessagingPlatform.unsubscribeFromTopic(any));
      });
    });
    group('unsubscribeFromTopic', () {
      when(kMockMessagingPlatform.unsubscribeFromTopic(any))
          .thenAnswer((_) => null);
      test('verify delegate method is called with correct args', () async {
        final topic = 'test-topic';

        await messaging.unsubscribeFromTopic(topic);
        verify(kMockMessagingPlatform.unsubscribeFromTopic(topic));
      });

      test('throws AssertionError for invalid topic name', () {
        expect(
            () => messaging.unsubscribeFromTopic(null), throwsAssertionError);
        verifyNever(kMockMessagingPlatform.unsubscribeFromTopic(any));
      });
    });
  });
}
