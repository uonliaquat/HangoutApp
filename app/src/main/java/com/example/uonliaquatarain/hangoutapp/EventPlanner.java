package com.example.uonliaquatarain.hangoutapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventPlanner extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button pickDate_btn, pickTime_btn;
    private TextView date_text, time_text, sendEventRequest_btn;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ProgressDialog progressDialog;
    private String event_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_planner);

        progressDialog = new ProgressDialog(this);
        sendEventRequest_btn = (Button) findViewById(R.id.send_event_request_btn);
        pickDate_btn = (Button) findViewById(R.id.pic_your_date_id);
        pickTime_btn = (Button) findViewById(R.id.pic_your_time_id);
        date_text = (TextView) findViewById(R.id.date_picker_text);
        time_text = (TextView) findViewById(R.id.timepicker_text);
        viewPager = (ViewPager) findViewById(R.id.even_planner_viewpager_id);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Intent intent = getIntent();
        event_name = intent.getExtras().getString("event");

        FragmentFriends fragmentFriends = new FragmentFriends();
        Bundle bundle = new Bundle(MainActivity.fragmentFriends.friends.size());
        for(int i = 0; i < MainActivity.fragmentFriends.friends.size(); i++){
            bundle.putString(MainActivity.fragmentFriends.friends.get(i).getNAME(), MainActivity.fragmentFriends.friends.get(i).getNAME());
            bundle.putString(MainActivity.fragmentFriends.friends.get(i).getUSERNAME(), MainActivity.fragmentFriends.friends.get(i).getUSERNAME());
            bundle.putString(MainActivity.fragmentFriends.friends.get(i).getPHOTO(), MainActivity.fragmentFriends.friends.get(i).getPHOTO());
        }
        fragmentFriends.setArguments(bundle);
        viewPagerAdapter.AddFragment(fragmentFriends, "");
        viewPager.setAdapter(viewPagerAdapter);

        pickDate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Date Picker");

            }
        });

        pickTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "Time Picker");
            }
        });

        sendEventRequest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = date_text.getText().toString();
                String time = time_text.getText().toString();
                progressDialog.setTitle("Wait");
                progressDialog.setMessage("Please wait while we arer sending event request!");
                //progressDialog.setCancelable(false);
                List<User> users = RecyclerViewAdapterFragmentFriends.selectedFriends;
                if(users.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please select users first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(date.equals("Date")){
                    Toast.makeText(getApplicationContext(),"Select Date!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(time.equals("Time")){
                    Toast.makeText(getApplicationContext(),"Select Time!", Toast.LENGTH_SHORT).show();
                    return;

                }
                else{
                    progressDialog.show();
                    String name = "", username = "", photo = "";
                    for(int i = 0; i < users.size(); i++){
                        username = users.get(i).getUSERNAME();
                        username = username + "...";
                    }
                    List<String> data = Splash.databaseAdapter.getData();
                    SendRequest sendRequest = new SendRequest();
                    sendRequest.execute(Constatnts.EVENT_REQUEST, data.get(1), username, event_name, date, time);
                }
                RecyclerViewAdapterFragmentFriends.selectedFriends.clear();
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
            String result = intent.getStringExtra(Constatnts.EVENT_REQUEST);
            Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(EventPlanner.this).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.EVENT_REQUEST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(EventPlanner.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}
