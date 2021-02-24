package com.example.postit;

import java.sql.Timestamp;

public class Post  {

    private String user_id;
    private String content;
    private String tags;
    private Timestamp timestamp;
    private Boolean published;

    public Post() {
    }

    public Post(String content, Boolean published, String tags, Timestamp timestamp, String user_id) {
        this.user_id = user_id;
        this.content = content;
        tags = tags;
        this.timestamp = timestamp;
        this.published = published;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_content() {
        return content;
    }

    public void setPost_content(String post_content) {
        this.content = post_content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        tags = tags;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}
