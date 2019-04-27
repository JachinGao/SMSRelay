package com.gzc.smsrelay.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.gzc.smsrelay.mail.MessageInfo;
import com.gzc.smsrelay.mail.SendMailUtil;

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
                String date_time = null;

                Object[] object = (Object[]) extras.get("pdus");

                if (object == null) {
                    return;
                }

                for (Object pdus : object) {
                    byte[] pdusMsg = (byte[]) pdus;
                    SmsMessage sms = SmsMessage.createFromPdu(pdusMsg);
                    sender = sms.getOriginatingAddress();//发送短信的手机号
                    String str = sms.getMessageBody();//短信内容

                    Date date = new Date(sms.getTimestampMillis()); //下面是获取短信的发送时间
                    date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                    content.append(str);
                }

                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setmSender(sender);
                messageInfo.setmContent(content.toString());
                messageInfo.setmDate(date_time);

                SendMailUtil.send(messageInfo);

                String smsToast = "New SMS received from : "
                        + content.toString() + "\n'";
//                Toast.makeText(context, smsToast, Toast.LENGTH_LONG)
//                        .show();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

