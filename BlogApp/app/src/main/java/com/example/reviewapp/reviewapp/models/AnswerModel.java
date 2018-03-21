package com.example.reviewapp.reviewapp.models;

import java.io.Serializable;

public class AnswerModel implements Serializable {
    private String queryId;
    private String title;
    private String userid;

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
