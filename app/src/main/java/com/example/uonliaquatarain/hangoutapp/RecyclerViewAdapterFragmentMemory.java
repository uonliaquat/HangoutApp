package com.example.uonliaquatarain.hangoutapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class RecyclerViewAdapterFragmentMemory extends RecyclerView.Adapter<RecyclerViewAdapterFragmentMemory.MemoryViewHolder> {

    Context context;
    List<MemoryItem> memoryItems;

    public RecyclerViewAdapterFragmentMemory(Context context, List<MemoryItem> memoryItems) {
        this.context = context;
        this.memoryItems = memoryItems;
    }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.memory_item, viewGroup, false);
        MemoryViewHolder memoryViewHolder = new MemoryViewHolder(view);

        return memoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder memoryViewHolder, int i) {
        memoryViewHolder.name.setText(memoryItems.get(i).getProfileName());
        memoryViewHolder.username.setText(memoryItems.get(i).getUsername());
        if(!memoryItems.get(i).getProfilePhoto().equals("null")) {
            Glide.with(memoryViewHolder.profile_photo.getContext()).load(memoryItems.get(i).getProfilePhoto()).into(memoryViewHolder.profile_photo);
        }
        if(!memoryItems.get(i).getBackground().equals("null")) {
            Glide.with(memoryViewHolder.memory_image.getContext()).load(memoryItems.get(i).getBackground()).into(memoryViewHolder.memory_image);
        }

    }

    @Override
    public int getItemCount() {
        return memoryItems.size();
    }

    public class MemoryViewHolder extends RecyclerView.ViewHolder{
        ImageView memory_image;
        CircularImageView  profile_photo;
        TextView name, username;
        public MemoryViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_photo = (CircularImageView) itemView.findViewById(R.id.memory_profile_image);
            memory_image = (ImageView) itemView.findViewById(R.id.memory_image);
            name = (TextView) itemView.findViewById(R.id.memory_name);
            username = (TextView) itemView.findViewById(R.id.memory_username);
        }
    }


    public void clear() {
        int size = memoryItems.size();
        memoryItems.clear();
        notifyItemRangeRemoved(0, size);
    }
}
