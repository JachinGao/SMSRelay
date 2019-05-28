package com.gzc.smsrelay.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;

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

    private static final String WE_CHAT_1 = "com.tencent.mm";
    private static final String WE_CHAT_2 = "com.tencent.wework";
    private static final String QQ = "com.tencent.mobileqq";

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Bundle extras = sbn.getNotification().extras;
        String packageName = sbn.getPackageName();

        String title = extras.getString(Notification.EXTRA_TITLE); //通知title
        String content = extras.getString(Notification.EXTRA_TEXT); //通知内容

        boolean isSend = packageName.contains(WE_CHAT_1)
                || packageName.contains(WE_CHAT_2)
                || packageName.contains(QQ);


        if (!isSend) {
            return;
        }

        Date date = new Date(System.currentTimeMillis()); //下面是获取短信的发送时间
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

        MessageInfo messageInfo = new MessageInfo();

        if (packageName.equals("com.tencent.mobileqq")) {
            messageInfo.setmSender("转发|QQ|" + time);
        } else {
            messageInfo.setmSender("转发|微信|" + time);
        }

        messageInfo.setmContent(contentTrim(title, content));
        SendMailUtil.send(messageInfo);

    }


    private String contentTrim(String title, String content) {
        String subTitle = title;
        if (title == null) {
            return "" + content;
        }
        if (title.contains("(") || title.contains(")")) {
            int start = title.indexOf("(");
            int end = title.indexOf(")");

            if (start == 0) {
                subTitle = title.substring(end, title.length() - 1);
            } else {
                subTitle = title.substring(0, start - 1);
            }
        }

        if (title.contains("[") || title.contains("]")) {
            int start = title.indexOf("(");
            int end = title.indexOf(")");

            if (start == 0) {
                subTitle = title.substring(end, title.length() - 1);
            } else {
                subTitle = title.substring(0, start - 1);
            }
        }

        return subTitle + " | " + content;


    }
}
