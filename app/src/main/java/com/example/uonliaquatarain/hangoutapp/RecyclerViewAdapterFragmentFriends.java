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

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapterFragmentFriends extends RecyclerView.Adapter<RecyclerViewAdapterFragmentFriends.ViewHolderFriend> {

    Context context;
    List<User> friends;
    Dialog dialog;
    private int item_position;

    public RecyclerViewAdapterFragmentFriends(Context context, List<User> friends) {
        this.context = context;
        this.friends = friends;
    }


    @NonNull
    @Override
    public ViewHolderFriend onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_friend, viewGroup, false);
        final ViewHolderFriend viewHolder = new ViewHolderFriend(view);

        //Dialog init
//        dialog = new Dialog(context);
//        dialog.setContentView(R.layout.dialog_user);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        Button profile_btn = (Button) dialog.findViewById(R.id.dialog_viewprofileBtn_id);

        viewHolder.item_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChatUI.class);
                intent.putExtra("username", friends.get(viewHolder.getAdapterPosition()).getUSERNAME());
                context.startActivity(intent);

//                TextView dialog_name = (TextView) dialog.findViewById(R.id.dialog_name_id);
//                TextView dialog_username = (TextView) dialog.findViewById(R.id.dialog_username_id);
//                ImageView dialog_photo = (ImageView) dialog.findViewById(R.id.dialog_image_id);
//                dialog_name.setText(friends.get(viewHolder.getAdapterPosition()).getNAME());
//                dialog_username.setText(friends.get(viewHolder.getAdapterPosition()).getUSERNAME());
//                dialog_photo.setImageDrawable(viewHolder.photo.getDrawable());
//                item_position = viewHolder.getAdapterPosition();
//                dialog.show();
            }
        });

        viewHolder.create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> data  = Splash.databaseAdapter.getData();

                String usernameOfReceiver = friends.get(viewHolder.getAdapterPosition()).getUSERNAME();
                SendRequest sendRequest = new SendRequest();
                sendRequest.execute(Constatnts.SEND_FRIEND_REQUEST, data.get(1), usernameOfReceiver, "", Constatnts.FRIEND_REQUEST);

            }
        });

//        profile_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ImageView imageView = (ImageView) dialog.findViewById(R.id.dialog_image_id);
//                dialog.dismiss();
//                Bitmap bm = convertImageViewToBitmap(imageView);
//                Intent intent = new Intent(context, UserProfile.class);
//                intent.putExtra("name", friends.get(item_position).getNAME());
//                intent.putExtra("photo", bm);
//
//                Pair[] pairs = new Pair[2];
//                pairs[0] = new Pair<View, String>(viewHolder.name ,"name_transition");
//                pairs[1] = new Pair<View, String>(viewHolder.photo ,"profilePic_transition");
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
//
//                context.startActivity(intent, options.toBundle());
//            }
//        });


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
        private Button create_event;
        public ViewHolderFriend(@NonNull View itemView) {
            super(itemView);

            item_friend = (LinearLayout) itemView.findViewById(R.id.friend_item_id);
            create_event = (Button) itemView.findViewById(R.id.event_id);
            name = (TextView) itemView.findViewById(R.id.name_friend);
            username = (TextView) itemView.findViewById(R.id.userName_friend);
            photo = (ImageView) itemView.findViewById(R.id.image_friend);
        }
    }


}
