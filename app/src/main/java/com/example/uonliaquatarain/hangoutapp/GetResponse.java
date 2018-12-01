package com.example.uonliaquatarain.hangoutapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
        thread.start();
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

    private void checkMessage(String msg) throws JSONException {
        Intent in = new Intent();
        if(Constatnts.REGISTER_USER == SendRequest.jsonObject.getString("method")){
            in.setAction(Constatnts.REGISTER_USER);
            in.putExtra(Constatnts.REGISTER_USER, msg_received);
        }
        else if(Constatnts.LOGIN_USER == SendRequest.jsonObject.getString("method")){
            in.setAction(Constatnts.LOGIN_USER);
            in.putExtra(Constatnts.LOGIN_USER, msg_received);
        }
        else if(Constatnts.GET_ALL_USERS == SendRequest.jsonObject.getString("method")){
            int i = 2;
            boolean check = false;
            List<String> name_list = new ArrayList<>();
            List<String> username_list = new ArrayList<>();
            String name = "", username = "";
            while(msg.charAt(i) != '*'){
                if(msg.charAt(i) == ':'){
                    name_list.add(name);
                    name = "";
                    check = true;
                }
                else if(msg.charAt(i) == '/' && msg.charAt(i + 1) == '/'){
                    username_list.add(username);
                    username = "";
                    check = false;
                    i = i + 1;
                }
                else if(check){
                    username = username + String.valueOf(msg.charAt(i));
                }
                else if(msg.charAt(i) != ':' && !check){
                    name = name + String.valueOf(msg.charAt(i));
                }
                i++;
            }
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
    }


}
