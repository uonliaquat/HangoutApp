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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class SetEventFriends extends AppCompatActivity {

    private Button pickDate_btn, pickTime_btn, next;
    private TextView date_text, time_text;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ProgressDialog progressDialog;
    private String event_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_event_friends);

        progressDialog = new ProgressDialog(this);
        next = (Button) findViewById(R.id.set_event_friends_btn);
        pickDate_btn = (Button) findViewById(R.id.pic_your_date_id);
        pickTime_btn = (Button) findViewById(R.id.pic_your_time_id);
        date_text = (TextView) findViewById(R.id.date_picker_text);
        time_text = (TextView) findViewById(R.id.timepicker_text);
        viewPager = (ViewPager) findViewById(R.id.even_planner_viewpager_id);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        final Intent intent = getIntent();
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



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<User> users = RecyclerViewAdapterFragmentFriends.selectedFriends;
                if(users.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please select users first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = "", username = "", photo = "";
                for(int i = 0; i < users.size(); i++){
                    username = users.get(i).getUSERNAME();
                    username = username + "...";
                }

                List<String> data = Splash.databaseAdapter.getData();

                Intent intent1 = new Intent(SetEventFriends.this, SetEventLocation.class);
                intent1.putExtra("my_username", data.get(1));
                intent1.putExtra("username", username);
                intent1.putExtra("event_name", event_name);
                startActivity(intent1);
            }
        });


    }

}
