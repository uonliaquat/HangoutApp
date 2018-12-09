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

public class RecyclerViewAdapterFragmentUsers extends RecyclerView.Adapter<RecyclerViewAdapterFragmentUsers.ViewHolder> {

    Context context;
    List<User> users;
    Dialog dialog;
    private int item_position;

    public RecyclerViewAdapterFragmentUsers(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_user, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        //Dialog init
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_user);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button profile_btn = (Button) dialog.findViewById(R.id.dialog_viewprofileBtn_id);

        viewHolder.item_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView dialog_name = (TextView) dialog.findViewById(R.id.dialog_name_id);
                TextView dialog_username = (TextView) dialog.findViewById(R.id.dialog_username_id);
                ImageView dialog_photo = (ImageView) dialog.findViewById(R.id.dialog_image_id);
                dialog_name.setText(users.get(viewHolder.getAdapterPosition()).getNAME());
                dialog_username.setText(users.get(viewHolder.getAdapterPosition()).getUSERNAME());
                dialog_photo.setImageDrawable(viewHolder.photo.getDrawable());
                item_position = viewHolder.getAdapterPosition();
                dialog.show();
            }
        });

        viewHolder.add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> data  = Splash.databaseAdapter.getData();

                String usernameOfReceiver = users.get(viewHolder.getAdapterPosition()).getUSERNAME();
                SendRequest sendRequest = new SendRequest();
                sendRequest.execute(Constatnts.SEND_FRIEND_REQUEST, data.get(1), usernameOfReceiver, "", Constatnts.FRIEND_REQUEST);

            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) dialog.findViewById(R.id.dialog_image_id);
                dialog.dismiss();
                Bitmap bm = convertImageViewToBitmap(imageView);
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("name", users.get(item_position).getNAME());
                intent.putExtra("photo", bm);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(viewHolder.name ,"name_transition");
                pairs[1] = new Pair<View, String>(viewHolder.photo ,"profilePic_transition");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                context.startActivity(intent, options.toBundle());
            }
        });


        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.name.setText(users.get(position).getNAME());
        viewHolder.username.setText(users.get(position).getUSERNAME());
        if(!users.get(position).getPHOTO().equals("null")) {
            Glide.with(viewHolder.photo.getContext()).load(users.get(position).getPHOTO()).into(viewHolder.photo);
        }
        else{
            viewHolder.photo.setImageResource(R.drawable.defaultpic_icon);
        }

    }


    @Override
    public int getItemCount() {
        return users.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout item_user;
        private TextView name;
        private TextView username;
        private ImageView  photo;
        private Button add_friend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_user = (LinearLayout) itemView.findViewById(R.id.user_item_id);
            add_friend = (Button) itemView.findViewById(R.id.addfriend_id);
            name = (TextView) itemView.findViewById(R.id.name_user);
            username = (TextView) itemView.findViewById(R.id.userName_user);
            photo = (ImageView) itemView.findViewById(R.id.image_user);
        }
    }


}
