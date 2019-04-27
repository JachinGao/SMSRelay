package com.gzc.smsrelay.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import android.util.Log;

import com.gzc.smsrelay.MainActivity;
import com.gzc.smsrelay.R;

public class SMSService extends Service {

    public static final String TAG = SMSService.class.getSimpleName();
    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";

    private NotificationManager notificationManager = null;
    private Notification.Builder builder = null;
    boolean isCreateChannel = false;
    private static long cnt = 0;
    private static boolean isRunning = false;
    private LocalBinder mBinder = new LocalBinder();
    private Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Log.e(TAG, "onCreate: ");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!isRunning) {
            startForeground(1, buildNotification());
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new ProgressRefresh(), 1000);
            isRunning = true;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    public void stopShow() {
        isRunning = false;
    }


    private Notification buildNotification() {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName();
            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);

                notificationChannel.setSound(null, null);
                notificationChannel.enableLights(false);//是否在桌面icon右上角展示小圆点
                notificationChannel.enableVibration(false);
                notificationChannel.setVibrationPattern(new long[]{0});
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("中转站后台运行")
                .setContentText("运行时间")
                .setContentIntent(pi)
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }


    public class LocalBinder extends Binder {
        public SMSService getService() {
            return SMSService.this;
        }
    }

    private class ProgressRefresh implements Runnable {
        @Override
        public void run() {
            cnt++;

            if (isRunning) {
                int s = (int) (cnt % 60);
                int m = (int) ((cnt / 60) % 60);
                long h = (cnt / 60) / 60 % 24;
                int d = (int) (cnt / 3600 / 24);

                String s1 = s < 10 ? "0" + s : "" + s;
                String m1 = m < 10 ? "0" + m : "" + m;
                String h1 = h < 10 ? "0" + h : "" + h;
                String d1 = d < 10 ? "0" + d : "" + d;

                builder.setContentText("已运行时间：" + d1 + "天  " + h1 + ":" + m1 + ":" + s1);
                notificationManager.notify(1, builder.build());
            }
            Log.e(TAG, "run: time = " + cnt + ",  is running = " + isRunning);
            handler.postDelayed(this, 1000);

        }
    }
}
