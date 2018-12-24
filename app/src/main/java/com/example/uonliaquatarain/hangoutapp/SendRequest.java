package com.example.uonliaquatarain.hangoutapp;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.Socket;

public class SendRequest extends AsyncTask<String, Void, Void> {



    public static PrintWriter writer;
    public static String SERVER_IP = "10.211.55.3"; //10.0.2.2
    public static int SERVER_PORT = 54000 ;
    public static JSONObject jsonObject;
    public static Socket socket = null;

    @Override
    protected Void doInBackground(String... voids) {
        try {
            jsonObject = new JSONObject();
            String method;
            method = voids[0];
            jsonObject.put("method", method);
            if(method.equals(Constatnts.REGISTER_USER)) {
                jsonObject.put("name", voids[1]);
                jsonObject.put("username", voids[2]);
                jsonObject.put("password", voids[3]);
            }
            else if(method.equals(Constatnts.LOGIN_USER)){
                jsonObject.put("username", voids[1]);
                jsonObject.put("password", voids[2]);
            }
            else if(method.equals(Constatnts.SEND_FRIEND_REQUEST)){
                jsonObject.put("username", voids[1]);
                jsonObject.put("username_receiver", voids[2]);
                jsonObject.put("message",voids[3]);
                jsonObject.put("status", voids[4]);
            }
            else if(method.equals(Constatnts.SAVE_PROFILE_PIC)){
                jsonObject.put("username", voids[1]);
                jsonObject.put("profile_pic", voids[2]);
                jsonObject.put("name", voids[3]);
            }
            else if(method.equals(Constatnts.GET_PROFILE_PIC)){
                jsonObject.put("username", voids[1]);
            }
            else if(method.equals(Constatnts.GET_ALL_USERS)){
                jsonObject.put("username", voids[1]);
            }
            else if(method.equals(Constatnts.MESSAGE)){
                jsonObject.put("username", voids[1]);
                jsonObject.put("username_receiver", voids[2]);
                jsonObject.put("message", voids[3]);
            }
            else if(method.equals(Constatnts.SEND_EVENT_REQUEST)){
                jsonObject.put("username", voids[1]);
                jsonObject.put("username_receiver", voids[2]);
                jsonObject.put("message", voids[3]);
                jsonObject.put("date", voids[4]);
                jsonObject.put("time", voids[5]);
                jsonObject.put("place", voids[6]);
                jsonObject.put("latlng", voids[7]);
            }
            else if(method.equals(Constatnts.GET_FRIENDS)){
                jsonObject.put("username", voids[1]);
            }
            else if(method.equals(Constatnts.GET_EVENT_REQUESTS)){
                jsonObject.put("username", voids[1]);
            }
            else if(method.equals(Constatnts.EVENT_REQUEST_ACCEPTED)){
                jsonObject.put("username", voids[1]);
                jsonObject.put("event_id", voids[2]);
            }
            else if(method.equals(Constatnts.GET_PENDING_EVENTS)){
                jsonObject.put("username", voids[1]);
            }
            else if(method.equals(Constatnts.SAVE_MEMORY)){
                jsonObject.put("username", voids[1]);
                jsonObject.put("profile_pic", voids[2]);
            }
            else if(method.equals(Constatnts.GET_MEMORY)){
                jsonObject.put("username", voids[1]);
            }

            socket = new Socket(SERVER_IP, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream());

            writer.write(jsonObject.toString());
            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return   null;
    }
}
