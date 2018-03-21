package com.example.reviewapp.reviewapp.models;
import java.util.HashMap;

public class QueryModel {

    private String key;
    private String query;
    private String title;
    private String userid;
    private LocationModel location;
    private HashMap<String, AnswerModel> answers = new HashMap<>();
    private HashMap<String, VoteModel> votes = new HashMap<>();

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public HashMap<String, VoteModel> getVotes() {
        return votes;
    }

    public void setVotes(HashMap<String, VoteModel> votes) {
        this.votes = votes;
    }

    public HashMap<String,AnswerModel> getAnswers() {
        return answers;
    }

    public void setAnswers(HashMap<String,AnswerModel> answers) {
        this.answers = answers;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public QueryModel()
    {

    }
    public QueryModel(String title,String query,String userid, LocationModel location)
    {
        this.title = title;
        this.query = query;
        this.userid = userid;
        this.location = location;
    }
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getQuery() {
        return query;

    }

    public void setQuery(String query) {
        this.query = query;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
