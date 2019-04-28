package com.gzc.smsrelay.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.gzc.smsrelay.mail.MessageInfo;
import com.gzc.smsrelay.mail.SendMailUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatNotificationListenerService extends NotificationListenerService {

    private static final String TAG = ChatNotificationListenerService.class.getSimpleName();

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Bundle extras = sbn.getNotification().extras;
        String packageName = sbn.getPackageName();

        String title = extras.getString(Notification.EXTRA_TITLE); //通知title
        String content = extras.getString(Notification.EXTRA_TEXT); //通知内容
        int smallIconId = extras.getInt(Notification.EXTRA_SMALL_ICON); //通知小图标id
        Bitmap largeIcon = extras.getParcelable(Notification.EXTRA_LARGE_ICON); //通知的大图标，注意和获取小图标的区别


//        if (title != null && title.equals("中转站后台运行")) {
//            return;
//        }
//
//        if (packageName.contains("messaging")) {
//            return;
//        }
//
//        if (title != null && title.equals("选择键盘")) {
//            return;
//        }
//
        Log.e(TAG, "onNotificationPosted: title = " + title + ",  content = " + content + "" + ", package name = " + packageName);

        if (packageName != null && packageName.contains("com.tencent.")) {

            String substring = null;
            if (title.contains("(")) {
                int index = title.indexOf("(");
                substring = title.substring(0, index - 1);
                Log.e(TAG, "onNotificationPosted: title = " + substring + ",  content = " + content + "" + ", package name = " + packageName);
            }

            Date date = new Date(System.currentTimeMillis()); //下面是获取短信的发送时间
            @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

            MessageInfo messageInfo = new MessageInfo();
            //String mTitle = substring == null ? content : substring;


            if (packageName.equals(" com.tencent.mobileqq")){
                messageInfo.setmSender("转发|QQ|" + time);
            }else {
                messageInfo.setmSender("转发|微信|" + time);
            }
            messageInfo.setmContent(content);
            SendMailUtil.send(messageInfo);
        }


    }
}
