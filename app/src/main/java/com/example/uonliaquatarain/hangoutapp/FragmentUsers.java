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

public class FragmentUsers extends Fragment {

    View view;
    private RecyclerView recyclerView;
    public static List<User> users;
    public FragmentUsers() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        users = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.users_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Set data after tab is moved back to its position.
        if(savedInstanceState != null){
            List<String> name = savedInstanceState.getStringArrayList("user_name_list");
            List<String> username = savedInstanceState.getStringArrayList("user_username_list");
            List<String> photo = savedInstanceState.getStringArrayList("user_photo_list");
            for(int i = 0; i < name.size(); i++){
                users.add(new User(name.get(i), username.get(i), photo.get(i)));
            }
            RecyclerViewAdapterFragmentUsers recyclerViewAdapterFragmentUsers = new RecyclerViewAdapterFragmentUsers(getContext(), users);
            recyclerView.setAdapter(recyclerViewAdapterFragmentUsers);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        List<String> name = new ArrayList<>();
        List<String> username = new ArrayList<>();
        List<String> photo = new ArrayList<>();
        for(int i = 0; i < users.size(); i++){
            name.add(users.get(i).getNAME());
            username.add(users.get(i).getUSERNAME());
            photo.add(users.get(i).getPHOTO());
        }
        outState.putStringArrayList("user_name_list", (ArrayList<String>) name);
        outState.putStringArrayList("user_username_list", (ArrayList<String>) username);
        outState.putStringArrayList("user_photo_list", (ArrayList<String>) photo);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<String> name = intent.getStringArrayListExtra(Constatnts.GET_ALL_USERS + "names");
            List<String> username = intent.getStringArrayListExtra(Constatnts.GET_ALL_USERS + "usernames");
            List<String> pictures = intent.getStringArrayListExtra(Constatnts.GET_ALL_USERS + "pictures");

            //Set users in Recycler View
            for(int i = 0; i < name.size(); i++){
                    users.add(new User(name.get(i), username.get(i), pictures.get(i)));
            }
            RecyclerViewAdapterFragmentUsers recyclerViewAdapterFragmentUsers = new RecyclerViewAdapterFragmentUsers(getContext(), users);
            recyclerView.setAdapter(recyclerViewAdapterFragmentUsers);
        }
    };



    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.GET_ALL_USERS));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }


}
