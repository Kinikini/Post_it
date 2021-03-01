package com.example.postit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<Post> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;

    public BlogRecyclerAdapter(List<Post> blog_list){
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

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
