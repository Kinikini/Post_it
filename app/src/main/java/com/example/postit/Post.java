package com.example.postit;

import java.sql.Timestamp;
import java.util.Date;

public class Post  {

    public Post(String user_id, String content, String title, String image_url, String categories, Boolean published,Date timestamp) {
        this.user_id = user_id;
        this.content = content;
        this.title = title;
        this.image_url = image_url;
        this.categories = categories;
        this.published = published;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getCategories() {
        return categories;
    }

    public Boolean getPublished() {
        return published;
    }

    public String user_id;
    public String content, title;
    public String image_url;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }



    public Date timestamp;
    public String categories;
    public Boolean published;

    public Post() {
    }


}
