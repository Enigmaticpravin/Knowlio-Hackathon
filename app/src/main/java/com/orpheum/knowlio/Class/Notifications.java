package com.orpheum.knowlio.Class;

public class Notifications {
    String authorid, commentorid, text, time, notificationid, type, postid;
    boolean seen;

    public Notifications() {
    }

    public Notifications(String authorid, String commentorid, String text, String time, String notificationid, String type, String postid, boolean seen) {
        this.authorid = authorid;
        this.commentorid = commentorid;
        this.text = text;
        this.time = time;
        this.notificationid = notificationid;
        this.type = type;
        this.postid = postid;
        this.seen = seen;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getCommentorid() {
        return commentorid;
    }

    public void setCommentorid(String commentorid) {
        this.commentorid = commentorid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotificationid() {
        return notificationid;
    }

    public void setNotificationid(String notificationid) {
        this.notificationid = notificationid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
