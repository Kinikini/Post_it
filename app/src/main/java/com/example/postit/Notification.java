package com.example.postit;

import java.util.Date;

public class Notification {

    private String notification_message;
    private String post_id;
    private Date timestamp;

    public Notification() {
    }

    public Notification(String notification_message, String post_id, Date timestamp) {
        this.notification_message = notification_message;
        this.post_id = post_id;
        this.timestamp = timestamp;
    }

    public String getNotification_message() {
        return notification_message;
    }

    public void setNotification_message(String notification_message) {
        this.notification_message = notification_message;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
