import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_push/firebase_messaging.dart'
    show FirebaseMessaging, MessageToken;

/// Manages & returns the users FCM token.
///
/// Also monitors token refreshes and updates state.
class TokenMonitor extends StatefulWidget {
  // ignore: public_member_api_docs
  TokenMonitor(this._builder);

  final Widget Function(String? token) _builder;

  @override
  State<StatefulWidget> createState() => _TokenMonitor();
}

class _TokenMonitor extends State<TokenMonitor> {
  String? _token;
  StreamSubscription? _subscription;

  void setToken(MessageToken? token) {
    if (token != null) {
      print('FCM Token: ${token.token}');
      setState(() {
        _token = token.token;
      });
    }
  }

  @override
  void initState() {
    super.initState();
    FirebaseMessaging.instance.getToken().then(setToken);
    _subscription = FirebaseMessaging.instance.onTokenRefresh.listen(setToken);
  }

  @override
  void dispose() {
    super.dispose();
    _subscription?.cancel();
  }

  @override
  Widget build(BuildContext context) {
    return widget._builder(_token);
  }
}
