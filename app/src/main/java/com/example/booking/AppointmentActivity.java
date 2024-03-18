package com.example.booking;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity {

    private Spinner bankSpinner;
    private TextView userIdTextView;
    private TextView helloUsernameTextView;
    private Button dateButton;
    private Button timeButton;
    private Button bankButton;
    private Button appointmentButton;
    private ProgressBar progressBar;
    private CardView appointmentformCardView;

    private CheckBox checkBoxDeposit,checkBoxWithdraw,checkBoxPassbook,checkBoxPrinting,checkBoxIPO,checkBoxLoan,checkBoxShare
            ,checkBoxBill,checkBoxAtm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);


        CheckBox checkBoxDeposit = findViewById(R.id.checkBoxDeposit);
        CheckBox checkBoxWithdraw = findViewById(R.id.checkBoxWithdraw);
        CheckBox checkBoxPassbook = findViewById(R.id.checkBoxPassbook);
        CheckBox checkBoxPrinting = findViewById(R.id.checkBoxPrinting);
        CheckBox checkBoxIPO = findViewById(R.id.checkBoxIPO);
        CheckBox checkBoxLoan = findViewById(R.id.checkBoxLoan);
        CheckBox checkBoxShare = findViewById(R.id.checkBoxShare);
        CheckBox checkBoxBill = findViewById(R.id.checkBoxBill);
        CheckBox checkBoxAtm = findViewById(R.id.checkBoxAtm);
        userIdTextView = findViewById(R.id.userIdTextView);
        helloUsernameTextView = findViewById(R.id.helloUsernameTextView);
        bankSpinner = findViewById(R.id.bankSpinner);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        progressBar = findViewById(R.id.progressBar);
        appointmentformCardView = findViewById(R.id.appointmentformCardView);

        appointmentButton = findViewById(R.id.appointmentButton);

        // Retrieve user_id and username from Intent extras
        String userId = getIntent().getStringExtra("user_id");
        String username = getIntent().getStringExtra("username");

        // Display user_id and username in TextViews
        userIdTextView.setText("User ID: " + userId);
        helloUsernameTextView.setText(capitalizeEachWord(username));

        // Set values for the bankSpinner programmatically
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bank_names_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankSpinner.setAdapter(adapter);

        // Set click listeners for the buttons
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(AppointmentActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Handle the date selected by the user
                                // For example, display the selected date in a TextView
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                dateButton.setText(selectedDate);
                            }
                        }, year, month, dayOfMonth);

                // Show the date picker dialog
                datePickerDialog.show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time
                Calendar calendar = Calendar.getInstance();
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Create a TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AppointmentActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int selectedHourOfDay, int selectedMinute) {
                                // Validate the selected time
                                if (isValidTime(selectedHourOfDay, selectedMinute)) {
                                    // Handle the valid time selected by the user
                                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHourOfDay, selectedMinute);
                                    timeButton.setText(selectedTime);
                                } else {
                                    // Show an error message or toast indicating invalid time selection
                                    Toast.makeText(AppointmentActivity.this, "Please select a time between 10 AM and 4 PM", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, hourOfDay, minute, false); // true for 24-hour format

                // Set minimum and maximum time range for TimePicker
                timePickerDialog.updateTime(10, 0); // Set minimum time to 10:00 AM
                timePickerDialog.updateTime(16, 0); // Set maximum time to 4:00 PM

                // Show the TimePickerDialog
                timePickerDialog.show();
            }
        });

        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected transaction types

                StringBuilder selectedTransactionTypes = new StringBuilder();
                if (checkBoxDeposit.isChecked()) {
                    selectedTransactionTypes.append("Deposit , ");
                }
                if (checkBoxWithdraw.isChecked()) {
                    selectedTransactionTypes.append("Withdraw , ");
                }
                if (checkBoxPassbook.isChecked()) {
                    selectedTransactionTypes.append("Passbook , ");
                }
                if (checkBoxPrinting.isChecked()) {
                    selectedTransactionTypes.append("Printing , ");
                }
                if (checkBoxIPO.isChecked()) {
                    selectedTransactionTypes.append("IPO , ");
                }
                if (checkBoxLoan.isChecked()) {
                    selectedTransactionTypes.append("Loan , ");
                }
                if (checkBoxShare.isChecked()) {
                    selectedTransactionTypes.append("Share , ");
                }
                if (checkBoxAtm.isChecked()) {
                    selectedTransactionTypes.append("ATM , ");
                }
                if (checkBoxBill.isChecked()) {
                    selectedTransactionTypes.append("Bill Payment , ");
                }

                // Remove the last comma and space
                String transactionTypes = selectedTransactionTypes.toString().trim();
                if (transactionTypes.endsWith(",")) {
                    transactionTypes = transactionTypes.substring(0, transactionTypes.length() - 2);
                }

                // Get selected date
                String selectedDate = dateButton.getText().toString();

                // Get selected time
                String selectedTime = timeButton.getText().toString();

                String selectedBank = bankSpinner.getSelectedItem().toString();

                // Check if the user has selected at least one transaction type
                if (transactionTypes.isEmpty()) {
                    // Show a toast message asking the user to select at least one transaction type
                    Toast.makeText(AppointmentActivity.this, "Please select at least one transaction type", Toast.LENGTH_SHORT).show();
                }
                // Check if the user has selected only date or only time
                else if (selectedDate.equals("Select Date")) {
                    // Show a toast message asking the user to select the date
                    Toast.makeText(AppointmentActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
                }
                else if (selectedTime.equals("Select Time")) {
                    // Show a toast message asking the user to select the time
                    Toast.makeText(AppointmentActivity.this, "Please select a time", Toast.LENGTH_SHORT).show();
                } // Check if the user has selected a valid bank
                else if (selectedBank.equals("Select Bank")) {
                    // Show a toast message asking the user to select a bank
                    Toast.makeText(AppointmentActivity.this, "Please select a bank", Toast.LENGTH_SHORT).show();
                } else {


                    // Log the selected bank
                    Log.d("Selected Bank", selectedBank);

                    // Print selected values in log
                    Log.d("AppointmentDetails", "Selected Transaction Types: " + transactionTypes);
                    Log.d("AppointmentDetails", "Selected Date: " + selectedDate);
                    Log.d("AppointmentDetails", "Selected Time: " + selectedTime);
                    Log.d("AppointmentDetails", "Selected Bank: " + selectedBank);
                    appointmentformCardView.setVisibility(View.GONE);
                    // Show progress bar
                    progressBar.setVisibility(View.VISIBLE);
                    sendAppointmentData(userId, selectedDate, selectedTime, selectedBank, transactionTypes);
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

    private void sendAppointmentData(String userId, String selectedDate, String selectedTime, String selectedBank, String transactionTypes) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Create an AppointmentRequest object with the appointment data
        AppointmentRequest appointmentRequest = new AppointmentRequest(userId, transactionTypes, selectedDate, selectedTime, selectedBank);

        // Make the network request
        Call<ResponseBody> call = apiService.sendAppointmentData(appointmentRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);
                // Show the form again
                appointmentformCardView.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {
                    try {
                        // Parse the response body JSON
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        // Check if the 'success' field exists and its value is true
                        if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                            // Appointment booked successfully
                            Toast.makeText(AppointmentActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                            Log.d("AppointmentResponse", "Appointment booked successfully");
                            finish();


                            // Handle any further actions after successful appointment booking
                        } else {
                            // Appointment booking failed or other error
                            String message = jsonObject.has("message") ? jsonObject.getString("message") : "Failed to book appointment";
                            Toast.makeText(AppointmentActivity.this, message, Toast.LENGTH_SHORT).show();
                            Log.e("AppointmentResponse", message);

                            // Handle any further actions after failed appointment booking
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        // Error handling
                        Toast.makeText(AppointmentActivity.this, "Failed to process response", Toast.LENGTH_SHORT).show();
                        Log.e("AppointmentResponse", "Failed to process response", e);
                    }
                } else {
                    // Handle HTTP error
                    Toast.makeText(AppointmentActivity.this, "Failed to make appointment request", Toast.LENGTH_SHORT).show();
                    Log.e("AppointmentResponse", "Failed to make appointment request");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show the form again
                appointmentformCardView.setVisibility(View.VISIBLE);
                // Handle network failure
                Toast.makeText(AppointmentActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("AppointmentResponse", "Network error", t);
                // Handle any further actions after network failure
            }
        });
    }

    private void clearFormData() {
        // Clear selected transaction types
        checkBoxDeposit.setChecked(false);
        checkBoxWithdraw.setChecked(false);
        checkBoxPassbook.setChecked(false);
        checkBoxPrinting.setChecked(false);
        checkBoxIPO.setChecked(false);
        checkBoxLoan.setChecked(false);
        checkBoxShare.setChecked(false);
        checkBoxAtm.setChecked(false);
        checkBoxBill.setChecked(false);

        // Reset date and time buttons
        dateButton.setText("Select Date");
        timeButton.setText("Select Time");

        // Reset bank spinner
        bankSpinner.setSelection(0);
    }

    private boolean isValidTime(int hourOfDay, int minute) {
        // Check if the time falls within the allowed range (10:00 AM to 4:00 PM)
        return (hourOfDay >= 10 && hourOfDay <= 16);
    }
}
