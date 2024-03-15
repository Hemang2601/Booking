package com.example.booking;

public class LoginResponse {
    private boolean success;
    private String message;
    private String email;
    private int user_id;  // Updated field name
    private String username;

    // Getter methods for the new fields
    public String getEmail() {
        return email;
    }

    public int getUserId() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    // Existing getter methods
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
