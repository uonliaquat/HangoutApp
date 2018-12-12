package com.example.uonliaquatarain.hangoutapp;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Events extends AppCompatActivity {

    private ViewPager viewPager;
    private EventAdapter eventAdapter;
    private List<EventModel> eventModels;
    private Integer[] colors = null;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private Button createEvent_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        getSupportActionBar().hide();

        createEvent_btn = (Button) findViewById(R.id.createevent_btn);

        eventModels = new ArrayList<>();
        eventModels.add(new EventModel(R.drawable.food, "Food", "Enjoy Food With your friends and family!"));
        eventModels.add(new EventModel(R.drawable.movie, "Movie", "Enjoy Movie With your friends and family"));
        eventModels.add(new EventModel(R.drawable.meeting, "Meeting", "Plan a meeting!"));
        eventModels.add(new EventModel(R.drawable.concert, "Concert", "Enjoy live concert with your friends and family"));
        eventModels.add(new EventModel(R.drawable.workshop, "Workshop", "Gather your friends for a workshop"));
        eventModels.add(new EventModel(R.drawable.festival, "Festival", "Go to a festival"));
        eventModels.add(new EventModel(R.drawable.traveling, "Traveling", "Plan a trip"));
        eventModels.add(new EventModel(R.drawable.hangout, "Hangout", "Hangout with you friends"));

        eventAdapter = new EventAdapter(eventModels, this);
        viewPager = findViewById(R.id.event_viewpager);
        viewPager.setAdapter(eventAdapter);
        viewPager.setPadding(130,0,130,0);

        Integer[] colors_temp = {getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4),
                getResources().getColor(R.color.color5),
                getResources().getColor(R.color.color6),
                getResources().getColor(R.color.color7),
                getResources().getColor(R.color.color8)
        };

        colors = colors_temp;

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

        createEvent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int id = viewPager.getCurrentItem();
               String event = "";
               switch (id){
                   case 0:
                       event = "Food";
                       break;
                   case 1:
                       event = "Movie";
                       break;
                   case 2:
                       event = "Meeting";
                       break;
                   case 3:
                       event = "Concert";
                       break;
                   case 4:
                       event = "Workshop";
                       break;
                   case 5:
                       event = "Festival";
                       break;
                   case 6:
                       event = "Traveling";
                       break;
                   case 7:
                       event = "Hangout";
                       break;

               }
                Intent intent = new Intent(Events.this, EventPlanner.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });

    }
}
