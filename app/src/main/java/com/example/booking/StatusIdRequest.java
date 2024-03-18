// StatusIdRequest.java
package com.example.booking;

public class StatusIdRequest {
    private String userId;

    public StatusIdRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
