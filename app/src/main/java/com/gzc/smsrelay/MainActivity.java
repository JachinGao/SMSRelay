package com.gzc.smsrelay;

import android.Manifest;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gzc.smsrelay.adapter.ShowSmsAdapter;
import com.gzc.smsrelay.db.SmsDao;
import com.gzc.smsrelay.mail.MailProxy;
import com.gzc.smsrelay.mail.MessageInfo;
import com.gzc.smsrelay.receiver.ChatNotificationListenerService;
import com.gzc.smsrelay.service.SmsService;
import com.gzc.smsrelay.settings.SettingActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MailProxy.MailSendLister {
    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSION_CODE = 1;
    private static final int QUERY_FINISHED = 1000;
    private static final int MSG_SUCCESS = 1;
    private static final int MSG_ERROR = 2;

    private SmsService smsService;
    private ShowSmsAdapter smsAdapter;

    private RecyclerView mRecyclerView;
    private TextView mNote;

    private SmsDao smsDao;
    ArrayList<MessageInfo> messageInfoList;

    private String[] permissions = new String[]{Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS};


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SUCCESS:

                    if (smsAdapter.getItemCount() == 0) {
                        mNote.setVisibility(View.VISIBLE);
                    } else {
                        mNote.setVisibility(View.GONE);
                    }

                    MessageInfo info = (MessageInfo) msg.obj;

                    Log.e(TAG, "handleMessage: name = " + info.getSenderName());
                    Log.e(TAG, "handleMessage: date = " + info.getDate());
                    Log.e(TAG, "handleMessage: source = " + info.getSource());
                    Log.e(TAG, "handleMessage: content = " + info.getContent());

                    smsAdapter.addData(info);
                    smsAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(smsAdapter.getItemCount() - 1);
                    smsDao.insert(info.getSource(), info.getDate(), info.getSenderName(), info.getContent());
                    break;
                case MSG_ERROR:
                    Toast.makeText(MainActivity.this, "邮件地址或授权码配置错误,请重新配置", Toast.LENGTH_SHORT).show();
                    break;

                case QUERY_FINISHED:
                    smsAdapter.initData(messageInfoList);

                    if (smsAdapter.getItemCount() == 0) {
                        mNote.setVisibility(View.VISIBLE);
                    } else {
                        mNote.setVisibility(View.GONE);
                    }

                    smsAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(smsAdapter.getItemCount() == 0 ? 0 : smsAdapter.getItemCount() - 1);
                    break;
            }

        }
    };


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SmsService.LocalBinder binder = (SmsService.LocalBinder) service;
            smsService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();

        if (!isNotificationListenersEnabled()) {
            gotoNotificationAccessSetting();
        }

        toggleNotificationListenerService(this);

        smsDao = new SmsDao(this);
        MailProxy.getInstance().register(this, MainActivity.this);

        new Thread() {
            @Override
            public void run() {
                super.run();
                messageInfoList = smsDao.queryAll();
                handler.sendEmptyMessage(QUERY_FINISHED);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        smsDao.close();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false));

        smsAdapter = new ShowSmsAdapter();
        mRecyclerView.setAdapter(smsAdapter);


        mNote = findViewById(R.id.show_note);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            startActivity(new Intent(MainActivity.this, SettingActivity.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startForeground();
        } else if (id == R.id.nav_gallery) {
            smsService.stopForeground(true);
            smsService.stopShow();
        } else if (id == R.id.nav_share) {
            Uri uri = Uri.parse("https://github.com/JachinGao/SMSRelay/releases");    //设置跳转的网站
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.setting) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        } else if (id == R.id.clear) {
            clearDatabase();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearDatabase() {
        final AlertDialog.Builder dialog =
                new AlertDialog.Builder(this);
        dialog.setTitle("Hi");

        dialog.setMessage("确定要清空数据库么");
        dialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        smsDao.clear();
                        smsAdapter.clearData();
                        smsAdapter.notifyDataSetChanged();

                        mNote.setVisibility(View.VISIBLE);

                        Toast.makeText(MainActivity.this, "数据已清空", Toast.LENGTH_SHORT).show();
                    }
                });
        dialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        dialog.create().show();
    }

    private void requestPermission() {

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
            } else {
                Toast.makeText(this, "get all permission", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean isNotificationListenersEnabled() {
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName());
    }

    private void gotoNotificationAccessSetting() {
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    private void startForeground() {
        Intent intent = new Intent(MainActivity.this, SmsService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, ChatNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, ChatNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onSuccess(MessageInfo info) {
        Message message = Message.obtain();
        message.obj = info;
        message.what = MSG_SUCCESS;
        handler.sendMessage(message);
    }

    @Override
    public void onError(String content) {
        Message message = Message.obtain();
        message.obj = content;
        message.what = MSG_ERROR;
        handler.sendMessage(message);
    }
}
