package com.example.uonliaquatarain.hangoutapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PendingEventAdapter extends PagerAdapter {

    private  List<UpcomingEventModel> eventModels;
    private LayoutInflater layoutInflater;
    protected Context context;

    public PendingEventAdapter(List<UpcomingEventModel> eventModels, Context context) {
        this.eventModels = eventModels;
        this.context = context;
    }

    @Override
    public int getCount() {
        return eventModels.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_upcoming_event, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.event_image_id);
        TextView title = (TextView) view.findViewById(R.id.event_title_id);
        TextView creator = (TextView) view.findViewById(R.id.pending_event_creator_name);
        TextView place = (TextView) view.findViewById(R.id.pending_event_location);
        TextView date = (TextView) view.findViewById(R.id.pending_event_date);
        TextView time = (TextView) view.findViewById(R.id.pending_event_time);
        Button check_location = (Button) view.findViewById(R.id.upcoming_event_check_location);


        imageView.setImageResource(eventModels.get(position).getImage());
        title.setText(eventModels.get(position).getTitle());
        creator.setText(eventModels.get(position).getCreator());
        place.setText(eventModels.get(position).getPlace());
        date.setText(eventModels.get(position).getDate());
        time.setText(eventModels.get(position).getTime());

        check_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SetEventLocation.class);
                intent.putExtra("latlng", eventModels.get(position).getLatLng().toString());
                context.startActivity(intent);
            }
        });



        container.addView(view, 0);


        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
