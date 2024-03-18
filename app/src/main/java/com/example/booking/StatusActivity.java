package com.example.booking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);

        String userId = getIntent().getStringExtra("user_id");
        getStatusData(userId);
    }

    private void getStatusData(String userId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        StatusIdRequest statusIdRequest = new StatusIdRequest(userId);
        Call<ResponseBody> call = apiService.getStatusData(statusIdRequest);

        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);

                        List<Appointment> appointments = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject appointmentObject = jsonArray.getJSONObject(i);
                            int appointmentId = appointmentObject.getInt("appointment_id");
                            String selectedDate = appointmentObject.getString("selected_date");
                            String selectedTime = appointmentObject.getString("selected_time");
                            String selectedBank = appointmentObject.getString("selected_bank");
                            String transactionTypes = appointmentObject.getString("transaction_types");
                            String status = appointmentObject.getString("status");
                            String token = appointmentObject.getString("token");

                            Appointment appointment = new Appointment(appointmentId, selectedDate, selectedTime,
                                    selectedBank, transactionTypes, status, token);
                            appointments.add(appointment);
                            progressBar.setVisibility(View.GONE);
                        }

                        if (appointments.isEmpty()) {
                            recyclerView.setVisibility(View.GONE); // Hide RecyclerView if no data
                            Toast.makeText(StatusActivity.this, "No appointments found", Toast.LENGTH_SHORT).show();
                        } else {
                            recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView if data available
                            appointmentAdapter = new AppointmentAdapter(appointments);
                            recyclerView.setAdapter(appointmentAdapter);
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(StatusActivity.this, "Failed to process response", Toast.LENGTH_SHORT).show();
                        Log.e("StatusActivity", "Failed to parse JSON response", e);
                    }
                } else {
                    Toast.makeText(StatusActivity.this, "Failed to get status data", Toast.LENGTH_SHORT).show();
                    Log.e("StatusActivity", "HTTP error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StatusActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("StatusActivity", "Network error", t);
            }
        });
    }
}
