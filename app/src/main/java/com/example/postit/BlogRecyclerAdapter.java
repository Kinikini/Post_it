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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<Post> blog_list;
    public Context context;

    public BlogRecyclerAdapter(List<Post> blog_list){
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String content_data = blog_list.get(position).getContent();
        holder.setContentText(content_data);

        String title_data = blog_list.get(position).getTitle();
        holder.setTagsText(title_data);

        String categories_data = blog_list.get(position).getCategories();
        holder.setCategoriesText(categories_data);

        String image_data = blog_list.get(position).getImage_url();
        holder.setPostImage(image_data);
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView contentView;
        private TextView tagsView;
        private TextView categoriesView;

        private View mView;
        private CircleImageView authorProfilView;

        private ImageView postImageView;

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
    }


}
