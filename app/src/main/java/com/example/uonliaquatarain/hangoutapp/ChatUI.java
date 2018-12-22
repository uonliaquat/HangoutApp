package com.example.uonliaquatarain.hangoutapp;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatUI extends AppCompatActivity {

    private EditText userInput;
    private RecyclerView recyclerView;
    public  static List<ResponseMessage> responseMessageList;
    public static MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ui);

        userInput = (EditText) findViewById(R.id.user_input);
        recyclerView = (RecyclerView) findViewById(R.id.conversation);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(messageAdapter);

        Intent intent = getIntent();
        final String username = intent.getExtras().getString("username");

        //Get Messages From file!
        GetMessageFromFile(username);



        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    ResponseMessage responseMessage = new ResponseMessage(userInput.getText().toString(), true);
                    responseMessageList.add(responseMessage);
                    messageAdapter.notifyDataSetChanged();

                    if(!isVisible()){
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                    }

                    //Add message into file!
                    FileHandling.SaveMessage(getApplicationContext(), username ,userInput.getText().toString(), true);

                    //Sending message to your friend
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> list = Splash.databaseAdapter.getData();
                            String my_username = list.get(1);
                            try {
                                SendRequest.jsonObject.put("method", Constatnts.MESSAGE);
                                SendRequest.jsonObject.put("username", my_username);
                                SendRequest.jsonObject.put("username_receiver", username);
                                SendRequest.jsonObject.put("message", userInput.getText().toString());
                                SendRequest.socket = new Socket(SendRequest.SERVER_IP, SendRequest.SERVER_PORT);
                                SendRequest.writer = new PrintWriter(SendRequest.socket.getOutputStream());
                                SendRequest.writer.write(SendRequest.jsonObject.toString());
                                SendRequest.writer.flush();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
                return true;
            }
        });


    }

    private void GetMessageFromFile(String username){
        List<String> messages = FileHandling.GetMessage(getApplicationContext(), username);
        if(messages != null) {
            for (int i = 0; i < messages.size(); i++) {
                String msg = messages.get(i);
                String f_msg = "";
                char charArray[] = msg.toCharArray();
                boolean isMe = false;

                for (int j = 0; j < charArray.length; j++) {
                    if (charArray[j] == 'm' && charArray[j + 1] == 'e' && charArray[j + 2] == ':') {
                        j = j + 2;
                        isMe = true;
                    }
                    else if(charArray.length > 5) {
                        if (charArray[j] == 'f' && charArray[j + 1] == 'r' && charArray[j + 2] == 'i' && charArray[j + 3] == 'e'
                                && charArray[j + 4] == 'n' && charArray[j + 5] == 'd' && charArray[j + 6] == ':') {
                            isMe = false;
                            j = j + 6;
                        } else {
                            f_msg = f_msg + charArray[j];
                        }
                    }
                }
                if (isMe) {
                    ResponseMessage responseMessage = new ResponseMessage(f_msg, true);
                    responseMessageList.add(responseMessage);

                } else {
                    ResponseMessage responseMessage = new ResponseMessage(f_msg, false);
                    responseMessageList.add(responseMessage);
                }
                messageAdapter.notifyDataSetChanged();

            }
            if(!isVisible()){
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }
        }
    }


    public boolean isVisible(){
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int positionOfLastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int itemCount = recyclerView.getAdapter().getItemCount();
        return (positionOfLastVisibleItem >= itemCount);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatUI.this, MainActivity.class);
        intent.putExtra("activity_name", "chat_ui");
        startActivity(intent);
    }
}
