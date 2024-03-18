package com.example.booking;

// Appointment.java
public class Appointment {
    private int appointmentId;
    private String selectedDate;
    private String selectedTime;
    private String selectedBank;
    private String transactionTypes;
    private String status;
    private String token;

    public Appointment(int appointmentId, String selectedDate, String selectedTime, String selectedBank, String transactionTypes, String status, String token) {
        this.appointmentId = appointmentId;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
        this.selectedBank = selectedBank;
        this.transactionTypes = transactionTypes;
        this.status = status;
        this.token = token;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
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

    public String getTransactionTypes() {
        return transactionTypes;
    }

    public void setTransactionTypes(String transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
