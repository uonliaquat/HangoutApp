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
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, username, password;
    private CardView register_btn;
    private static ProgressDialog dialog;
    private String str_name;
    private String str_username;
    private String str_password;
    private TextView alreadHaveAnAccount_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.register_name);
        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        register_btn = (CardView) findViewById(R.id.register_btn);
        alreadHaveAnAccount_btn = (TextView) findViewById(R.id.alreadyHaveAnAccount);

        dialog = new ProgressDialog(this);



        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUser();
            }
        });


        alreadHaveAnAccount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });



    }

    private void RegisterUser() {
        str_name = name.getText().toString();
        str_username = username.getText().toString();
        str_password = password.getText().toString();
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
                //Add data in local database
                Splash.databaseAdapter.set_isActive(str_username, 0);
                Splash.databaseAdapter.insertData(str_name, str_username, 1);

                Intent intent1 = new Intent(RegisterActivity.this, MainActivity.class);
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

