package com.example.postit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;

    private EditText postContent;
    private EditText postTags;
    private Button submitPost;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;

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
                            categories.setText("");
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

        postTags = (EditText)findViewById(R.id.post_title);

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


        submitPost.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                String content = postContent.getText().toString();
                String tags = postTags.getText().toString();

                if(!TextUtils.isEmpty(content) && !TextUtils.isEmpty(tags))
                {
                    Map<String, Object> postMap = new HashMap<>();
                    Boolean published = new Boolean(false);


                    postMap.put("content", content);
                    postMap.put("tags", tags);
                    postMap.put("published",published);
                    postMap.put("user_id",current_user_id);
                    //postMap.put("timestamp", FieldValue.serverTimestamp());

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

                    Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.unsubmited_post_msg).toString(),Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
    }
}