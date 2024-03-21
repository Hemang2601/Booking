package com.example.booking;

public class HistoryIdRequest {
    private String userId;

    public HistoryIdRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
