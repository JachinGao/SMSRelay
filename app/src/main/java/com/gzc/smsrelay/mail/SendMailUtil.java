package com.gzc.smsrelay.mail;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gzc.smsrelay.SMSApplication;
import com.gzc.smsrelay.bean.Bean;

import java.io.File;

public class SendMailUtil {
    //qq
//    private static final String HOST = "smtp.qq.com";
//    private static final String PORT = "587";
//    private static final String FROM_ADD = "770562623@qq.com"; //发送方邮箱
//    private static final String FROM_PSW = "csxjbjjcwkmnbcfb";//发送方邮箱授权码

    //163
//    private static final String HOST = "smtp.163.com";
//    private static final String PORT = "465"; //或者465  994
//    private static final String FROM_ADD = "winter_2013@163.com";
//    private static final String FROM_PSW = "iiiii";
//    private static final String TO_ADD = "2584770373@qq.com";

    //可以发送附件
    public static void send(final File file) {
        final MailInfo mailInfo = createMail(null);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo, file);
            }
        }).start();
    }

    public static void send(final MessageInfo info) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final MailInfo mailInfo = createMail(info);
                final MailSender sms = new MailSender();
                if (mailInfo != null) {
                    sms.sendTextMail(mailInfo);
                }
            }
        }).start();
    }


    private static MailInfo createMail(MessageInfo info) {

        final SharedPreferences userSettings = SMSApplication.getAppContext()
                .getSharedPreferences(Bean.SHARED_DATA_ADDRESS, Context.MODE_PRIVATE);

        String host = userSettings.getString(Bean.HOST, Bean.HOST_DEFAULT);
        String port = userSettings.getString(Bean.PORT, Bean.PORT_DEFAULT);

        String from_address = userSettings.getString(Bean.SEND_EMAIL_ADDRESS, Bean.DEFAULT);
        String from_authorization_code = userSettings.getString(Bean.AUTHORIZATION_CODE, Bean.DEFAULT);
        String to_address = userSettings.getString(Bean.RECEIVE_EMAIL_ADDRESS, Bean.DEFAULT);


        if (from_address == null || from_address.equals(Bean.DEFAULT)) {
            return null;
        }

        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(host);
        mailInfo.setMailServerPort(port);
        mailInfo.setValidate(true);
        mailInfo.setUserName(from_address); // 你的邮箱地址
        mailInfo.setPassword(from_authorization_code);// 您的邮箱授权码
        mailInfo.setFromAddress(from_address); // 发送的邮箱
        mailInfo.setToAddress(to_address); // 发到哪个邮件去
        mailInfo.setSubject(info.getmSender() == null ? "title" : info.getmSender()); // 邮件主题
        mailInfo.setContent(info.getmContent() == null ? "test" : info.getmContent()); // 邮件文本

        Log.e("info", "createMail: title = " + info.getmSender() + ", content = " + info.getmContent());
        return mailInfo;
    }

}