package com.example.uonliaquatarain.hangoutapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.example.uonliaquatarain.hangoutapp.FragmentUsers.users;

public class EventRequests extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventRequestAdapter eventRequestAdapter;
    private List<EventRequestItem> eventRequestItems;
    private String event_name;
    private String latlng_str;
    private String event_message;
    private int image_id;
    private double longitude;
    private  double latitude;
    private LatLng latLng;
    private String event_id;
    private TextView noEventRequestText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_requests);

        noEventRequestText = (TextView) findViewById(R.id.noEventRequestText);

        eventRequestItems = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.event_request_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //eventRequestItems.add(new EventRequestItem("hi", R.drawable.addfriend_icon ));

        eventRequestAdapter = new EventRequestAdapter(this, eventRequestItems);
        recyclerView.setAdapter(eventRequestAdapter);

        Intent intent = getIntent();
        if(intent.hasExtra("event_message")){
            latlng_str = intent.getExtras().getString("latlng");
            event_name = intent.getExtras().getString("event_name");
            event_message = intent.getExtras().getString("event_message");
            event_id = intent.getExtras().getString("event_id");

            AddEvent();
        }
        else{
            List<String> data = Splash.databaseAdapter.getData();
            SendRequest getAlleventRequests = new SendRequest();
            getAlleventRequests.execute(Constatnts.GET_EVENT_REQUESTS, data.get(1));
        }

    }

    public void AddEvent(){

        LatLng latLng = getLatLng(latlng_str, event_name);

        eventRequestItems.add(new EventRequestItem(latLng, event_message, image_id, event_id));
        EventRequestAdapter eventRequestAdapter = new EventRequestAdapter(getApplicationContext(), eventRequestItems);
        recyclerView.setAdapter(eventRequestAdapter);
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
        latLng = new LatLng(latitude, longitude);

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
            noEventRequestText.setVisibility(View.GONE);
            List<String> event_creator= intent.getStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "event_creator");
            List<String> event_name = intent.getStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "event_name");
            List<String> date = intent.getStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "date");
            List<String> time = intent.getStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS+ "time");
            List<String> location = intent.getStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "location");
            List<String> latlng = intent.getStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "latlng");
            List<String> event_id = intent.getStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "event_id");

            //Set event request in recycler view
            for(int i = 0; i < event_creator.size(); i++){
                LatLng latLng = getLatLng(latlng.get(i), event_name.get(i));
                String msg = event_creator.get(i) + " has asked you for " + event_name.get(i) + " on " + date.get(i) + " at " + time.get(i) + " at " + location.get(i);
                eventRequestItems.add(new EventRequestItem(latLng, msg, image_id, event_id.get(i)));
            }
            EventRequestAdapter eventRequestAdapter = new EventRequestAdapter(getApplicationContext(), eventRequestItems);
            recyclerView.setAdapter(eventRequestAdapter);

        }
    };


    private BroadcastReceiver eventRequestAccepted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), intent.getExtras().getString(Constatnts.EVENT_REQUEST_ACCEPTED), Toast.LENGTH_SHORT).show();
            EventRequestAdapter.progressDialog.dismiss();

        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(EventRequests.this).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.GET_EVENT_REQUESTS));
        LocalBroadcastManager.getInstance(EventRequests.this).registerReceiver(eventRequestAccepted, new IntentFilter(Constatnts.EVENT_REQUEST_ACCEPTED));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(EventRequests.this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(EventRequests.this).unregisterReceiver(eventRequestAccepted);
        super.onPause();
    }

}
