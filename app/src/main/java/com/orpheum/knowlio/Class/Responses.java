package com.orpheum.knowlio.Class;

public class Responses {
    String respondid, postid, publisherid, time, respondTxt;

    String matching, tag;

    public Responses() {
    }

    public Responses(String respondid, String postid, String publisherid, String time, String respondTxt, String matching, String tag) {
        this.respondid = respondid;
        this.postid = postid;
        this.publisherid = publisherid;
        this.time = time;
        this.respondTxt = respondTxt;
        this.matching = matching;
        this.tag = tag;
    }

    public String getRespondid() {
        return respondid;
    }

    public void setRespondid(String respondid) {
        this.respondid = respondid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRespondTxt() {
        return respondTxt;
    }

    public void setRespondTxt(String respondTxt) {
        this.respondTxt = respondTxt;
    }

    public String getMatching() {
        return matching;
    }

    public void setMatching(String matching) {
        this.matching = matching;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
