package com.example.uonliaquatarain.hangoutapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

public class GetResponse extends Service {

    InputStreamReader inputStreamReader;
    BufferedReader bufferedReader;
    Handler handler = new Handler(Looper.getMainLooper());
    char message[] = new char[4096];
    public static String msg_received;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        if (SendRequest.socket != null) {
                            for(int i = 0; i < 4096; i++) {
                                message[i] = 0;
                            }
                            inputStreamReader = new InputStreamReader(SendRequest.socket.getInputStream());
                            bufferedReader = new BufferedReader(inputStreamReader);
                            if(bufferedReader.ready()) {
                                bufferedReader.read(message);
                                msg_received = parseString(message);
                                checkMessage(msg_received);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        //Ping thread
        Thread ping = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(SendRequest.socket != null){
                        try {
                            Thread.sleep(10000);
                            List<String> list = Splash.databaseAdapter.getData();
                            String my_username = list.get(1);
                            SendRequest.jsonObject.put("method", Constatnts.PING);
                            SendRequest.jsonObject.put("username", my_username);
                            SendRequest.socket = new Socket(SendRequest.SERVER_IP, SendRequest.SERVER_PORT);
                            SendRequest.writer = new PrintWriter(SendRequest.socket.getOutputStream());
                            SendRequest.writer.write(SendRequest.jsonObject.toString());
                            SendRequest.writer.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        thread.setPriority(10);
        thread.start();
        ping.start();
        return START_STICKY;
    }


    private String parseString(char[] str){
        String message = "";
        boolean check = false;
        for(int i = 0; i < str.length; i++){
            if(str[i + 1] == 0){
                return message;
            }
            if(str[i] == ' ' || check == true){
                check = true;
                message = message + str[i + 1];
            }
        }
        return message;
    }

    private List<String> getMessage(String str){

        List<String> list = new ArrayList<>();
        String msg = "";
        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            if(chars[i] == '/' && chars[i + 1] == '/' && chars[i + 2] == '/'){
                list.add(msg);
                msg = "";
                i = i + 2;
            }
            else {
                msg = msg + chars[i];
            }
        }
        return list;
    }

    private void checkMessage(String msg) throws JSONException {
        Intent in = new Intent();
        if(Constatnts.PING == SendRequest.jsonObject.getString("method")){
            List<String> sender_information = getMessage(msg_received);
            in.setAction(Constatnts.PING);
            in.putExtra(Constatnts.PING, msg_received);
            if(sender_information.get(3).equals(Constatnts.FRIEND_REQUEST)){
                CustomNotification customNotification = new CustomNotification(getApplicationContext());
                customNotification.notifyThis(sender_information, Constatnts.FRIEND_REQUEST);
            }
            else if(sender_information.get(3).equals(Constatnts.FRIEND_REQUEST_ACCEPTED)){
                CustomNotification customNotification = new CustomNotification(getApplicationContext());
                customNotification.notifyThis(sender_information, Constatnts.FRIEND_REQUEST_ACCEPTED);

                //Add user in your Friend List

                //Add User into your Friend List
//                in.setAction(Constatnts.FRIEND_REQUEST_ACCEPTED);
//                in.putExtra("name", sender_information.get(0));
//                in.putExtra("username", sender_information.get(1));
//                in.putExtra("pic_url", sender_information.get(2));
            }
        }

        else if(Constatnts.REGISTER_USER == SendRequest.jsonObject.getString("method")){
            in.setAction(Constatnts.REGISTER_USER);
            in.putExtra(Constatnts.REGISTER_USER, msg_received);
        }
        else if(Constatnts.LOGIN_USER == SendRequest.jsonObject.getString("method")){
            in.setAction(Constatnts.LOGIN_USER);
            in.putExtra(Constatnts.LOGIN_USER, msg_received);
        }
        else if(Constatnts.GET_ALL_USERS == SendRequest.jsonObject.getString("method")){
            int i = 3;
            int check = 0;
            List<String> name_list = new ArrayList<>();
            List<String> username_list = new ArrayList<>();
            List<String> pictureUrl_list = new ArrayList<>();
            String name = "", username = "", picture = "";
            while(msg.charAt(i) != '*'){
                if(msg.charAt(i) == ':' && msg.charAt(i + 1) == ':'){
                    if(check == 0) {
                        name_list.add(name);
                        name = "";
                        check = 1;
                    }
                    else if(check == 1){
                        username_list.add(username);
                        username = "";
                        check = 2;
                    }
                    i++;
                }
                else if(msg.charAt(i) == '/' && msg.charAt(i + 1) == '/' && msg.charAt(i + 2) == '/'){
                    pictureUrl_list.add(picture);
                    picture = "";
                    check = 0;
                    i = i + 2;
                }
                else if(check == 1){
                    username = username + String.valueOf(msg.charAt(i));
                }
                else if(check == 2){
                    picture = picture + String.valueOf(msg.charAt(i));
                }
                else if(msg.charAt(i) != ':' && check == 0){
                    name = name + String.valueOf(msg.charAt(i));
                }
                i++;
            }
            pictureUrl_list.add(picture);
            in.setAction(Constatnts.GET_ALL_USERS);
            in.putStringArrayListExtra(Constatnts.GET_ALL_USERS + "names", (ArrayList<String>) name_list);
            in.putStringArrayListExtra(Constatnts.GET_ALL_USERS + "usernames", (ArrayList<String>) username_list);
            in.putStringArrayListExtra(Constatnts.GET_ALL_USERS + "pictures", (ArrayList<String>) pictureUrl_list);

        }
        else if(Constatnts.SEND_FRIEND_REQUEST == SendRequest.jsonObject.getString("method")){
            in.setAction(Constatnts.SEND_FRIEND_REQUEST);
            in.putExtra(Constatnts.SEND_FRIEND_REQUEST, msg_received);

        }
        else if(Constatnts.SAVE_PROFILE_PIC == SendRequest.jsonObject.getString("method")){
            in.setAction(Constatnts.SAVE_PROFILE_PIC);
            in.putExtra(Constatnts.SAVE_PROFILE_PIC, msg_received);

        }
        else if(Constatnts.GET_PROFILE_PIC == SendRequest.jsonObject.getString("method")){
            in.setAction(Constatnts.GET_PROFILE_PIC);
            in.putExtra(Constatnts.GET_PROFILE_PIC, msg_received);

        }

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
    }


}
