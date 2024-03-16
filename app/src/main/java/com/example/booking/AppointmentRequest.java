package com.example.booking;

public class AppointmentRequest {
    private String userId;
    private String transactionTypes;
    private String selectedDate;
    private String selectedTime;
    private String selectedBank;

    // Constructor
    public AppointmentRequest(String userId, String transactionTypes, String selectedDate, String selectedTime, String selectedBank) {
        this.userId = userId;
        this.transactionTypes = transactionTypes;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
        this.selectedBank = selectedBank;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTransactionTypes() {
        return transactionTypes;
    }

    public void setTransactionTypes(String transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }

    public String getSelectedBank() {
        return selectedBank;
    }

    public void setSelectedBank(String selectedBank) {
        this.selectedBank = selectedBank;
    }
}
