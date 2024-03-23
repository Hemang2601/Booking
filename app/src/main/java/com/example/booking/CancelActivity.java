package com.example.booking;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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

public class CancelActivity extends AppCompatActivity implements CancelAdapter.CancelListener {
    private TextView userIdTextView;
    private CardView noDataCardView;
    private String userId; // Define userId as a class-level variable
    private RecyclerView recyclerView;
    private TextView helloUsernameTextView;
    private ProgressBar progressBar;
    private CancelAdapter cancelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel);

        userIdTextView = findViewById(R.id.userIdTextView);
        helloUsernameTextView = findViewById(R.id.helloUsernameTextView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);
        noDataCardView = findViewById(R.id.noDatacardView);

        // Retrieve user_id and username from Intent extras
        userId = getIntent().getStringExtra("user_id");
        String username = getIntent().getStringExtra("username");

        // Display user_id and username in TextViews
        userIdTextView.setText("User ID: " + userId);
        helloUsernameTextView.setText(capitalizeEachWord(username));
        progressBar.setVisibility(View.VISIBLE); // Show progress bar
        noDataCardView.setVisibility(View.GONE); // Show progress bar
        cancelBooking(userId);
    }

    private void cancelBooking(String userId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        CancelRequest cancelRequest = new CancelRequest(userId);
        Call<ResponseBody> call = apiService.cancelBooking(cancelRequest);
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
                            progressBar.setVisibility(View.GONE); // Hide progress bar
                        }

                        if (appointments.isEmpty()) {
                            Toast.makeText(CancelActivity.this, "No appointments found", Toast.LENGTH_SHORT).show();
                            noDataCardView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        } else {
                            recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView if data available
                            cancelAdapter = new CancelAdapter(appointments, CancelActivity.this);
                            recyclerView.setAdapter(cancelAdapter);
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(CancelActivity.this, "Failed to process response", Toast.LENGTH_SHORT).show();
                        Log.e("CancelActivity", "Failed to parse JSON response", e);
                        noDataCardView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(CancelActivity.this, "Failed to get status data", Toast.LENGTH_SHORT).show();
                    Log.e("CancelActivity", "HTTP error: " + response.code());
                    noDataCardView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                noDataCardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CancelActivity.this, "Network error, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String capitalizeEachWord(String text) {
        if (!TextUtils.isEmpty(text)) {
            String[] words = text.split("\\s+");
            StringBuilder result = new StringBuilder();
            for (String word : words) {
                if (!TextUtils.isEmpty(word)) {
                    result.append(word.substring(0, 1).toUpperCase())
                            .append(word.substring(1).toLowerCase()).append(" ");
                }
            }
            return result.toString().trim();
        }
        return text;
    }

    @Override
    public void onCancel(int appointmentId) {
        // Implement canceling appointment API call here using Retrofit
        Log.d("CancelActivity", "Initiating appointment cancellation for ID: " + appointmentId);

        // Create a dialog to confirm the cancellation
        AlertDialog.Builder builder = new AlertDialog.Builder(CancelActivity.this);
        builder.setTitle("Confirm Cancellation");
        builder.setMessage("Are you sure you want to cancel this appointment?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed cancellation, proceed with cancellation API call
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                performCancellation(appointmentId);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User canceled cancellation, do nothing
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void performCancellation(int appointmentId) {
        // Create a CancelAppointmentRequest object with the appointment ID
        CancelAppointmentRequest cancelRequest = new CancelAppointmentRequest(appointmentId);

        // Create the Retrofit API service instance
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Make the POST request to cancel the appointment
        Call<ResponseBody> call = apiService.cancelAppointment(cancelRequest);

        // Enqueue the call to execute asynchronously
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Appointment canceled successfully, you can handle UI updates or show a message
                    Log.d("CancelActivity", "Appointment canceled successfully for ID: " + appointmentId);

                    // Log the response body if available
                    try {
                        String responseBody = response.body().string();
                        Log.d("CancelActivity", "Response Body: " + responseBody);
                        progressBar.setVisibility(View.VISIBLE); // Show progress bar
                        cancelBooking(userId); // Refresh the appointment list
                    } catch (IOException e) {
                        Log.e("CancelActivity", "Error reading response body", e);
                    }

                    Toast.makeText(CancelActivity.this, "Appointment canceled successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful cancellation, show error message
                    Log.e("CancelActivity", "Failed to cancel appointment for ID: " + appointmentId);
                    Toast.makeText(CancelActivity.this, "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
                    noDataCardView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                // Handle network failure
                noDataCardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Log.e("CancelActivity", "Network error while canceling appointment for ID: " + appointmentId, t);
                Toast.makeText(CancelActivity.this, "Network error, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
