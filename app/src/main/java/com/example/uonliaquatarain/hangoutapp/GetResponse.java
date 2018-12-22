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
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetResponse extends Service {

    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String message;
    public static String msg_received;
    private ServerSocket serverSocket;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        if (SendRequest.socket != null) {
                            inputStreamReader = new InputStreamReader(SendRequest.socket.getInputStream());
                            bufferedReader = new BufferedReader(inputStreamReader);

                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        if (SendRequest.socket != null) {
                            inputStreamReader = new InputStreamReader(SendRequest.socket.getInputStream());
                            bufferedReader = new BufferedReader(inputStreamReader);
                            message = bufferedReader.readLine();
                            if(message != null) {
                                msg_received = parseString(message);
                                checkMessage(msg_received);
                            }
                            message = "";
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
                            Thread.sleep(5000);
                            List<String> list = Splash.databaseAdapter.getData();
                            String my_username = list.get(1);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("method", Constatnts.PING);
                            jsonObject.put("username", my_username);
                            SendRequest.socket = new Socket(SendRequest.SERVER_IP, SendRequest.SERVER_PORT);
                            SendRequest.writer = new PrintWriter(SendRequest.socket.getOutputStream());
                            SendRequest.writer.write(jsonObject.toString());
                            SendRequest.writer.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        thread.setPriority(10);
        ping.setPriority(10);
        thread.start();
        ping.start();
        return START_STICKY;
    }


    private String parseString(String str){
        String message = "";
        if(str.charAt(0) == 'P' && str.charAt(1) == 'I' && str.charAt(2) == 'N' && str.charAt(3) == 'G'){
            return str;
        }
        boolean check = false;
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == ' ' && !check){
                check = true;
            }
            else if(check){
                message = message + str.charAt(i);
            }
        }
        return message;
    }

    private List<String> getMessage(String str){

        List<String> list = new ArrayList<>();
        String msg = "";
        char[] chars = str.toCharArray();
        int check = 0;
        if(chars[0] == 'P' && chars[1] == 'I' && chars[2] == 'N' && chars[3] == 'G'){
            list.add("PING");
            check = 5;
        }
        for(int i = check; i < chars.length; i++) {
            if(chars[i] == '/' && chars[i + 1] == '/' && chars[i + 2] == '/'){
                list.add(msg);
                msg = "";
                i = i + 2;
            }
            else {
                msg = msg + chars[i];
            }
        }
        list.add(msg);
        return list;
    }

    private void checkMessage(String msg) throws JSONException {
        Intent in = new Intent();
        List<String> sender_information = getMessage(msg_received);
        if (Constatnts.PING == sender_information.get(0)) {
            in.setAction(Constatnts.PING);
            in.putExtra(Constatnts.PING, msg_received);
            if (sender_information.get(4).equals(Constatnts.FRIEND_REQUEST)) {
                CustomNotification customNotification = new CustomNotification(getApplicationContext());
                customNotification.notifyThis(sender_information, Constatnts.FRIEND_REQUEST);
            } else if (sender_information.get(4).equals(Constatnts.FRIEND_REQUEST_ACCEPTED)) {
                CustomNotification customNotification = new CustomNotification(getApplicationContext());
                customNotification.notifyThis(sender_information, Constatnts.FRIEND_REQUEST_ACCEPTED);


                List<String> sender_name = new ArrayList<String>(Arrays.asList(sender_information.get(1).split(",")));
                List<String> sender_username = new ArrayList<String>(Arrays.asList(sender_information.get(2).split(",")));
                List<String> sender_picture_url = new ArrayList<String>(Arrays.asList(sender_information.get(3).split(",")));
                //Add User into your Friend List
                in.setAction(Constatnts.FRIEND_REQUEST_ACCEPTED);
                in.putStringArrayListExtra(Constatnts.GET_ALL_USERS + "names", (ArrayList<String>) sender_name);
                in.putStringArrayListExtra(Constatnts.GET_ALL_USERS + "usernames", (ArrayList<String>) sender_username);
                in.putStringArrayListExtra(Constatnts.GET_ALL_USERS + "pictures", (ArrayList<String>) sender_picture_url);


            } else if ((sender_information.get(4).equals(Constatnts.MESSAGE))) {
                CustomNotification customNotification = new CustomNotification(getApplicationContext());
                customNotification.notifyThis(sender_information, Constatnts.MESSAGE);
                String _msg = sender_information.get(5);
                FileHandling.SaveMessage(getApplicationContext(), sender_information.get(2), _msg, false);
                ResponseMessage responseMessage = new ResponseMessage(_msg, false);
                ChatUI.responseMessageList.add(responseMessage);
                ChatUI.messageAdapter.notifyDataSetChanged();

            }
            else if ((sender_information.get(4).equals(Constatnts.SEND_EVENT_REQUEST))) {
                CustomNotification customNotification = new CustomNotification(getApplicationContext());
                customNotification.notifyThis(sender_information, Constatnts.EVENT_REQUEST_RECEIVED);

                //Add event in event Request List
                String message = sender_information.get(1) + " is asking you for " + sender_information.get(5) + " on " +
                        sender_information.get(6) + " at " + sender_information.get(7) + " at " + sender_information.get(8) ;
//                in.setAction(Constatnts.EVENT_REQUEST_RECEIVED);
//                in.putExtra("message", message);
//                in.putExtra("latlng", sender_information.get(9));
//                in.putExtra("event_name", sender_information.get(5));


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

            //Get All Friends
            List<String> data = Splash.databaseAdapter.getData();
            SendRequest getFriends = new SendRequest();
            getFriends.execute(Constatnts.GET_FRIENDS, data.get(1));

        }
        else if(Constatnts.GET_FRIENDS == SendRequest.jsonObject.getString("method")){
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
            in.setAction(Constatnts.GET_FRIENDS);
            in.putStringArrayListExtra(Constatnts.GET_FRIENDS + "names", (ArrayList<String>) name_list);
            in.putStringArrayListExtra(Constatnts.GET_FRIENDS + "usernames", (ArrayList<String>) username_list);
            in.putStringArrayListExtra(Constatnts.GET_FRIENDS + "pictures", (ArrayList<String>) pictureUrl_list);

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
        else if(Constatnts.SEND_EVENT_REQUEST == SendRequest.jsonObject.getString("method")){
            in.setAction(Constatnts.SEND_EVENT_REQUEST);
            in.putExtra(Constatnts.SEND_EVENT_REQUEST, msg_received);

        }
        else if(Constatnts.GET_EVENT_REQUESTS == SendRequest.jsonObject.getString("method")){
            int i = 3;
            int check = 0;
            List<String> event_creator = new ArrayList<>();
            List<String> event_name = new ArrayList<>();
            List<String> date = new ArrayList<>();
            List<String> time = new ArrayList<>();
            List<String> location = new ArrayList<>();
            List<String> latlng = new ArrayList<>();
            String eventCreator = "", eventName = "", Date = "", Time = "", Location = "", Latlng = "";
            while(msg.charAt(i) != '*'){
                if(msg.charAt(i) == ':' && msg.charAt(i + 1) == ':'){
                    if(check == 0) {
                        event_creator.add(eventCreator);
                        eventCreator = "";
                    }
                    else if(check == 1){
                        event_name.add(eventName);
                        eventName = "";
                    }
                    else if(check == 2){
                        date.add(Date);
                        Date = "";
                    }
                    else if(check == 3){
                        time.add(Time);
                        Time = "";
                    }
                    else if(check == 4){
                        location.add(Location);
                        Location = "";
                    }
                    else if(check == 5){
                        latlng.add(Latlng);
                        Latlng = "";
                    }
                    check++;
                    i = i+2;
                }
                if(msg.charAt(i) == '/' && msg.charAt(i + 1) == '/' && msg.charAt(i + 2) == '/'){
                    Latlng = "";
                    check = 0;
                    i = i + 3;
                }
                if(check == 0){
                    eventCreator = eventCreator + String.valueOf(msg.charAt(i));
                }
                else if(check == 1){
                    eventName = eventName + String.valueOf(msg.charAt(i));
                }
                else if(check == 2){
                    Date = Date + String.valueOf(msg.charAt(i));
                }
                else if(check == 3){
                    Time = Time + String.valueOf(msg.charAt(i));
                }
                else if(check == 4){
                    Location = Location + String.valueOf(msg.charAt(i));
                }
                else if(check == 5){
                    Latlng = Latlng + String.valueOf(msg.charAt(i));
                }
                i++;
            }

            in.setAction(Constatnts.GET_EVENT_REQUESTS);
            in.putStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "event_creator", (ArrayList<String>) event_creator);
            in.putStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "event_name", (ArrayList<String>) event_name);
            in.putStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "date", (ArrayList<String>)date);
            in.putStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "time", (ArrayList<String>) time);
            in.putStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "location", (ArrayList<String>) location);
            in.putStringArrayListExtra(Constatnts.GET_EVENT_REQUESTS + "latlng", (ArrayList<String>) latlng);

        }

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
    }


}
