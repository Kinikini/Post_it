package com.example.postit;

import java.util.Date;

public class Notification {

    private String notification_message;
    private String post_id;
    private Date timestamp;
    private String action_id;

    public String getAction_id() {
        return action_id;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public Notification() {
    }

    public Notification(String notification_message, String post_id, Date timestamp, String action_id) {
        this.notification_message = notification_message;
        this.post_id = post_id;
        this.timestamp = timestamp;
        this.action_id = action_id;
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
