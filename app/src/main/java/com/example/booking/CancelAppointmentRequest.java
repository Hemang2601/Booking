package com.example.booking;

public class CancelAppointmentRequest {
    private int appointmentId;

    public CancelAppointmentRequest(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
}
