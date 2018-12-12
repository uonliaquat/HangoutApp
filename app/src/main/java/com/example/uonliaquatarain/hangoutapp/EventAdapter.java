package com.example.uonliaquatarain.hangoutapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class EventAdapter extends PagerAdapter {

    private  List<EventModel> eventModels;
    private LayoutInflater layoutInflater;
    protected Context context;

    public EventAdapter(List<EventModel> eventModels, Context context) {
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
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_event, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.event_image_id);
        TextView title = (TextView) view.findViewById(R.id.event_title_id);
        TextView description = (TextView) view.findViewById(R.id.desc);

        imageView.setImageResource(eventModels.get(position).getImage());
        title.setText(eventModels.get(position).getTitle());
        description.setText(eventModels.get(position).getDescription());

        container.addView(view, 0);


        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
