package com.example.uonliaquatarain.hangoutapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {

    public static DatabaseAdapter databaseAdapter;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        databaseAdapter = new DatabaseAdapter(this);

        Intent intent = new Intent(Splash.this, RegisterActivity.class);
        startActivity(intent);
        serviceIntent = new Intent(getApplicationContext(), GetResponse.class);
        startService(serviceIntent);

    }
}
