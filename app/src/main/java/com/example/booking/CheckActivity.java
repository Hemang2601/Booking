package com.example.booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckActivity extends AppCompatActivity {
    private Button selectDateButton;
    private Button selectTimeButton;
    private Button checkButton;

    private String selectedDate;
    private String selectedTime;
    private TextView userIdTextView;
    private TextView helloUsernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        selectDateButton = findViewById(R.id.selectDateButton);
        selectTimeButton = findViewById(R.id.selectTimeButton);
        checkButton = findViewById(R.id.checkButton);

        userIdTextView = findViewById(R.id.userIdTextView);
        helloUsernameTextView = findViewById(R.id.helloUsernameTextView);

        // Retrieve user_id and username from Intent extras
        String userId = getIntent().getStringExtra("user_id");
        String username = getIntent().getStringExtra("username");

        // Display user_id and username in TextViews
        userIdTextView.setText("User ID: " + userId);
        helloUsernameTextView.setText(capitalizeEachWord(username));

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate != null && selectedTime != null) {
                    // Both date and time are selected, call API
                    sendDateTimeToAPI(selectedDate, selectedTime);
                } else {
                    // Date or time is not selected
                    Toast.makeText(CheckActivity.this, "Please select date and time", Toast.LENGTH_SHORT).show();
                }
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

    private void showDatePicker() {
        // Get current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Display selected date in a TextView
                        selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        selectDateButton.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        // Get current time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Display selected time in a TextView
                        selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        selectTimeButton.setText(selectedTime);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void sendDateTimeToAPI(String selectedDate, String selectedTime) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.sendDateTime(selectedDate, selectedTime);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // API call successful, parse JSON response
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);

                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        if (success) {
                            // Slot is available, show success message
                            Toast.makeText(CheckActivity.this, message, Toast.LENGTH_SHORT).show();
                            // Reset selected date and time
                            selectDateButton.setText("Select Date");
                            selectTimeButton.setText("Select Time");
                        } else {
                            // Slot is already booked or other error, show failure message
                            Toast.makeText(CheckActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // API call failed
                    Log.e("API Error", "Failed to send date and time to API: " + response.message());
                    // Handle API call failure here
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Failure in making network call
                Log.e("API Error", "Error in sending date and time to API: " + t.getMessage());
                // Handle network call failure here
            }
        });
    }

}