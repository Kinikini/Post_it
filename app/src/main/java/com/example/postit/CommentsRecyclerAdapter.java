package com.example.postit;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comment> comment_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private TextView commentUserNameView;
    private ImageView authorProfilView;
    private TextView timestampView;




    public CommentsRecyclerAdapter(List<Comment> comment_list){
        this.comment_list = comment_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        holder.setIsRecyclable(false);
        long milliseconds = 0;
        try
        {
            milliseconds = comment_list.get(position).getTimestamp().getTime();
        }
        catch (Exception e)
        {
            milliseconds = new Date().getTime();
        }

        //long milliseconds =0;
        String pattern = "dd/MM/yyyy HH:mm:ss";
        String commentMessage = comment_list.get(position).getMessage();
        holder.set_comment_message(commentMessage);

        DateFormat df = new SimpleDateFormat(pattern);
        String date_string = df.format(milliseconds);
        holder.setTimestampView(date_string);

        String user_id = comment_list.get(position).getUser_id();
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



    }

    @Override
    public int getItemCount() {
        return comment_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        private TextView comment_message;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void set_comment_message(String message)
        {
            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }

        public void setUserNameView(String name)
        {
            commentUserNameView = mView.findViewById(R.id.commentUsername);
            commentUserNameView.setText(name);
        }

        public void setAuthorProfilView(String image)
        {
            authorProfilView = mView.findViewById(R.id.authorProfile);
            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderOptions).load(image).into(authorProfilView);
        }

        public void setTimestampView(String timestamp)
        {
            timestampView = mView.findViewById(R.id.commentDate);
            timestampView.setText(timestamp);
        }


    }


}
