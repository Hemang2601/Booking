package com.example.booking;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AppointmentActivity extends AppCompatActivity {

    private Spinner bankSpinner;
    private TextView userIdTextView;
    private TextView helloUsernameTextView;
    private Button dateButton;
    private Button timeButton;
    private Button appointmentButton;

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
        userIdTextView = findViewById(R.id.userIdTextView);
        helloUsernameTextView = findViewById(R.id.helloUsernameTextView);
        bankSpinner = findViewById(R.id.bankSpinner);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
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
                    selectedTransactionTypes.append("Deposit, ");
                }
                if (checkBoxWithdraw.isChecked()) {
                    selectedTransactionTypes.append("Withdraw, ");
                }
                if (checkBoxPassbook.isChecked()) {
                    selectedTransactionTypes.append("Passbook, ");
                }
                if (checkBoxPrinting.isChecked()) {
                    selectedTransactionTypes.append("Printing, ");
                }
                if (checkBoxIPO.isChecked()) {
                    selectedTransactionTypes.append("IPO, ");
                }
                if (checkBoxLoan.isChecked()) {
                    selectedTransactionTypes.append("Loan, ");
                }
                if (checkBoxShare.isChecked()) {
                    selectedTransactionTypes.append("Share, ");
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

                // Check if the user has selected a valid bank
                if (selectedBank.equals("Select Bank")) {
                    // Show a toast message asking the user to select a bank
                    Toast.makeText(AppointmentActivity.this, "Please select a bank", Toast.LENGTH_SHORT).show();
                } else {
                    // Implement appointment booking logic here
                    Toast.makeText(AppointmentActivity.this, "Appointment Booked", Toast.LENGTH_SHORT).show();

                    // Log the selected bank
                    Log.d("Selected Bank", selectedBank);

                    // Print selected values in log
                    Log.d("AppointmentDetails", "Selected Transaction Types: " + transactionTypes);
                    Log.d("AppointmentDetails", "Selected Date: " + selectedDate);
                    Log.d("AppointmentDetails", "Selected Time: " + selectedTime);
                    Log.d("AppointmentDetails", "Selected Bank: " + selectedBank);
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

    private boolean isValidTime(int hourOfDay, int minute) {
        // Check if the time falls within the allowed range (10:00 AM to 4:00 PM)
        return (hourOfDay >= 10 && hourOfDay <= 16);
    }
}
