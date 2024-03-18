package com.example.booking;

public class UserIdRequest {
    private String userId;

    public UserIdRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}