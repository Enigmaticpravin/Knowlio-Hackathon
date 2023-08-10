package com.opion.knowlio.Class;

import java.util.Objects;

public class Post {
    String postid, publisherid, time, attachedFile, fileName, category, problemTxt, aiResponse, label;
    boolean edited, closed;

    public Post() {
    }

    public Post(String postid, String publisherid, String time, String attachedFile, String fileName, String category, String problemTxt, String aiResponse, String label, boolean edited, boolean closed) {
        this.postid = postid;
        this.publisherid = publisherid;
        this.time = time;
        this.attachedFile = attachedFile;
        this.fileName = fileName;
        this.category = category;
        this.problemTxt = problemTxt;
        this.aiResponse = aiResponse;
        this.label = label;
        this.edited = edited;
        this.closed = closed;
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

    public String getAttachedFile() {
        return attachedFile;
    }

    public void setAttachedFile(String attachedFile) {
        this.attachedFile = attachedFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProblemTxt() {
        return problemTxt;
    }

    public void setProblemTxt(String problemTxt) {
        this.problemTxt = problemTxt;
    }

    public String getAiResponse() {
        return aiResponse;
    }

    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
