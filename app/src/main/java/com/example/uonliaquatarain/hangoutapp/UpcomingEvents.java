package com.example.uonliaquatarain.hangoutapp;

import android.animation.ArgbEvaluator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class UpcomingEvents extends AppCompatActivity {

    private ViewPager viewPager;
    private PendingEventAdapter eventAdapter;
    private List<UpcomingEventModel> eventModels;
    private Integer[] colors = null;
    private TextView noPendingEventText;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private int image_id;
    private double longitude;
    private  double latitude;
    private LatLng latLng;
    private String event_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);

        getSupportActionBar().hide();
        noPendingEventText = (TextView) findViewById(R.id.noPendingEventText);

        viewPager = findViewById(R.id.event_viewpager);
        eventModels = new ArrayList<>();

        Integer[] colors_temp = {getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4),
                getResources().getColor(R.color.color5),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color7),
                getResources().getColor(R.color.color1)
        };

        colors = colors_temp;


        //Get Pending Events
        List<String> data = Splash.databaseAdapter.getData();
        SendRequest getAllUsers = new SendRequest();
        getAllUsers.execute(Constatnts.GET_PENDING_EVENTS, data.get(1));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int i1) {
                if(position < (eventAdapter.getCount() -1) && position < (colors.length -1)){
                   viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
                }
                else{
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private LatLng getLatLng(String latlng_str, String event_name){
        int i = 10;
        longitude = 0;
        latitude = 0;
        if(latlng_str.charAt(4) == '/'){
            i = 11;
        }
        String latitude_str = "", longitude_str = "";
        while (latlng_str.charAt(i) != ','){
            latitude_str = latitude_str + latlng_str.charAt(i);
            i++;
        }
        i++;
        while (latlng_str.charAt(i) != ')'){
            longitude_str = longitude_str + latlng_str.charAt(i);
            i++;
        }

        longitude = Double.parseDouble(longitude_str);
        latitude = Double.parseDouble(latitude_str);
        LatLng latLng = new LatLng(latitude, longitude);

        image_id = 0;

        if(event_name.equals(Constatnts.FOOD)){
            image_id = R.drawable.food;
        }
        else if(event_name.equals(Constatnts.MOVIE)){
            image_id = R.drawable.movie;
        }
        else if(event_name.equals(Constatnts.MEETING)){
            image_id = R.drawable.meeting;
        }
        else if(event_name.equals(Constatnts.CONCERT)){
            image_id = R.drawable.concert;
        }
        else if(event_name.equals(Constatnts.WORKSHOP)){
            image_id = R.drawable.workshop;
        }
        else if(event_name.equals(Constatnts.FESTIVAL)){
            image_id = R.drawable.festival;
        }
        else if(event_name.equals(Constatnts.TRAVELING)){
            image_id = R.drawable.traveling;
        }
        else if(event_name.equals(Constatnts.HANGOUT)){
            image_id = R.drawable.hangout;
        }
        return latLng;
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            noPendingEventText.setVisibility(View.GONE);
            List<String> event_creator= intent.getStringArrayListExtra(Constatnts.GET_PENDING_EVENTS + "event_creator");
            List<String> event_name = intent.getStringArrayListExtra(Constatnts.GET_PENDING_EVENTS + "event_name");
            List<String> date = intent.getStringArrayListExtra(Constatnts.GET_PENDING_EVENTS + "date");
            List<String> time = intent.getStringArrayListExtra(Constatnts.GET_PENDING_EVENTS+ "time");
            List<String> location = intent.getStringArrayListExtra(Constatnts.GET_PENDING_EVENTS + "location");
            List<String> latlng = intent.getStringArrayListExtra(Constatnts.GET_PENDING_EVENTS + "latlng");
            List<String> event_id = intent.getStringArrayListExtra(Constatnts.GET_PENDING_EVENTS + "event_id");

            //Set event request in recycler view
            for(int i = 0; i < event_creator.size(); i++){
                LatLng latLng  = getLatLng(latlng.get(i), event_name.get(i));
                eventModels.add(new UpcomingEventModel(image_id, event_name.get(i), event_creator.get(i), location.get(i), date.get(i), time.get(i), latLng));
            }
            eventAdapter = new PendingEventAdapter(eventModels, context);
            viewPager.setAdapter(eventAdapter);
            viewPager.setPadding(130,0,130,0);


        }
    };



    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(UpcomingEvents.this).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.GET_PENDING_EVENTS));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(UpcomingEvents.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}
