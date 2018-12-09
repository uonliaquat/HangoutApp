package com.example.uonliaquatarain.hangoutapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FragmentFriends extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    public static List<User> friends;

    public FragmentFriends() {

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friends = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.friends_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Set data after tab is moved back to its position.
        if(savedInstanceState != null){
            List<String> name = savedInstanceState.getStringArrayList("friend_name_list");
            List<String> username = savedInstanceState.getStringArrayList("friend_username_list");
            List<String> photo = savedInstanceState.getStringArrayList("friend_photo_list");
            for(int i = 0; i < name.size(); i++){
                friends.add(new User(name.get(i), username.get(i), photo.get(i)));
            }
            RecyclerViewAdapterFragmentFriends recyclerViewAdapterFragmentFriends = new RecyclerViewAdapterFragmentFriends(getContext(), friends);
            recyclerView.setAdapter(recyclerViewAdapterFragmentFriends);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        List<String> name = new ArrayList<>();
        List<String> username = new ArrayList<>();
        List<String> photo = new ArrayList<>();
        for(int i = 0; i < friends.size(); i++){
            name.add(friends.get(i).getNAME());
            username.add(friends.get(i).getUSERNAME());
            photo.add(friends.get(i).getPHOTO());
        }
        outState.putStringArrayList("friend_name_list", (ArrayList<String>) name);
        outState.putStringArrayList("friend_username_list", (ArrayList<String>) username);
        outState.putStringArrayList("friend_photo_list", (ArrayList<String>) photo);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getExtras().getString("name");
            String username = intent.getExtras().getString( "username");
            String pic_url = intent.getExtras().getString("pic_url");

            //Set users in Friend List
            friends.add(new User(name, username, pic_url));
            RecyclerViewAdapterFragmentFriends recyclerViewAdapterFragmentFriends = new RecyclerViewAdapterFragmentFriends(getContext(), friends);
            recyclerView.setAdapter(recyclerViewAdapterFragmentFriends);
        }
    };



    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.FRIEND_REQUEST_ACCEPTED));
    }

    @Override
    public void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }


}
