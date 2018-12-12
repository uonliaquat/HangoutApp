package com.example.uonliaquatarain.hangoutapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.MimeTypeFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EditProfile extends AppCompatActivity {

    private ImageView imageView;
    private EditText name;
    private Button save_btn;
    private Uri imageUri;
    private static final int PICK_IMAGE = 100;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private static final int DIALOG_PROGRESS = 1;
    private ProgressDialog progressDialog;
    private String downloadUri;
    private String _name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();

        mStorageReference = FirebaseStorage.getInstance().getReference("Profile_Pics");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Profile_Pics");
        save_btn = (Button) findViewById(R.id.saveBtn_editProfile);
        imageView = (ImageView) findViewById(R.id.image_editProfile);
        name = (EditText) findViewById(R.id.name_editPrrofile);

        progressDialog = new ProgressDialog(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading!");
        progressDialog.setMessage("Please wait while we upload you picture!");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        Intent intent = getIntent();
        _name = intent.getStringExtra("name");
        name.setText(_name);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                UploadPic();
            }
        });
    }


    private void UploadPic(){
        if(imageUri != null){
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            //byte[] bytes = compressImage(imageUri);
            UploadTask uploadTask = fileReference.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();
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
                        sendRequest.execute(Constatnts.SAVE_PROFILE_PIC, data.get(1), downloadUri, name.getText().toString());
                        progressDialog.setProgress(100);
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Couldn't Upload Picture!", Toast.LENGTH_SHORT).show();
                        // Handle failures
                        // ...
                    }
                }
            });

        }
        else{
            Toast.makeText(getApplicationContext(), "No Picture Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case DIALOG_PROGRESS:

                return progressDialog;
            default:
                return null;
        }
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(Constatnts.SAVE_PROFILE_PIC);
            Toast.makeText(getApplication(), result, Toast.LENGTH_LONG).show();
            //Splash.databaseAdapter.savePic(_name, getPath(imageUri));
        }
    };


    public String getPath(Uri uri){
        if(uri == null){
            return  null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor != null){
            int column_index  = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(EditProfile.this).registerReceiver(broadcastReceiver, new IntentFilter(Constatnts.SAVE_PROFILE_PIC));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(EditProfile.this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }


}
