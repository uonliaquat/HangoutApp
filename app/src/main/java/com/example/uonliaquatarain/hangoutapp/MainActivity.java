package com.example.uonliaquatarain.hangoutapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        actionBar = getSupportActionBar();

        actionBar.setElevation(0);

        //ADD FRAGMENTS
        viewPagerAdapter.AddFragment(new FragmentUsers(), "");
        viewPagerAdapter.AddFragment(new FragmentFriends(), "");
        viewPagerAdapter.AddFragment(new FragmentMemories(), "");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.users_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.friends_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.memories_icon);

        //Get All Users
        List<String> data = Splash.databaseAdapter.getData();
        SendRequest sendRequest = new SendRequest();
        sendRequest.execute(Constatnts.GET_ALL_USERS, data.get(1));




    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(Constatnts.SEND_FRIEND_REQUEST);
            Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.SEND_FRIEND_REQUEST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.view_your_profile_id:
                List<String> list = Splash.databaseAdapter.getData();
                String name = list.get(0);
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.putExtra("name", name);
                intent.putExtra("my_profile", true);
                startActivity(intent);
        }
        return true;
    }
}
