package com.example.uonliaquatarain.hangoutapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class EventRequestAdapter extends RecyclerView.Adapter<EventRequestAdapter.EventRequestViewHolder> {

    private Context context;
    private List<EventRequestItem> eventRequestItemList;
    public static ProgressDialog progressDialog;

    public EventRequestAdapter(Context context, List<EventRequestItem> eventRequestItemList) {
        this.context = context;
        this.eventRequestItemList = eventRequestItemList;
    }

    @NonNull
    @Override
    public EventRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.event_request_item, null);
        final EventRequestViewHolder eventRequestViewHolder = new EventRequestViewHolder(view);

        eventRequestViewHolder.check_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = eventRequestItemList.get(eventRequestViewHolder.getAdapterPosition()).getLatLng();
                Intent intent = new Intent(context, SetEventLocation.class);
                intent.putExtra("latlng", latLng.toString());
                context.startActivity(intent);
            }
        });

        eventRequestViewHolder.accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> data = Splash.databaseAdapter.getData();
                SendRequest sendRequest = new SendRequest();
                sendRequest.execute(Constatnts.EVENT_REQUEST_ACCEPTED, data.get(1), eventRequestItemList.get(eventRequestViewHolder.getAdapterPosition()).getEvent_id());
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Accepting Request");
                progressDialog.setMessage("Please wait while we accept event request");
//                progressDialog.show();

            }
        });

        return eventRequestViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventRequestViewHolder eventRequestViewHolder, int i) {
        EventRequestItem eventRequestItem = eventRequestItemList.get(i);
        eventRequestViewHolder.title.setText(eventRequestItem.getTitle());
        eventRequestViewHolder.imageView.setImageDrawable(context.getResources().getDrawable(eventRequestItem.getImage()));
    }

    @Override
    public int getItemCount() {
        return eventRequestItemList.size();
    }

    class EventRequestViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title;
        Button accept_request;
        Button check_location;
        Button reject_request;
        public EventRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_event_request);
            title = (TextView) itemView.findViewById(R.id.textViewTitle_eventRequest);
            accept_request = (Button) itemView.findViewById(R.id.accept_event_request);
            check_location = (Button) itemView.findViewById(R.id.check_location);
            reject_request = (Button) itemView.findViewById(R.id.rej_event_request);


        }
    }
}
