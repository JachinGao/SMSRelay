package com.gzc.smsrelay.settings;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gzc.smsrelay.R;
import com.gzc.smsrelay.bean.Config;
import com.gzc.smsrelay.mail.MailProxy;

public class SettingActivity extends AppCompatActivity {
    public static final String TAG = SettingActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {

        Button mSave = findViewById(R.id.ok);
        Button mClear = findViewById(R.id.clear);

        final EditText mHost = findViewById(R.id.host);
        final EditText mPort = findViewById(R.id.port);
        final EditText mSendEmail = findViewById(R.id.send_email);
        final EditText mReceiveEmail = findViewById(R.id.receive_email);
        final EditText mAuthorizationCode = findViewById(R.id.authorization_code);


        final SharedPreferences userSettings = getSharedPreferences(Config.SHARED_DATA_ADDRESS, MODE_PRIVATE);

        String mHostStr = userSettings.getString(Config.HOST, Config.HOST_DEFAULT);
        String mPortStr = userSettings.getString(Config.PORT, Config.PORT_DEFAULT);

        String mSendStr = userSettings.getString(Config.SEND_EMAIL_ADDRESS, Config.DEFAULT);
        String mAuthorizationCodeStr = userSettings.getString(Config.AUTHORIZATION_CODE, Config.DEFAULT);
        String mReceiveStr = userSettings.getString(Config.RECEIVE_EMAIL_ADDRESS, Config.DEFAULT);

        mHost.setText(mHostStr);
        mPort.setText(mPortStr);
        mSendEmail.setText(mSendStr);
        mAuthorizationCode.setText(mAuthorizationCodeStr);
        mReceiveEmail.setText(mReceiveStr);


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mHostStr = mHost.getText().toString();
                String mPortStr = mPort.getText().toString();
                String mSend = mSendEmail.getText().toString();
                String mCode = mAuthorizationCode.getText().toString();
                String mReceive = mReceiveEmail.getText().toString();


                SharedPreferences.Editor edit = userSettings.edit();
                edit.putString(Config.HOST,mHostStr);
                edit.putString(Config.PORT,mPortStr);
                edit.putString(Config.SEND_EMAIL_ADDRESS,mSend);
                edit.putString(Config.AUTHORIZATION_CODE,mCode);
                edit.putString(Config.RECEIVE_EMAIL_ADDRESS,mReceive);
                edit.apply();
                MailProxy.getInstance().refreshMailConfigInfo();
                finish();
            }
        });
        
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHost.setText(Config.DEFAULT);
                mPort.setText(Config.DEFAULT);
                mSendEmail.setText(Config.DEFAULT);
                mAuthorizationCode.setText(Config.DEFAULT);
                mReceiveEmail.setText(Config.DEFAULT);

                SharedPreferences.Editor edit = userSettings.edit();
                edit.clear();
                edit.apply();
                MailProxy.getInstance().refreshMailConfigInfo();
                Toast.makeText(SettingActivity.this, "数据已清除", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
