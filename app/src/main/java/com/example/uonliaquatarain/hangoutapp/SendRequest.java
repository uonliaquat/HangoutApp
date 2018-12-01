package com.example.uonliaquatarain.hangoutapp;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.Socket;

public class SendRequest extends AsyncTask<String, Void, Void> {


    public static Socket socket;
    private PrintWriter writer;
    public static String SERVER_IP = "10.211.55.3"; //10.0.2.2
    public static int SERVER_PORT = 54000;
    public static JSONObject jsonObject;
    @Override
    protected Void doInBackground(String... voids) {
        try {

            jsonObject = new JSONObject();
            String method, name, username, password;
            method = voids[0];
            jsonObject.put("method", method);
            if(method == Constatnts.REGISTER_USER) {
                name = voids[1];
                username = voids[2];
                password = voids[3];

                jsonObject.put("name", name);
                jsonObject.put("username", username);
                jsonObject.put("password", password);
            }
            else if(method == Constatnts.LOGIN_USER){
                username = voids[1];
                password = voids[2];
                jsonObject.put("username", username);
                jsonObject.put("password", password);
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
