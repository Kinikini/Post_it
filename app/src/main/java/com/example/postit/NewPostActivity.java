package com.example.postit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageReference = (StorageReference) FirebaseStorage.getInstance().getReference();
        firebaseFirestore = (FirebaseFirestore) FirebaseFirestore.getInstance();
        firebaseAuth = (FirebaseAuth) FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();


        newPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add new post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        postContent = (EditText)findViewById(R.id.post_content);

        postTags = (EditText)findViewById(R.id.post_tags);

        submitPost = (Button)findViewById(R.id.submit_post);

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
                    Toast toast = Toast.makeText(getApplicationContext(),getResources().getString(R.string.unsubmited_post_msg).toString(),Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
    }
}