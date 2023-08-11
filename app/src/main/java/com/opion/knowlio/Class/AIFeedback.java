package com.opion.knowlio.Class;

public class AIFeedback {
    String publisherid, postid, rating, feedback;

    public AIFeedback() {
    }

    public AIFeedback(String publisherid, String postid, String rating, String feedback) {
        this.publisherid = publisherid;
        this.postid = postid;
        this.rating = rating;
        this.feedback = feedback;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
