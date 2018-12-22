package com.example.uonliaquatarain.hangoutapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventMainPage extends AppCompatActivity {

    private ImageView background, clover;
    private LinearLayout textSplash, texthome, menu;
    private LinearLayout createEvent_btn, upcomingEvents_btn, notifications_btn, eventRequests_btn;
    Animation fromBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_main_page);

        getSupportActionBar().hide();

        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom_anim);

        createEvent_btn = (LinearLayout) findViewById(R.id.createEvent_btn_id);
        upcomingEvents_btn = (LinearLayout) findViewById(R.id.upcomingEvents_btn_id);
        notifications_btn = (LinearLayout) findViewById(R.id.notifications_btn_id);
        eventRequests_btn = (LinearLayout) findViewById(R.id.eventRequests_btn_id);
        menu = (LinearLayout) findViewById(R.id.eventPage_menu);
        texthome = (LinearLayout) findViewById(R.id.texthome);
        clover = (ImageView) findViewById(R.id.clover);
        background = (ImageView) findViewById(R.id.event_main_layout_background);
        textSplash = (LinearLayout) findViewById(R.id.textsplash);

        background.animate().translationY(-1000).setDuration(800).setStartDelay(300);
        clover.animate().alpha(0).setDuration(800).setStartDelay(600);
        textSplash.animate().translationY(140).alpha(0).setDuration(800).setStartDelay(600);

        texthome.startAnimation(fromBottom);
        menu.startAnimation(fromBottom);


        createEvent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventMainPage.this, Events.class);
                startActivity(intent);
            }
        });

        eventRequests_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventMainPage.this, EventRequests.class);
                startActivity(intent);
            }
        });
    }
}
