package com.gzc.smsrelay.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.gzc.smsrelay.mail.MessageInfo;
import com.gzc.smsrelay.mail.MailProxy;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {
    public static final String TAG = SmsReceiver.class.getSimpleName();
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            if (SMS_RECEIVED.equals(intent.getAction())) {

                Bundle extras = intent.getExtras();

                if (extras == null) {
                    return;
                }

                StringBuilder content = new StringBuilder();
                String sender = null;
                SmsMessage sms=null;

                Object[] object = (Object[]) extras.get("pdus");

                if (object == null) {
                    return;
                }

                for (Object pd : object) {
                    byte[] pdMsg = (byte[]) pd;
                    sms = SmsMessage.createFromPdu(pdMsg);
                    sender = sms.getOriginatingAddress();//发送短信的手机号
                    String str = sms.getMessageBody();//短信内容
                    content.append(str);
                }

                Date date = new Date(sms == null ? System.currentTimeMillis() : sms.getTimestampMillis()); //下面是获取短信的发送时间
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setSource("短信");
                messageInfo.setSenderName(sender);
                messageInfo.setDate(time);
                messageInfo.setContent(content.toString());

                MailProxy.getInstance().send(messageInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

