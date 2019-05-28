package com.gzc.smsrelay.settings;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gzc.smsrelay.R;
import com.gzc.smsrelay.bean.Bean;

public class SettingActivity extends AppCompatActivity {
    public static final String TAG = SettingActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {

        Button mButton = findViewById(R.id.ok);
        Button mClear = findViewById(R.id.clear);

        final EditText mHost = findViewById(R.id.host);
        final EditText mPort = findViewById(R.id.port);
        final EditText mSendEmail = findViewById(R.id.send_email);
        final EditText mReceiveEmail = findViewById(R.id.receive_email);
        final EditText mAuthorizationCode = findViewById(R.id.authorization_code);


        final SharedPreferences userSettings = getSharedPreferences(Bean.SHARED_DATA_ADDRESS, MODE_PRIVATE);

        String mHostStr = userSettings.getString(Bean.HOST, Bean.HOST_DEFAULT);
        String mPortStr = userSettings.getString(Bean.PORT, Bean.PORT_DEFAULT);

        String mSendStr = userSettings.getString(Bean.SEND_EMAIL_ADDRESS, Bean.DEFAULT);
        String mAuthorizationCodeStr = userSettings.getString(Bean.AUTHORIZATION_CODE, Bean.DEFAULT);
        String mReceiveStr = userSettings.getString(Bean.RECEIVE_EMAIL_ADDRESS, Bean.DEFAULT);

        mHost.setText(mHostStr);
        mPort.setText(mPortStr);
        mSendEmail.setText(mSendStr);
        mAuthorizationCode.setText(mAuthorizationCodeStr);
        mReceiveEmail.setText(mReceiveStr);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mHostStr = mHost.getText().toString();
                String mPortStr = mPort.getText().toString();
                String mSend = mSendEmail.getText().toString();
                String mCode = mAuthorizationCode.getText().toString();
                String mReceive = mReceiveEmail.getText().toString();


                SharedPreferences.Editor edit = userSettings.edit();
                edit.putString(Bean.HOST,mHostStr);
                edit.putString(Bean.PORT,mPortStr);
                edit.putString(Bean.SEND_EMAIL_ADDRESS,mSend);
                edit.putString(Bean.AUTHORIZATION_CODE,mCode);
                edit.putString(Bean.RECEIVE_EMAIL_ADDRESS,mReceive);
                edit.apply();

                finish();
            }
        });
        
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHost.setText(Bean.DEFAULT);
                mPort.setText(Bean.DEFAULT);
                mSendEmail.setText(Bean.DEFAULT);
                mAuthorizationCode.setText(Bean.DEFAULT);
                mReceiveEmail.setText(Bean.DEFAULT);

                SharedPreferences.Editor edit = userSettings.edit();
                edit.clear();
                edit.apply();
                Toast.makeText(SettingActivity.this, "数据已清除", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
