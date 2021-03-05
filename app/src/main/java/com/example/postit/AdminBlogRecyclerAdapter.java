package com.example.postit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminBlogRecyclerAdapter extends RecyclerView.Adapter<AdminBlogRecyclerAdapter.ViewHolder> {

    public List<Post> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;




    public AdminBlogRecyclerAdapter(List<Post> blog_list){
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_blog_list_item,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String postId = blog_list.get(position).postId;

        final String current_user_id = firebaseAuth.getCurrentUser().getUid();



        String content_data = blog_list.get(position).getContent();
        holder.setContentText(content_data);

        final String title_data = blog_list.get(position).getTitle();
        holder.setTagsText(title_data);

        String categories_data = blog_list.get(position).getCategories();
        holder.setCategoriesText(categories_data);

        String image_data = blog_list.get(position).getImage_url();
        holder.setPostImage(image_data);

        final String user_id = blog_list.get(position).getUser_id();
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

        holder.refuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts").document(postId).delete();

                Intent intent = new Intent(context.getApplicationContext(),AdminPanel.class);
                Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.unsuccessfull_published).toString(),Toast.LENGTH_SHORT).show();

                final String notification_message = context.getResources().getString(R.string.your_post).toString()+": "+title_data+" "+context.getResources().getString(R.string.not_been_published).toString();



                Map<String, Object> notificationsMap = new HashMap<>();
                notificationsMap.put("notification_message", notification_message);
                notificationsMap.put("post_id", postId);
                notificationsMap.put("action_id",current_user_id);
                notificationsMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Users/" + user_id + "/Notifications")
                        .add(notificationsMap)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {

                                }
                            }
                        });
                ((AppCompatActivity)context).finish();
                context.startActivity(intent);
            }


        });

        holder.validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,Object> uMap = new HashMap<>();
                uMap.put("published",new Boolean(true));
                firebaseFirestore.collection("Posts").document(postId).set(uMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.successfull_published).toString(),Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(context.getApplicationContext(),AdminPanel.class);

                        final String notification_message = context.getResources().getString(R.string.your_post).toString()+": "+title_data+" "+context.getResources().getString(R.string.been_published).toString();



                        Map<String, Object> notificationsMap = new HashMap<>();
                        notificationsMap.put("notification_message", notification_message);
                        notificationsMap.put("post_id", postId);
                        notificationsMap.put("action_id",current_user_id);
                        notificationsMap.put("timestamp", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("Users/" + user_id + "/Notifications")
                                .add(notificationsMap)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {

                                        }
                                    }
                                });



                        ((AppCompatActivity)context).finish();

                        context.startActivity(intent);


                    }
                });
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

        private Button refuseBtn, validateBtn;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            refuseBtn = itemView.findViewById(R.id.refuse_button);
            validateBtn = itemView.findViewById(R.id.validate_button);



            mView = itemView;

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

    }


}
