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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, username, password;
    private Button register_btn;
    private static ProgressDialog dialog;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.register_name);
        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        register_btn = (Button) findViewById(R.id.register_btn);

        dialog = new ProgressDialog(this);

        serviceIntent = new Intent(getApplicationContext(), GetResponse.class);
        startService(serviceIntent);


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUser();
            }
        });



    }

    private void RegisterUser() {
        String str_name = name.getText().toString();
        String str_username = username.getText().toString();
        String str_password = password.getText().toString();
        if (str_name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter your name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (str_username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter your username!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (str_password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter your password!", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.setMessage("Registering User, please wait.");
        dialog.show();
        SendRequest sendRequest = new SendRequest();
        sendRequest.execute(Constatnts.REGISTER_USER, str_name, str_username, str_password);

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(Constatnts.REGISTER_USER);
            Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            dialog.dismiss();
            if (result.equals(Constatnts.REGISTRATION_SUCCESSFUL))
            {
                Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent1);
            }
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(RegisterActivity.this).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.REGISTER_USER));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(RegisterActivity.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}

