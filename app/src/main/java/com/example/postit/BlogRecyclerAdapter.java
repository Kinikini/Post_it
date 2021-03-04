package com.example.postit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<Post> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;




    public BlogRecyclerAdapter(List<Post> blog_list){
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String postId = blog_list.get(position).postId;

        final String current_user_id = firebaseAuth.getCurrentUser().getUid();



        String content_data = blog_list.get(position).getContent();
        holder.setContentText(content_data);

        String title_data = blog_list.get(position).getTitle();
        holder.setTagsText(title_data);

        String categories_data = blog_list.get(position).getCategories();
        holder.setCategoriesText(categories_data);

        String image_data = blog_list.get(position).getImage_url();
        holder.setPostImage(image_data);

        String user_id = blog_list.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserNameView(userName);
                    holder.setAuthorProfilView(userImage);


                }
                else
                {

                }
            }
        });

        long milliseconds = blog_list.get(position).getTimestamp().getTime();
        String pattern = "dd/MM/yyyy HH:mm:ss";


        DateFormat df = new SimpleDateFormat(pattern);
        String date_string = df.format(milliseconds);
        holder.setTimestampView(date_string);

        firebaseFirestore.collection("Posts/"+postId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty())
                {
                    holder.setLikesCountView(queryDocumentSnapshots.size());
                }
                else
                {
                    holder.setLikesCountView(0);
                }
            }
        });

        firebaseFirestore.collection("Posts/"+postId+"/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty())
                {
                    holder.setCommentsCountView(queryDocumentSnapshots.size());
                }
                else
                {
                    holder.setCommentsCountView(0);
                }
            }
        });

        firebaseFirestore.collection("Posts/"+postId+"/Likes").document(current_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    holder.post_like_btn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

                }
                else
                {
                    holder.post_like_btn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
                }
            }
        });

        holder.post_like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                firebaseFirestore.collection("Posts/"+postId+"/Likes").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists())
                        {
                            Map<String,Object> likesMap = new HashMap<>();

                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts/"+postId+"/Likes").document(current_user_id).set(likesMap);

                            firebaseFirestore.collection("Users")
                                    .document(current_user_id).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                final String commentUserName = task.getResult().getString("name");


                                                firebaseFirestore.collection("Posts/")
                                                        .document(postId).get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {


                                                                    final String postUserId = task.getResult().getString("user_id");

                                                                    if (!postUserId.equals(current_user_id))
                                                                    {

                                                                        final String notification_message = "like";

                                                                        Map<String, Object> notificationsMap = new HashMap<>();
                                                                        notificationsMap.put("notification_message", notification_message);
                                                                        notificationsMap.put("post_id", postId);
                                                                        notificationsMap.put("action_id",current_user_id);
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
                        else
                        {
                            firebaseFirestore.collection("Posts/"+postId+"/Likes").document(current_user_id).delete();
                        }
                    }
                });



            }
        });

        holder.post_like_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/"+postId+"/Likes").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists())
                        {
                            Map<String,Object> likesMap = new HashMap<>();

                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts/"+postId+"/Likes").document(current_user_id).set(likesMap);

                            firebaseFirestore.collection("Users")
                                    .document(current_user_id).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                final String commentUserName = task.getResult().getString("name");


                                                firebaseFirestore.collection("Posts/")
                                                        .document(postId).get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {


                                                                    final String postUserId = task.getResult().getString("user_id");

                                                                    if (!postUserId.equals(current_user_id))
                                                                    {

                                                                        final String notification_message = "like";

                                                                        Map<String, Object> notificationsMap = new HashMap<>();
                                                                        notificationsMap.put("notification_message", notification_message);
                                                                        notificationsMap.put("post_id", postId);
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
                        else
                        {
                            firebaseFirestore.collection("Posts/"+postId+"/Likes").document(current_user_id).delete();
                        }


                    }
                });

            }
        });

        holder.post_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blogPostId",postId);
                context.startActivity(commentIntent);
            }
        });

        holder.post_comment_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blogPostId",postId);
                context.startActivity(commentIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView contentView;
        private TextView tagsView;
        private TextView categoriesView;
        private TextView postUserNameView;

        private View mView;
        private CircleImageView authorProfilView;

        private ImageView postImageView;

        private TextView timestampView;


        private ImageView post_like_btn;
        private TextView post_like_count;
        private ImageView post_comment_btn;
        private TextView post_comment_count;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);



            mView = itemView;

            post_like_btn = mView.findViewById(R.id.post_like_btn);
            post_comment_btn = mView.findViewById(R.id.post_comment_btn);
            post_comment_count = mView.findViewById(R.id.post_comment_count);
            post_like_count = mView.findViewById(R.id.post_like_count);
        }

        public void setContentText(String text){
            contentView = mView.findViewById(R.id.postContent);
            contentView.setText(text);


        }

        public void setTagsText(String text)
        {
            tagsView = mView.findViewById(R.id.postTitle);
            tagsView.setText(text);
        }

        public void setCategoriesText(String text)
        {
            categoriesView = mView.findViewById(R.id.postCategorie);
            categoriesView.setText(text);
        }

        public void setPostImage(String downloadUri)
        {
            postImageView = mView.findViewById(R.id.postImage);
            Glide.with(context).load(downloadUri).into(postImageView);
        }

        public void setTimestampView(String timestamp)
        {
            timestampView = mView.findViewById(R.id.postDate);
            timestampView.setText(timestamp);
        }

        public void setUserNameView(String name)
        {
            postUserNameView = mView.findViewById(R.id.postUsername);
            postUserNameView.setText(name);
        }

        public void setAuthorProfilView(String image)
        {
            authorProfilView = mView.findViewById(R.id.authorProfile);
            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(image).into(authorProfilView);
        }

        public void setLikesCountView(int likes)
        {
            post_like_count = mView.findViewById(R.id.post_like_count);



            post_like_count.setText(likes+" "+context.getResources().getString(R.string.likes).toString());
        }

        public void setCommentsCountView(int likes)
        {
            post_comment_count = mView.findViewById(R.id.post_comment_count);



            post_comment_count.setText(likes+" "+context.getResources().getString(R.string.comments).toString());
        }
    }


}
