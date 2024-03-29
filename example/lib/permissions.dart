import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_push/firebase_messaging.dart';

/// Requests & displays the current user permissions for this device.
class Permissions extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _Permissions();
}

class _Permissions extends State<Permissions> {
  bool _requested = false;
  bool _fetching = false;
  NotificationSettings? _settings;

  void requestPermissions() async {
    setState(() {
      _fetching = true;
    });

    NotificationSettings? settings =
        await FirebaseMessaging.instance.requestPermission(
      alert: true,
      announcement: true,
      badge: true,
      carPlay: true,
      criticalAlert: true,
      // This will ensure the popup shows for users
      provisional: false,
    );

    setState(() {
      _requested = true;
      _fetching = false;
      _settings = settings;
    });
  }

  Widget row(String title, String? value) {
    return Container(
      margin: EdgeInsets.only(bottom: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text("$title:", style: TextStyle(fontWeight: FontWeight.bold)),
          if (value != null) Text(value),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_fetching) {
      return Container(child: CircularProgressIndicator());
    }

    if (!_requested) {
      return ElevatedButton(
        onPressed: requestPermissions,
        child: Text("Request Permissions"),
      );
    }

    return (Column(children: [
      row("Authorization Status", statusMap[_settings!.authorizationStatus]),
      if (defaultTargetPlatform == TargetPlatform.iOS) ...[
        row("Alert", settingsMap[_settings!.alert]),
        row("Announcement", settingsMap[_settings!.announcement]),
        row("Badge", settingsMap[_settings!.badge]),
        row("Car Play", settingsMap[_settings!.carPlay]),
        row("Lock Screen", settingsMap[_settings!.lockScreen]),
        row("Notification Center", settingsMap[_settings!.notificationCenter]),
        row("Show Previews", previewMap[_settings!.showPreviews]),
        row("Sound", settingsMap[_settings!.sound]),
      ],
      Container(
        child: ElevatedButton(
          onPressed: () => {},
          child: Text("Reload Permissions"),
        ),
      ),
    ]));
  }
}

/// Maps a [AuthorizationStatus] to a string value.
const statusMap = {
  AuthorizationStatus.authorized: 'Authorized',
  AuthorizationStatus.denied: 'Denied',
  AuthorizationStatus.notDetermined: 'Not Determined',
  AuthorizationStatus.provisional: 'Provisional',
};

/// Maps a [AppleNotificationSetting] to a string value.
const settingsMap = {
  AppleNotificationSetting.disabled: 'Disabled',
  AppleNotificationSetting.enabled: 'Enabled',
  AppleNotificationSetting.notSupported: 'Not Supported',
};

/// Maps a [AppleShowPreviewSetting] to a string value.
const previewMap = {
  AppleShowPreviewSetting.always: 'Always',
  AppleShowPreviewSetting.never: 'Never',
  AppleShowPreviewSetting.notSupported: 'Not Supported',
  AppleShowPreviewSetting.whenAuthenticated: 'Only When Authenticated',
};
