package com.example.uonliaquatarain.hangoutapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.uonliaquatarain.hangoutapp.Models.PlaceInfo;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Calendar;

public class SetEventLocation extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker locationMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    private AutoCompleteTextView searchBox;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private PlaceInfo mPlace;
    private String username, my_username, event_name, place = "";
    private LatLng place_latlng;
    private Button next;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_event_location);

        progressDialog = new ProgressDialog(this);
        searchBox = (AutoCompleteTextView) findViewById(R.id.map_searchBox_id);
        next = (Button) findViewById(R.id.map_next_btn);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Intent intent = getIntent();
        username = intent.getExtras().getString("username");
        my_username = intent.getExtras().getString("my_username");
        event_name = intent.getExtras().getString("event_name");



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent1 = new Intent(SetEventLocation.this, SetEventDateAndTime.class);
//
//                intent1.putExtra("username", username);
//                intent1.putExtra("my_username", my_username);
//                intent1.putExtra("event_name", event_name);
//                intent1.putExtra("place", place);
//                intent1.putExtra("latlng", place_latlng.toString());
//                startActivity(intent1);

                dialog = new Dialog(SetEventLocation.this);
                dialog.setContentView(R.layout.dialog_date_time);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                final Button set_date = (Button) dialog.findViewById(R.id.dialog_dateTime_setDate);
                final Button set_time = (Button) dialog.findViewById(R.id.dialog_dateTime_setTime);
                final Button send_request = (Button) dialog.findViewById(R.id.dialog_dateTime_sendRequest);

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
                        String date = set_date.getText().toString();
                        String time = set_time.getText().toString();

                        progressDialog.setTitle("Sending Event Request");
                        progressDialog.setMessage("Please wait while we send event request!");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        SendRequest sendRequest = new SendRequest();
                        sendRequest.execute(Constatnts.SEND_EVENT_REQUEST, my_username, username, event_name, date, time, place, place_latlng.toString());
                    }
                });
            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, Places.getGeoDataClient(this.getApplicationContext()), LAT_LNG_BOUNDS, null);
            searchBox.setAdapter(placeAutocompleteAdapter);
            searchBox.setOnItemClickListener(mAutoCompleteListener);
            Intent intent = getIntent();
            if(intent.hasExtra("latlng")){
                String latlng_str = intent.getExtras().getString("latlng");
                int i = 10;
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

                Double longitude = Double.parseDouble(longitude_str);
                Double latitude = Double.parseDouble(latitude_str);
                LatLng latLng = new LatLng(latitude, longitude);

                moveCamera(latLng,10, "Location");
                next.setVisibility(View.GONE);
            }

        }

    }

    protected synchronized void buildGoogleApiClient(){

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if(locationMarker != null){
            locationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        locationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
        place_latlng = latLng;

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission is granted!
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(googleApiClient == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private AdapterView.OnItemClickListener mAutoCompleteListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            GeoDataClient geoDataClient = Places.getGeoDataClient(SetEventLocation.this, null);
            Task<PlaceBufferResponse> placeResult = geoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);
        }
    };

    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
            if(task.isSuccessful()){
                PlaceBufferResponse places = task.getResult();
                final Place myPlace = places.get(0);
                try{
                    mPlace = new PlaceInfo();
                    mPlace.setName(myPlace.getName().toString());
                    mPlace.setAddress(myPlace.getAddress().toString());
                    mPlace.setAttributes(myPlace.getAttributions().toString());
                    mPlace.setId(myPlace.getId());
                    mPlace.setLatLng(myPlace.getLatLng());
                    mPlace.setRating(myPlace.getRating());
                    mPlace.setPhoneNo(myPlace.getPhoneNumber().toString());
                    mPlace.setUri(myPlace.getWebsiteUri());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                moveCamera(myPlace.getLatLng(),16,mPlace.getName());
                places.release();

            }else{
                Toast.makeText(getApplicationContext(), "Couldn't Load Location!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void moveCamera(LatLng latLng, float zoom, String title){
        place = title;
        place_latlng = latLng;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(!title.equals("My Location")){
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(markerOptions);
        }
        hideSoftKeyboard();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Button set_date = (Button) dialog.findViewById(R.id.dialog_dateTime_setDate);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        set_date.setText(currentDateString);


    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Button set_time = (Button) dialog.findViewById(R.id.dialog_dateTime_setTime);
        set_time.setText(hourOfDay + ":" + minute);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(Constatnts.SEND_EVENT_REQUEST);
            Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            dialog.dismiss();

            Intent intent1 = new Intent(SetEventLocation.this, Events.class);
            startActivity(intent1);
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(SetEventLocation.this).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.SEND_EVENT_REQUEST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(SetEventLocation.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}
