package com.example.uonliaquatarain.hangoutapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ActionBar actionBar;
    private  NavigationView navigationView;
    public static FragmentFriends fragmentFriends;
    public static FragmentMemories fragmentMemories;
    private ImageView userPic;
    private TextView name, username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        actionBar = getSupportActionBar();

        actionBar.setElevation(0);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ADD FRAGMENTS
        fragmentFriends = new FragmentFriends();
        fragmentMemories = new FragmentMemories(getApplicationContext());
        viewPagerAdapter.AddFragment(new FragmentUsers(), "");
        viewPagerAdapter.AddFragment(fragmentFriends, "");
        viewPagerAdapter.AddFragment(fragmentMemories, "");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.users_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.friends_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.memories_icon);


        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        userPic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_pic);
        name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_userName);
        username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_username);


        Intent intent = getIntent();
        String activity_name = "";
        if(intent.hasExtra("activity_name")){
            activity_name = intent.getExtras().getString("activity_name");
        }
        if(activity_name.equals("login_activity") || activity_name.equals("register_activity")) {
            //Get All Users
            List<String> data = Splash.databaseAdapter.getData();
            SendRequest getAllUsers = new SendRequest();
            getAllUsers.execute(Constatnts.GET_ALL_USERS, data.get(1));
            name.setText(data.get(0));
            username.setText(data.get(1));
        }




    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(Constatnts.SEND_FRIEND_REQUEST);
            Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
        }
    };

    private BroadcastReceiver broadcastReceiver_profile_pic = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(Constatnts.GET_PROFILE_PIC);
            Glide.with(getApplicationContext()).load(result).into(userPic);
        }
    };




    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.SEND_FRIEND_REQUEST));
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiver_profile_pic, new IntentFilter(Constatnts.GET_PROFILE_PIC));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(broadcastReceiver_profile_pic);
        super.onPause();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.view_your_profile_id:
                List<String> list = Splash.databaseAdapter.getData();
                String name = list.get(0);
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.putExtra("name", name);
                intent.putExtra("my_profile", true);
                startActivity(intent);
                break;

            case R.id.crreateEvent_id:
                Intent intent1 = new Intent(MainActivity.this, EventMainPage.class);
                startActivity(intent1);
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
