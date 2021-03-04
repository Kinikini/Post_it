package com.example.postit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentToolbar;
    private EditText commentField;
    private ImageView commentPostBtn;
    private String blogPostId;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comment> commentList;
    private RecyclerView comment_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentToolbar = findViewById(R.id.comment_toolbar);

        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.comments).toString());

        commentField = findViewById(R.id.comment_field);
        commentPostBtn = findViewById(R.id.comment_post_btn);

        comment_list = findViewById(R.id.comment_list);

        commentList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);

        blogPostId = getIntent().getStringExtra("blogPostId");

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        currentUserId = firebaseAuth.getCurrentUser().getUid();



        firebaseFirestore.collection("Posts/"+blogPostId+"/Comments")
                .addSnapshotListener(CommentsActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                {
                    if(doc.getType() == DocumentChange.Type.ADDED)
                    {
                        String commentId = doc.getDocument().getId();
                        Comment comments = doc.getDocument().toObject(Comment.class);
                        commentList.add(comments);
                        commentsRecyclerAdapter.notifyDataSetChanged();

                    }
                }
            }
        });


        //Recycler


        commentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment_message = commentField.getText().toString();

                if(!comment_message.isEmpty())
                {
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("message",comment_message);
                    commentsMap.put("user_id",currentUserId);
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Posts/"+blogPostId+"/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(CommentsActivity.this,"Error posting comment: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                firebaseFirestore.collection("Users")
                                        .document(currentUserId).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    final String commentUserName = task.getResult().getString("name");


                                                    firebaseFirestore.collection("Posts/")
                                                            .document(blogPostId).get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {


                                                                        final String postUserId = task.getResult().getString("user_id");

                                                                        if (!postUserId.equals(currentUserId))
                                                                        {

                                                                            final String notification_message = "comment";

                                                                            Map<String, Object> notificationsMap = new HashMap<>();
                                                                            notificationsMap.put("notification_message", notification_message);
                                                                            notificationsMap.put("post_id", blogPostId);
                                                                            notificationsMap.put("action_id",currentUserId);
                                                                            notificationsMap.put("timestamp", FieldValue.serverTimestamp());
                                                                            firebaseFirestore.collection("Users/" + postUserId + "/Notifications")
                                                                                    .add(notificationsMap)
                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                            if (task.isSuccessful()) {

                                                                                            }
                                                                                        }
                                                                                    });

                                                                        }
                                                                    }
                                                                }
                                                            });

                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });




    }

}