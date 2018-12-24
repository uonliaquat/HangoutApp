package com.example.uonliaquatarain.hangoutapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FragmentMemories extends Fragment {

    private Context context;
    private Button add_memory_btn;
    private Uri imageUri;
    private static final int PICK_IMAGE = 100;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private String downloadUri;
    private RecyclerView recyclerView;
    public  List<MemoryItem> memoryItems;
    private RecyclerViewAdapterFragmentMemory recyclerViewAdapterFragmentMemory;

    public FragmentMemories(){

    }
    @SuppressLint("ValidFragment")
    public FragmentMemories(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        memoryItems = new ArrayList<>();
        if(getArguments() != null){
            for(int i = 0; i < MainActivity.fragmentFriends.friends.size(); i++) {
                String name = getArguments().getString(MainActivity.fragmentMemories.memoryItems.get(i).getProfileName());
                String username = getArguments().getString(MainActivity.fragmentMemories.memoryItems.get(i).getUsername());
                String photo = getArguments().getString(MainActivity.fragmentMemories.memoryItems.get(i).getProfilePhoto());
                String bg_photo = getArguments().getString(MainActivity.fragmentMemories.memoryItems.get(i).getBackground());
                memoryItems.add(new MemoryItem(bg_photo, name, photo, username));
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.memories_fragment, container, false);
        add_memory_btn = (Button) view.findViewById(R.id.add_memory_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.memorry_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState != null){
            List<String> name = savedInstanceState.getStringArrayList("memory_name_list");
            List<String> username = savedInstanceState.getStringArrayList("memory_username_list");
            List<String> photo = savedInstanceState.getStringArrayList("memory_photo_list");
            List<String> bg_photo = savedInstanceState.getStringArrayList("memory_background_photo_list");
            for(int i = 0; i < name.size(); i++){
                memoryItems.add(new MemoryItem(bg_photo.get(i), name.get(i), photo.get(i), username.get(i)));
            }
            RecyclerViewAdapterFragmentMemory recyclerViewAdapterFragmentMemory = new RecyclerViewAdapterFragmentMemory(getContext(), memoryItems);
            recyclerView.setAdapter(recyclerViewAdapterFragmentMemory);
        }
        else{
            List<String> data = Splash.databaseAdapter.getData();
            SendRequest getMemory = new SendRequest();
            getMemory.execute(Constatnts.GET_MEMORY, data.get(1));
        }

        add_memory_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

        return view;

    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        List<String> name = new ArrayList<>();
        List<String> username = new ArrayList<>();
        List<String> photo = new ArrayList<>();
        List<String> bg_photo = new ArrayList<>();
        for(int i = 0; i < memoryItems.size(); i++){
            name.add(memoryItems.get(i).getProfileName());
            username.add(memoryItems.get(i).getUsername());
            photo.add(memoryItems.get(i).getProfilePhoto());
            bg_photo.add(memoryItems.get(i).getBackground());
        }
        outState.putStringArrayList("memory_name_list", (ArrayList<String>) name);
        outState.putStringArrayList("memory_username_list", (ArrayList<String>) username);
        outState.putStringArrayList("memory_photo_list", (ArrayList<String>) photo);
        outState.putStringArrayList("memory_background_photo_list", (ArrayList<String>) bg_photo);
        super.onSaveInstanceState(outState);

    }


    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();


            mStorageReference = FirebaseStorage.getInstance().getReference("Memory_Pics");
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Memory_Pics");
            UploadPic();

        }
    }

    private void UploadPic(){
        if(imageUri != null){
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            UploadTask uploadTask = fileReference.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUri = task.getResult().toString();
                        List<String> data = Splash.databaseAdapter.getData();
                        SendRequest sendRequest = new SendRequest();
                        sendRequest.execute(Constatnts.SAVE_MEMORY, data.get(1), downloadUri);

                    } else {
                        Toast.makeText(context, "Couldn't post your Memory!", Toast.LENGTH_SHORT).show();
                        // Handle failures
                        // ...
                    }
                }
            });

        }
        else{
            Toast.makeText(context, "No Picture Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getExtras().getString(Constatnts.SAVE_MEMORY );
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            List<String> data = Splash.databaseAdapter.getData();
            SendRequest getMemory = new SendRequest();
            getMemory.execute(Constatnts.GET_MEMORY, data.get(1));
        }
    };


    private BroadcastReceiver broadcastReceiver_GetMemory = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RecyclerViewAdapterFragmentMemory c = (RecyclerViewAdapterFragmentMemory) recyclerView.getAdapter();
            if(c != null) {
                c.clear();
                for(int i = 0; i< c.memoryItems.size(); i++) {
                    c.memoryItems.remove(i);
                    c.notifyItemRemoved(i);
                }
            }
            List<String> name = intent.getStringArrayListExtra(Constatnts.GET_MEMORY + "name");
            List<String> username = intent.getStringArrayListExtra(Constatnts.GET_MEMORY + "username");
            List<String> pictures = intent.getStringArrayListExtra(Constatnts.GET_MEMORY + "image_url");
            List<String> bg_picture = intent.getStringArrayListExtra(Constatnts.GET_MEMORY + "memory_url");

            //Set users in Recycler View
            for(int i = 0; i < name.size(); i++){
                memoryItems.add(new MemoryItem(bg_picture.get(i), name.get(i) ,pictures.get(i), username.get(i)));
            }
            recyclerViewAdapterFragmentMemory = new RecyclerViewAdapterFragmentMemory(getContext(), memoryItems);
            recyclerView.setAdapter(recyclerViewAdapterFragmentMemory);
        }
    };



    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.SAVE_MEMORY));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver_GetMemory, new IntentFilter(Constatnts.GET_MEMORY));
        List<String> data = Splash.databaseAdapter.getData();
        SendRequest getMemory = new SendRequest();
        getMemory.execute(Constatnts.GET_MEMORY, data.get(1));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        //LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver_GetMemory);
    }

}
