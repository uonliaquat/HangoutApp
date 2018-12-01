package com.example.uonliaquatarain.hangoutapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button login_btn;

    private static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        login_btn = (Button) findViewById(R.id.loginBtn);

        dialog = new ProgressDialog(this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
    }

    private void LoginUser(){
        String str_username = username.getText().toString();
        String str_password = password.getText().toString();
        if(str_username.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter your username!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(str_password.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter your password!",Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.setMessage("Logging In, please wait.");
        dialog.show();
        SendRequest sendRequest = new SendRequest();
        sendRequest.execute(Constatnts.LOGIN_USER, str_username, str_password);
    }



    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(Constatnts.LOGIN_USER);
            Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            dialog.dismiss();
            if (result.equals(Constatnts.LOGIN_SUCCESSFUL))
            {
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(LoginActivity.this).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.LOGIN_USER));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(LoginActivity.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}
