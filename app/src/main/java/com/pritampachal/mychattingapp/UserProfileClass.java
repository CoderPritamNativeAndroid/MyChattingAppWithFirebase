package com.pritampachal.mychattingapp;

public class UserProfileClass {
    private String userName,userId;

    public UserProfileClass() {
    }

    public UserProfileClass(String userName, String userId) {
        this.userName = userName;
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
