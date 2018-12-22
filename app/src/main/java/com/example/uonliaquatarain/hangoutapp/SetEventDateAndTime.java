package com.example.uonliaquatarain.hangoutapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.Calendar;

public class SetEventDateAndTime extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private Button set_date, set_time, send_request;
    private TextView date_text, time_text;
    private ProgressDialog progressDialog;
    private String username, my_username, event_name, place = "";
    private String place_latlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_event_date_and_time);

        progressDialog = new ProgressDialog(this);
        date_text = (TextView) findViewById(R.id.date_picker_text);
        time_text = (TextView) findViewById(R.id.timepicker_text);
        set_date = (Button) findViewById(R.id.pic_your_date_id);
        set_time = (Button) findViewById(R.id.pic_your_time_id);
        send_request = (Button) findViewById(R.id.send_event_request_btn);

        Intent intent = getIntent();
        username = intent.getExtras().getString("username");
        my_username = intent.getExtras().getString("my_username");
        event_name = intent.getExtras().getString("event_name");
        place = intent.getExtras().getString("place");
        place_latlng = intent.getExtras().getString("latlng");

        set_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker");
            }
        });

        set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "Time Picker");
            }
        });

        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Sending Event Request");
                progressDialog.setTitle("Please wait while we send event request!");
                progressDialog.setCancelable(false);

                String date = date_text.getText().toString();
                String time = time_text.getText().toString();
                if(date.equals("Date")){
                    Toast.makeText(getApplicationContext(),"Select Date!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(time.equals("Time")){
                    Toast.makeText(getApplicationContext(),"Select Time!", Toast.LENGTH_SHORT).show();
                    return;

                }
                progressDialog.show();
                    SendRequest sendRequest = new SendRequest();
                    sendRequest.execute(Constatnts.SEND_EVENT_REQUEST, my_username, username, event_name, date, time, place, place_latlng);

            }
        });
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        date_text.setText(currentDateString);


    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time_text.setText(hourOfDay + ":" + minute);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(Constatnts.SEND_EVENT_REQUEST);
            Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(SetEventDateAndTime.this).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.SEND_EVENT_REQUEST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(SetEventDateAndTime.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}
