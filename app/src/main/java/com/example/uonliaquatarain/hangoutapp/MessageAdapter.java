package com.example.uonliaquatarain.hangoutapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.CustomViewHolder> {

    View view;
    List<ResponseMessage> responseMessagesList;
    public MessageAdapter(List<ResponseMessage> responseMessagesList) {
        this.responseMessagesList = responseMessagesList;
    }

    @NonNull
    @Override
    public MessageAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.CustomViewHolder customViewHolder, int i) {
        customViewHolder.textView.setText(responseMessagesList.get(i).getTextMessage());
    }

    @Override
    public int getItemViewType(int position) {
        if(responseMessagesList.get(position).isMe){
            return R.layout.my_bubble;
        }
        return R.layout.friend_bubble;
    }

    @Override
    public int getItemCount() {
        return responseMessagesList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.my_text_message);
        }
    }
}
