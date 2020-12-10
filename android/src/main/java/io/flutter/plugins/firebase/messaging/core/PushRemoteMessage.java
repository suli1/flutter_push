package io.flutter.plugins.firebase.messaging.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.vivo.push.model.UPSNotificationMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by suli on 2020/12/9
 **/
public class PushRemoteMessage implements Parcelable {
  public static PushRemoteMessage buildFromFcm(
      com.google.firebase.messaging.RemoteMessage remoteMessage) {
    PushRemoteMessage message = new PushRemoteMessage();
    message.collapseKey = remoteMessage.getCollapseKey();
    message.from = remoteMessage.getFrom();
    message.to = remoteMessage.getTo();
    message.messageId = remoteMessage.getMessageId();
    message.messageType = remoteMessage.getMessageType();
    message.data = remoteMessage.getData();
    message.ttl = remoteMessage.getTtl();
    message.sentTime = remoteMessage.getSentTime();

    com.google.firebase.messaging.RemoteMessage.Notification remoteNotification =
        remoteMessage.getNotification();
    if (remoteNotification != null) {
      message.notification = new Notification();
      message.notification.title = remoteNotification.getTitle();
      message.notification.titleLocKey = remoteNotification.getTitleLocalizationKey();
      message.notification.titleLocArgs = remoteNotification.getTitleLocalizationArgs();
      message.notification.body = remoteNotification.getBody();
      message.notification.bodyLocKey = remoteNotification.getBodyLocalizationKey();
      message.notification.bodyLocArgs = remoteNotification.getBodyLocalizationArgs();

      message.notification.android = new AndroidNotification();
      message.notification.android.channelId = remoteNotification.getChannelId();
      message.notification.android.clickAction = remoteNotification.getClickAction();
      message.notification.android.color = remoteNotification.getColor();
      message.notification.android.smallIcon = remoteNotification.getIcon();
      if (remoteNotification.getImageUrl() != null) {
        message.notification.android.imageUrl = remoteNotification.getImageUrl().toString();
      }
      if (remoteNotification.getLink() != null) {
        message.notification.android.link = remoteNotification.getLink().toString();
      }
      message.notification.android.priority = remoteNotification.getNotificationPriority();
      message.notification.android.sound = remoteNotification.getSound();
      message.notification.android.ticker = remoteNotification.getTicker();
      message.notification.android.visibility = remoteNotification.getVisibility();
    }
    return message;
  }

  public static PushRemoteMessage buildFromHuawei(com.huawei.hms.push.RemoteMessage remoteMessage) {
    PushRemoteMessage message = new PushRemoteMessage();
    message.collapseKey = remoteMessage.getCollapseKey();
    message.from = remoteMessage.getFrom();
    message.to = remoteMessage.getTo();
    message.messageId = remoteMessage.getMessageId();
    message.messageType = remoteMessage.getMessageType();
    message.data = remoteMessage.getDataOfMap();
    message.ttl = remoteMessage.getTtl();
    message.sentTime = remoteMessage.getSentTime();

    message.notification = new Notification();
    com.huawei.hms.push.RemoteMessage.Notification remoteNotification =
        remoteMessage.getNotification();
    message.notification.title = remoteNotification.getTitle();
    message.notification.titleLocKey = remoteNotification.getTitleLocalizationKey();
    message.notification.titleLocArgs = remoteNotification.getTitleLocalizationArgs();
    message.notification.body = remoteNotification.getBody();
    message.notification.bodyLocKey = remoteNotification.getBodyLocalizationKey();
    message.notification.bodyLocArgs = remoteNotification.getBodyLocalizationArgs();

    message.notification.android = new AndroidNotification();
    message.notification.android.channelId = remoteNotification.getChannelId();
    message.notification.android.clickAction = remoteNotification.getClickAction();
    message.notification.android.color = remoteNotification.getColor();
    message.notification.android.smallIcon = remoteNotification.getIcon();
    message.notification.android.imageUrl = remoteNotification.getImageUrl().toString();
    message.notification.android.link = remoteNotification.getLink().toString();
    message.notification.android.sound = remoteNotification.getSound();
    message.notification.android.ticker = remoteNotification.getTicker();
    message.notification.android.visibility = remoteNotification.getVisibility();

    return message;
  }

  public static PushRemoteMessage buildFromXiaomi(MiPushMessage remoteMessage) {
    PushRemoteMessage message = new PushRemoteMessage();
    message.messageId = remoteMessage.getMessageId();
    message.from = remoteMessage.getTopic();
    message.messageType = String.valueOf(remoteMessage.getMessageType());
    message.data = remoteMessage.getExtra();
    message.notification = new Notification();
    message.notification.body = remoteMessage.getContent();
    message.notification.title = remoteMessage.getTitle();
    message.notification.android = new AndroidNotification();

    return message;
  }

  public static PushRemoteMessage buildFromVivo(UPSNotificationMessage remoteMessage) {
    PushRemoteMessage message = new PushRemoteMessage();
    message.messageId = String.valueOf(remoteMessage.getMsgId());
    message.data = remoteMessage.getParams();
    message.notification = new Notification();
    message.notification.title = remoteMessage.getTitle();
    message.notification.body = remoteMessage.getContent();
    message.notification.android = new AndroidNotification();

    return message;
  }


  public String collapseKey;
  public String from;
  public String to;
  public String messageId;
  public String messageType;
  public Map<String, String> data;
  public int ttl;
  public long sentTime;
  public Notification notification;

  public PushRemoteMessage() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.collapseKey);
    dest.writeString(this.from);
    dest.writeString(this.to);
    dest.writeString(this.messageId);
    dest.writeString(this.messageType);
    dest.writeInt(this.data.size());
    for (Map.Entry<String, String> entry : this.data.entrySet()) {
      dest.writeString(entry.getKey());
      dest.writeString(entry.getValue());
    }
    dest.writeInt(this.ttl);
    dest.writeLong(this.sentTime);
    dest.writeParcelable(this.notification, flags);
  }

  protected PushRemoteMessage(Parcel in) {
    this.collapseKey = in.readString();
    this.from = in.readString();
    this.to = in.readString();
    this.messageId = in.readString();
    this.messageType = in.readString();
    int dataSize = in.readInt();
    this.data = new HashMap<>(dataSize);
    for (int i = 0; i < dataSize; i++) {
      String key = in.readString();
      String value = in.readString();
      this.data.put(key, value);
    }
    this.ttl = in.readInt();
    this.sentTime = in.readLong();
    this.notification = in.readParcelable(Notification.class.getClassLoader());
  }

  public static final Creator<PushRemoteMessage> CREATOR = new Creator<PushRemoteMessage>() {
    @Override
    public PushRemoteMessage createFromParcel(Parcel source) {
      return new PushRemoteMessage(source);
    }

    @Override
    public PushRemoteMessage[] newArray(int size) {
      return new PushRemoteMessage[size];
    }
  };


  public static class Notification implements Parcelable {
    public String title;
    public String titleLocKey;
    public String[] titleLocArgs;
    public String body;
    public String bodyLocKey;
    public String[] bodyLocArgs;
    public AndroidNotification android;


    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.title);
      dest.writeString(this.titleLocKey);
      dest.writeStringArray(this.titleLocArgs);
      dest.writeString(this.body);
      dest.writeString(this.bodyLocKey);
      dest.writeStringArray(this.bodyLocArgs);
      dest.writeParcelable(this.android, flags);
    }

    public Notification() {
    }

    protected Notification(Parcel in) {
      this.title = in.readString();
      this.titleLocKey = in.readString();
      this.titleLocArgs = in.createStringArray();
      this.body = in.readString();
      this.bodyLocKey = in.readString();
      this.bodyLocArgs = in.createStringArray();
      this.android = in.readParcelable(AndroidNotification.class.getClassLoader());
    }

    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
      @Override
      public Notification createFromParcel(Parcel source) {
        return new Notification(source);
      }

      @Override
      public Notification[] newArray(int size) {
        return new Notification[size];
      }
    };
  }

  public static class AndroidNotification implements Parcelable {
    public String channelId;
    public String clickAction;
    public String color;
    public String smallIcon;
    public String imageUrl;
    public String link;
    public Integer priority;
    public String sound;
    public String ticker;
    public Integer visibility;


    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.channelId);
      dest.writeString(this.clickAction);
      dest.writeString(this.color);
      dest.writeString(this.smallIcon);
      dest.writeString(this.imageUrl);
      dest.writeString(this.link);
      dest.writeValue(this.priority);
      dest.writeString(this.sound);
      dest.writeString(this.ticker);
      dest.writeValue(this.visibility);
    }

    public AndroidNotification() {
    }

    protected AndroidNotification(Parcel in) {
      this.channelId = in.readString();
      this.clickAction = in.readString();
      this.color = in.readString();
      this.smallIcon = in.readString();
      this.imageUrl = in.readString();
      this.link = in.readString();
      this.priority = (Integer) in.readValue(Integer.class.getClassLoader());
      this.sound = in.readString();
      this.ticker = in.readString();
      this.visibility = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<AndroidNotification> CREATOR = new Parcelable.Creator<AndroidNotification>() {
      @Override
      public AndroidNotification createFromParcel(Parcel source) {
        return new AndroidNotification(source);
      }

      @Override
      public AndroidNotification[] newArray(int size) {
        return new AndroidNotification[size];
      }
    };
  }
}

