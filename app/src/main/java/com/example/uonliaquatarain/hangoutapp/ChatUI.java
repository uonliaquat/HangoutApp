package com.example.uonliaquatarain.hangoutapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatUI extends AppCompatActivity {

    private EditText userInput;
    private RecyclerView recyclerView;
    List<ResponseMessage> responseMessageList;
    MessageAdapter messageAdapter;

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
                }
                return true;
            }
        });


    }

    public boolean isVisible(){
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int positionOfLastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int itemCount = recyclerView.getAdapter().getItemCount();
        return (positionOfLastVisibleItem >= itemCount);
    }
}
