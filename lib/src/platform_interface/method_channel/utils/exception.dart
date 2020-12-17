// Copyright 2020, the Chromium project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:quiver/core.dart';

/// Catches a [PlatformException] and converts it into a [FirebaseException] if
/// it was intentionally caught on the native platform.
Exception convertPlatformException(Object exception) {
  if (exception is! Exception || exception is! PlatformException) {
    throw exception;
  }

  return platformExceptionToFirebaseException(exception as PlatformException);
}

/// Converts a [PlatformException] into a [FirebaseException].
///
/// A [PlatformException] can only be converted to a [FirebaseException] if the
/// `details` of the exception exist. Firebase returns specific codes and messages
/// which can be converted into user friendly exceptions.
FirebaseException platformExceptionToFirebaseException(
    PlatformException platformException) {
  Map<String, String> details = platformException.details != null
      ? Map<String, String>.from(platformException.details)
      : null;

  String code = 'unknown';
  String message = platformException.message;

  if (details != null) {
    code = details['code'] ?? code;
    message = details['message'] ?? message;
  }

  return FirebaseException(
      plugin: 'firebase_messaging', code: code, message: message);
}

/// A generic class which provides exceptions in a Firebase-friendly format
/// to users.
///
/// ```dart
/// try {
///   await Firebase.initializeApp();
/// } on FirebaseException catch (e) {
///   print(e.toString());
/// }
/// ```
class FirebaseException implements Exception {
  /// A generic class which provides exceptions in a Firebase-friendly format
  /// to users.
  ///
  /// ```dart
  /// try {
  ///   await Firebase.initializeApp();
  /// } catch (e) {
  ///   print(e.toString());
  /// }
  /// ```
  FirebaseException({
    @required this.plugin,
    @required this.message,
    this.code = 'unknown',
    this.stackTrace,
  });

  /// The plugin the exception is for.
  ///
  /// The value will be used to prefix the message to give more context about
  /// the exception.
  final String plugin;

  /// The long form message of the exception.
  final String message;

  /// The optional code to accommodate the message.
  ///
  /// Allows users to identify the exception from a short code-name, for example
  /// "no-app" is used when a user attempts to read a [FirebaseApp] which does
  /// not exist.
  final String code;

  /// The stack trace which provides information to the user about the call
  /// sequence that triggered an exception
  final StackTrace stackTrace;

  @override
  bool operator ==(dynamic other) {
    if (identical(this, other)) return true;
    if (other is! FirebaseException) return false;
    return other.hashCode == hashCode;
  }

  @override
  int get hashCode {
    return hash3(plugin, code, message);
  }

  @override
  String toString() {
    String output = "[$plugin/$code] $message";

    if (stackTrace != null) {
      output += "\n\n${stackTrace.toString()}";
    }

    return output;
  }
}
