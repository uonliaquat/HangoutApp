package com.example.uonliaquatarain.hangoutapp;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterFragmentFriends extends RecyclerView.Adapter<RecyclerViewAdapterFragmentFriends.ViewHolderFriend> {

    Context context;
    List<User> friends;
    Dialog dialog;
    private int item_position;
    public static List<User> selectedFriends;
    public boolean canSelect;

    public RecyclerViewAdapterFragmentFriends(Context context, List<User> friends) {
        this.context = context;
        this.friends = friends;
        selectedFriends = new ArrayList<>();
        canSelect = false;
    }


    @NonNull
    @Override
    public ViewHolderFriend onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_friend, viewGroup, false);
        final ViewHolderFriend viewHolder = new ViewHolderFriend(view);


        viewHolder.item_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!canSelect) {
                    Intent intent = new Intent(context, ChatUI.class);
                    intent.putExtra("username", friends.get(viewHolder.getAdapterPosition()).getUSERNAME());
                    context.startActivity(intent);
                }
                else {
                    int color = Color.TRANSPARENT;
                    Drawable background = viewHolder.item_friend.getBackground();
                    if (background instanceof ColorDrawable) {
                        color = ((ColorDrawable) background).getColor();
                    }
                    if(color== Color.GRAY) {
                        viewHolder.item_friend.setBackgroundColor(Color.TRANSPARENT);
                        for(int i = 0; i < selectedFriends.size(); i++){
                            if(selectedFriends.get(i).getUSERNAME().equals(friends.get(viewHolder.getAdapterPosition()).getUSERNAME())){
                                selectedFriends.remove(i);
                                break;
                            }
                        }
                    }
                    else{
                        viewHolder.item_friend.setBackgroundColor(Color.GRAY);
                        selectedFriends.add(new User(friends.get(viewHolder.getAdapterPosition()).getNAME(),
                                friends.get(viewHolder.getAdapterPosition()).getUSERNAME(),
                                friends.get(viewHolder.getAdapterPosition()).getPHOTO()));
                    }
                }

            }
        });




        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderFriend viewHolder, int position) {
        viewHolder.name.setText(friends.get(position).getNAME());
        viewHolder.username.setText(friends.get(position).getUSERNAME());
        if(!friends.get(position).getPHOTO().equals("null")) {
            Glide.with(viewHolder.photo.getContext()).load(friends.get(position).getPHOTO()).into(viewHolder.photo);
        }
        else{
            viewHolder.photo.setImageResource(R.drawable.defaultpic_icon);
        }

    }


    @Override
    public int getItemCount() {
        return friends.size();
    }

    private Bitmap convertImageViewToBitmap(ImageView v){

        Bitmap bm = null;
        try {
            bm=((BitmapDrawable)v.getDrawable()).getBitmap();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return bm;
    }

    public class ViewHolderFriend extends RecyclerView.ViewHolder{

        private LinearLayout item_friend;
        private TextView name;
        private TextView username;
        private ImageView  photo;
        public ViewHolderFriend(@NonNull View itemView) {
            super(itemView);

            item_friend = (LinearLayout) itemView.findViewById(R.id.friend_item_id);
            name = (TextView) itemView.findViewById(R.id.name_friend);
            username = (TextView) itemView.findViewById(R.id.userName_friend);
            photo = (ImageView) itemView.findViewById(R.id.image_friend);
        }
    }


}
