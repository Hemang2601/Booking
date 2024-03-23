package com.example.booking;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class HistoryActivity extends AppCompatActivity {

    private TextView userIdTextView;
    private static final String TAG = "HistoryActivity";
    private TextView helloUsernameTextView;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<History> histories; // Changed to History
    private ProgressBar progressBar;
    private CardView noDataCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        userIdTextView = findViewById(R.id.userIdTextView);
        helloUsernameTextView = findViewById(R.id.helloUsernameTextView);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar); // Initialize progressBar

        histories = new ArrayList<>(); // Changed to History
        historyAdapter = new HistoryAdapter(histories); // Changed to History
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historyAdapter);
        noDataCardView = findViewById(R.id.noDatacardView);

        // Retrieve user_id and username from Intent extras
        String userId = getIntent().getStringExtra("user_id");
        String username = getIntent().getStringExtra("username");

        // Display user_id and username in TextViews
        userIdTextView.setText("User ID: " + userId);
        helloUsernameTextView.setText(capitalizeEachWord(username));

        // Call API to get history data
        progressBar.setVisibility(View.VISIBLE);
        noDataCardView.setVisibility(View.GONE);
        getHistoryData(userId);
    }

    private void getHistoryData(String userId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        HistoryIdRequest historyIdRequest = new HistoryIdRequest(userId);
        Call<ResponseBody> call = apiService.getHistoryData(historyIdRequest);

        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE); // Hide progressBar
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);

                        List<History> histories = new ArrayList<>(); // Changed to History

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject historyObject = jsonArray.getJSONObject(i); // Changed to historyObject
                            int appointmentId = historyObject.getInt("appointment_id"); // Changed to history_id
                            String selectedDate = historyObject.getString("selected_date");
                            String selectedTime = historyObject.getString("selected_time");
                            String selectedBank = historyObject.getString("selected_bank");
                            String transactionTypes = historyObject.getString("transaction_types");
                            String status = historyObject.getString("status");
                            String token = historyObject.getString("token");

                            History history = new History(appointmentId, selectedDate, selectedTime, // Changed to History
                                    selectedBank, transactionTypes, status, token); // Changed to History
                            histories.add(history);
                        }

                        if (histories.isEmpty()) {
                            recyclerView.setVisibility(View.GONE); // Hide RecyclerView if no data
                            Toast.makeText(HistoryActivity.this, "No history found", Toast.LENGTH_SHORT).show();
                            noDataCardView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView if data available
                            historyAdapter = new HistoryAdapter(histories); // Changed to History
                            recyclerView.setAdapter(historyAdapter);
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(HistoryActivity.this, "Failed to process response", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to parse JSON response", e); // Changed to TAG
                        noDataCardView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(HistoryActivity.this, "Failed to get history data", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "HTTP error: " + response.code()); // Changed to TAG
                    noDataCardView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                noDataCardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HistoryActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error", t); // Changed to TAG
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
}
