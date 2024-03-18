package com.example.booking;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("register.php")
    Call<ResponseBody> registerUser(@Body User user);

    @POST("login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);


    @POST("profiledata.php")
    Call<ResponseBody> fetchUserData(@Body UserIdRequest userIdRequest);

    @POST("change_password.php")
    Call<ResponseBody> changePassword(@Body UserIdPasswordRequest request);

    @POST("send_appointment_data.php")
    Call<ResponseBody> sendAppointmentData(@Body AppointmentRequest appointmentRequest);

    @POST("status.php")
    Call<ResponseBody> getStatusData(@Body StatusIdRequest statusIdRequest);
}
