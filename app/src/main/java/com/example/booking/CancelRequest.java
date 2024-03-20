package com.example.booking;

public class CancelRequest {
    private String userId;

    public CancelRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
