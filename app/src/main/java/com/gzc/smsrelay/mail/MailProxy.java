package com.gzc.smsrelay.mail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gzc.smsrelay.bean.Config;

public class MailProxy {

    public static final String TAG = MailProxy.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static MailProxy mailProxy;

    private MailSendLister mLister;
    private Context context;


    private String host;
    private String port;
    private String from_address;
    private String to_address;
    private String from_authorization_code;

    private MailProxy() {
    }

    public static MailProxy getInstance() {
        if (mailProxy == null) {
            synchronized (MailProxy.class) {
                if (mailProxy == null) {
                    mailProxy = new MailProxy();
                }
            }
        }
        return mailProxy;
    }


    //qq
//    private static final String HOST = "smtp.qq.com";
//    private static final String PORT = "587";
//    private static final String FROM_ADD = "770562623@qq.com"; //发送方邮箱
//    private static final String FROM_PSW = "csxjbjjcwkmnbcfb";//发送方邮箱授权码

// 163
//    private static final String HOST = "smtp.163.com";
//    private static final String PORT = "465"; //或者465  994
//    private static final String FROM_ADD = "winter_2013@163.com";
//    private static final String FROM_PSW = "iiiii";
//    private static final String TO_ADD = "2584770373@qq.com";

    public void register(MailSendLister lister, Context context) {
        mLister = lister;
        this.context = context;
        refreshMailConfigInfo();
    }


    public void refreshMailConfigInfo() {

        final SharedPreferences userSettings = context
                .getSharedPreferences(Config.SHARED_DATA_ADDRESS, Context.MODE_PRIVATE);

        host = userSettings.getString(Config.HOST, Config.HOST_DEFAULT);
        port = userSettings.getString(Config.PORT, Config.PORT_DEFAULT);

        from_address = userSettings.getString(Config.SEND_EMAIL_ADDRESS, Config.DEFAULT);
        to_address = userSettings.getString(Config.RECEIVE_EMAIL_ADDRESS, Config.DEFAULT);
        from_authorization_code = userSettings.getString(Config.AUTHORIZATION_CODE, Config.DEFAULT);
    }


    public void send(final MessageInfo info) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final MailInfo mailInfo = createMail(info);
                final MailSender sms = new MailSender();
                if (mailInfo != null) {
                    sms.sendTextMail(mailInfo);
                    //sms.sendFileMail(mailInfo, file);  可以发送附件
                }
            }
        }).start();
    }


    private MailInfo createMail(MessageInfo info) {
        if (mLister == null) {
            throw new RuntimeException("listener is null, please register first");
        }

        if (from_address == null || from_address.equals(Config.DEFAULT)) {
            mLister.onError("地址错误,请重新配置");
            return null;
        }

        String subject = "转发 | " + info.getSource() + " | " + info.getDate();
        String content = "from:" + info.getSenderName() + "\n" + info.getContent();

        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(host);
        mailInfo.setMailServerPort(port);
        mailInfo.setValidate(true);
        mailInfo.setUserName(from_address); // 你的邮箱地址
        mailInfo.setPassword(from_authorization_code);// 您的邮箱授权码
        mailInfo.setFromAddress(from_address); // 发送的邮箱
        mailInfo.setToAddress(to_address); // 发到哪个邮件去

        mailInfo.setSubject(subject); // 邮件主题
        mailInfo.setContent(content); // 邮件文本

        mLister.onSuccess(info);
        Log.e(TAG, "createMail:from_authorization_code " + from_authorization_code);
        Log.e(TAG, "createMail: subject = " + subject);
        Log.e(TAG, "createMail: content = " + content);

        return mailInfo;
    }

    public interface MailSendLister {
        void onSuccess(MessageInfo info);

        void onError(String content);
    }

}