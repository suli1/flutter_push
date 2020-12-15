// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

library firebase_messaging;

import 'dart:async';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_core_platform_interface/firebase_core_platform_interface.dart'
    show FirebasePluginPlatform;
import 'package:firebase_messaging/src/platform_interface/ios_notification_settings.dart';
import 'package:firebase_messaging/src/platform_interface/notification_settings.dart';
import 'package:firebase_messaging/src/platform_interface/platform_interface/platform_interface_messaging.dart';
import 'package:firebase_messaging/src/platform_interface/remote_message.dart';
import 'package:firebase_messaging/src/platform_interface/types.dart';

export 'package:firebase_messaging/src/platform_interface/ios_notification_settings.dart';
export 'package:firebase_messaging/src/platform_interface/notification_settings.dart';
export 'package:firebase_messaging/src/platform_interface/remote_message.dart';
export 'package:firebase_messaging/src/platform_interface/remote_notification.dart';
export 'package:firebase_messaging/src/platform_interface/types.dart';

part 'src/messaging.dart';
