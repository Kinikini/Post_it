package com.example.postit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<Post> blog_list;

    public BlogRecyclerAdapter(List<Post> blog_list){
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String content_data = blog_list.get(position).getPost_content();
        holder.setContentText(content_data);

        /*String tags_data = blog_list.get(position).getTags();
        holder.setTagsText(tags_data);*/
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView contentView;
        private TextView tagsView;
        private View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setContentText(String text){
            contentView = mView.findViewById(R.id.blog_content);
            contentView.setText(text);


        }

        public void setTagsText(String text)
        {
            tagsView = mView.findViewById(R.id.tags);
            tagsView.setText(text);
        }
    }


}
