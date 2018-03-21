package com.example.reviewapp.reviewapp.models;

import java.io.Serializable;

public class VoteModel implements Serializable{
    public String userid;
    public int vote;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}
