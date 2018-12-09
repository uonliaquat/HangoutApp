package com.example.uonliaquatarain.hangoutapp;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileHandling {

    public static void SaveMessage(Context context, String username, String message, boolean isMe){
        try {
            String msg = "";
            if(isMe){
                msg = "me:" + message;
            }
            else{
                msg = "friend:" + message;
            }
            File file = new File(username);
            if(!file.exists()){
                file = new File(context.getFilesDir(), username);
            }
            FileWriter writer = new FileWriter(file, true);
            writer.append(msg);
            writer.write('\n');
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> GetMessage(Context context, String username){
        try {

                FileInputStream fileInputStream;
                fileInputStream = context.openFileInput(username);
                if(fileInputStream!= null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String text;
                    List<String> list = new ArrayList<>();
                    while ((text = bufferedReader.readLine()) != null) {
                        list.add(text);
                    }
                    return list;
                }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
