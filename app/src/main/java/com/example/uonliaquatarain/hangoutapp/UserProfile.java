package com.example.uonliaquatarain.hangoutapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

public class UserProfile extends AppCompatActivity {

    private TextView name;
    private String name_str;
    private String photo;
    private ImageView image;
    private Button btn1, btn2;
    private boolean isMyProfile = false;
    private  String sender_username;
    private String sender_name;
    private String sender_pic_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        name = (TextView) findViewById(R.id.name_id);
        image = (ImageView) findViewById(R.id.image_id);
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);





        Intent intent = getIntent();
        name_str = intent.getExtras().getString("name");
        if(intent.hasExtra("sender_name")){
            sender_name = intent.getExtras().getString("sender_name");
            sender_username = intent.getExtras().getString("sender_username");
            sender_pic_url = intent.getExtras().getString("sender_pic_url");
            name.setText(sender_name);
            Glide.with(getApplicationContext()).load(sender_pic_url).into(image);
            btn1.setText("A C C E P T");
            btn2.setText("R E J E C T");

        }
        else {
            if (intent.hasExtra("my_profile")) {
                isMyProfile = intent.getExtras().getBoolean("my_profile");
            }
            if(intent.hasExtra("photo")) {
                Bitmap bitmap = (Bitmap) intent.getParcelableExtra("photo");
                if (bitmap != null) {
                    image.setImageBitmap(bitmap);
                }
            }
            name.setText(name_str);
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn1.getText().equals("A C C E P T")){
                    //Accept Friend Request
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> list = Splash.databaseAdapter.getData();
                            String my_username = list.get(1);
                            try {
                                SendRequest.jsonObject.put("method", Constatnts.FRIEND_REQUEST_ACCEPTED);
                                SendRequest.jsonObject.put("username", my_username);
                                SendRequest.jsonObject.put("username_receiver", sender_username);
                                SendRequest.socket = new Socket(SendRequest.SERVER_IP, SendRequest.SERVER_PORT);
                                SendRequest.writer = new PrintWriter(SendRequest.socket.getOutputStream());
                                SendRequest.writer.write(SendRequest.jsonObject.toString());
                                SendRequest.writer.flush();

                                //Add User into your Friend List
                                Intent in = new Intent();
                                in.setAction(Constatnts.FRIEND_REQUEST_ACCEPTED);
                                in.putExtra("name", sender_name);
                                in.putExtra("username", sender_username);
                                in.putExtra("pic_url", sender_pic_url);
                                LocalBroadcastManager.getInstance(UserProfile.this).sendBroadcast(in);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reject Friend Request
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isMyProfile) {
            getMenuInflater().inflate(R.menu.menu, menu);
        }
        else{
            getSupportActionBar().hide();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.edit_profile_id:
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                intent.putExtra("name", name_str);
                startActivity(intent);
        }
        return true;
    }

}
