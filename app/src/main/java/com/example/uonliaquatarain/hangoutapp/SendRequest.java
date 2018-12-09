package com.example.uonliaquatarain.hangoutapp;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.Socket;

public class SendRequest extends AsyncTask<String, Void, Void> {


    public static Socket socket;
    public static PrintWriter writer;
    public static String SERVER_IP = "10.211.55.3"; //10.0.2.2
    public static int SERVER_PORT = 54000;
    public static JSONObject jsonObject;
    @Override
    protected Void doInBackground(String... voids) {
        try {

            jsonObject = new JSONObject();
            String method;
            method = voids[0];
            jsonObject.put("method", method);
            if(method == Constatnts.REGISTER_USER) {
                jsonObject.put("name", voids[1]);
                jsonObject.put("username", voids[2]);
                jsonObject.put("password", voids[3]);
            }
            else if(method == Constatnts.LOGIN_USER){
                jsonObject.put("username", voids[1]);
                jsonObject.put("password", voids[2]);
            }
            else if(method == Constatnts.SEND_FRIEND_REQUEST){
                jsonObject.put("username", voids[1]);
                jsonObject.put("username_receiver", voids[2]);
                jsonObject.put("message",voids[3]);
                jsonObject.put("status", voids[4]);
            }
            else if(method == Constatnts.SAVE_PROFILE_PIC){
                jsonObject.put("username", voids[1]);
                jsonObject.put("profile_pic", voids[2]);
                jsonObject.put("name", voids[3]);
            }
            else if(method == Constatnts.GET_PROFILE_PIC){
                jsonObject.put("username", voids[1]);
            }
            else if(method == Constatnts.GET_ALL_USERS){
                jsonObject.put("username", voids[1]);
            }
            else if(method == Constatnts.MESSAGE){
                jsonObject.put("username", voids[1]);
                jsonObject.put("username_receiver", voids[2]);
                jsonObject.put("message", voids[3]);
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
