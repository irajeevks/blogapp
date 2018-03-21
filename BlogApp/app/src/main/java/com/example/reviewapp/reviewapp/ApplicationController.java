package com.example.reviewapp.reviewapp;

import android.app.Application;
import android.content.Context;

import com.example.reviewapp.reviewapp.models.UserModel;

public class ApplicationController extends Application{
    private static ApplicationController appContext;
    private UserModel userModel = new UserModel();

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }

    public static synchronized ApplicationController getInstance() {
        return appContext;
    }

    public static Context context() {
        return appContext;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
