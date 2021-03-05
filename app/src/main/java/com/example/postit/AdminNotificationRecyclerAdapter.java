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

public class AdminNotificationRecyclerAdapter extends RecyclerView.Adapter<AdminNotificationRecyclerAdapter.ViewHolder> {

    public List<Notification> notification_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private TextView notificationMessageView;

    private CircleImageView authorProfilView;
    private TextView postUserNameView;
    private TextView postTitleView;




    public AdminNotificationRecyclerAdapter(List<Notification> notification_list){
        this.notification_list = notification_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new AdminNotificationRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);



        String notification_type = notification_list.get(position).getNotification_message();

        if(notification_type.equals("comment"))
        {
            holder.setNotificationText(context.getResources().getString(R.string.notification_comment_message).toString());
        }
        else if((notification_type.equals("like")))
        {
            holder.setNotificationText(context.getResources().getString(R.string.notification_like_message).toString());
        }
        else
        {
            holder.setNotificationText(notification_type);
        }


        String action_id = notification_list.get(position).getAction_id();

        String post_id = notification_list.get(position).getPost_id();

        firebaseFirestore.collection("Users").document(action_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

        firebaseFirestore.collection("Posts").document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String title = task.getResult().getString("title");


                    holder.setPostTitleView(title);


                }
                else
                {

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return notification_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView notification_message;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setNotificationText(String text){
            notificationMessageView = mView.findViewById(R.id.notification_message);
            notificationMessageView.setText(text);


        }

        public void setAuthorProfilView(String image)
        {
            authorProfilView = mView.findViewById(R.id.notification_profil);
            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(image).into(authorProfilView);
        }

        public void setUserNameView(String name)
        {
            postUserNameView = mView.findViewById(R.id.notification_user_name);
            postUserNameView.setText(name);
        }

        public void setPostTitleView(String name)
        {
            postTitleView = mView.findViewById(R.id.notif_post_title);
            postTitleView.setText(name);
        }




    }


}
