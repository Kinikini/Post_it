package com.example.postit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;

    private EditText postContent;
    private EditText postTitle;
    private TextView postCategories;
    private ImageView postImage;
    private Button submitPost;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;

    private Uri postImageUri;

    private Toolbar go_back;

    private TextView categories;

    private boolean[] selectedCategories;

    private ArrayList<Integer> categorieList = new ArrayList<>();


    private String[] categorieArray;




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        categories = findViewById(R.id.categories);

        categorieArray = new String[10];


        categorieArray[0]=getResources().getString(R.string.sport).toString();
        categorieArray[1]=getResources().getString(R.string.health).toString();
        categorieArray[2]=getResources().getString(R.string.Food).toString();
        categorieArray[3]=getResources().getString(R.string.political).toString();
        categorieArray[4]=getResources().getString(R.string.news).toString();
        categorieArray[5]=getResources().getString(R.string.gaming).toString();
        categorieArray[6]=getResources().getString(R.string.music).toString();
        categorieArray[7]=getResources().getString(R.string.moovie).toString();
        categorieArray[8]=getResources().getString(R.string.fashion).toString();
        categorieArray[9]=getResources().getString(R.string.travel).toString();


        selectedCategories = new boolean[categorieArray.length];

        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        NewPostActivity.this
                );

                builder.setTitle(getResources().getString(R.string.select_categories).toString());
                builder.setCancelable(false);

                builder.setMultiChoiceItems(categorieArray, selectedCategories, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){

                            categorieList.add(which);

                            Collections.sort(categorieList);

                        }
                        else
                        {

                            categorieList.remove(Integer.valueOf(which));

                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();

                        for(int i = 0 ; i<categorieList.size(); i++)
                        {
                            stringBuilder.append(categorieArray[categorieList.get(i)]);

                            if(i!=categorieList.size()-1)
                            {
                                stringBuilder.append(", ");
                            }
                        }

                        categories.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.cancel).toString(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton(getResources().getString(R.string.clear_all).toString(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i< selectedCategories.length; i++)
                        {
                            selectedCategories[i]=false;
                            categorieList.clear();
                            categories.setText("".toString());
                        }
                    }
                });

                builder.show();
            }
        });



        storageReference = (StorageReference) FirebaseStorage.getInstance().getReference();
        firebaseFirestore = (FirebaseFirestore) FirebaseFirestore.getInstance();
        firebaseAuth = (FirebaseAuth) FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();


        newPostToolbar = findViewById(R.id.go_back);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add new post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        postContent = (EditText)findViewById(R.id.post_content);

        postTitle = (EditText)findViewById(R.id.post_title);


        postImage = (ImageView)findViewById(R.id.post_image);


        submitPost = (Button)findViewById(R.id.submit_post);

        go_back = (Toolbar) findViewById(R.id.go_back);

        go_back.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(NewPostActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });


        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);
            }
        });


        submitPost.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                final String content = postContent.getText().toString();
                final String title = postTitle.getText().toString();
                final String post_categories = categories.getText().toString();

                if(!TextUtils.isEmpty(content)
                        && !TextUtils.isEmpty(title)
                        && postImageUri != null
                        && categories.getText().length()!=0)
                {


                    String randomName = randomString(18);;


                    final StorageReference imagePath = storageReference.child("post_images").child(randomName+".jpg");
                    UploadTask uploadTask = imagePath.putFile(postImageUri);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return imagePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String downloadUri = task.getResult().toString();

                                Map<String, Object> postMap = new HashMap<>();
                                Boolean published = new Boolean(false);




                                postMap.put("content", content);
                                postMap.put("title", title);
                                postMap.put("published",published);
                                postMap.put("categories",post_categories);
                                postMap.put("image_url",downloadUri);
                                postMap.put("user_id",current_user_id);
                                postMap.put("timestamp", FieldValue.serverTimestamp());


                                firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        if(task.isSuccessful())
                                        {
                                            Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.submited_post_msg).toString(),Toast.LENGTH_LONG);
                                            toast.show();

                                            Intent mainIntent = new Intent(NewPostActivity.this,MainActivity.class);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                        else
                                        {

                                        }
                                    }
                                });

                            }
                            else
                            {

                            }






                        }
                    });



                }
                else if(postImageUri == null)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.fill_image).toString(),Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(categories.getText().length()==0)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.fill_categorie).toString(),Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {

                    Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.unsubmited_post_msg).toString(),Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                postImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }


}