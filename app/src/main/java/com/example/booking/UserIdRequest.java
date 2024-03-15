package com.example.booking;

import com.google.gson.annotations.SerializedName;

public class UserIdRequest {
    @SerializedName("userId")
    private String userId;

    public UserIdRequest(String userId) {
        this.userId = userId;
    }
}
