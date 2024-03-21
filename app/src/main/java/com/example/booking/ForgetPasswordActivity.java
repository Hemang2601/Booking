package com.example.booking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetButton;
    private EditText[] otpEditTexts;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button changePasswordButton,verifyButton;
    private CardView newPasswordCardView,otpCardView,cardView;
    private ProgressBar progressBar;
    private TextView backToLoginTextView;
    private TextView progressMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        backToLoginTextView = findViewById(R.id.backToLoginTextView);
        resetButton = findViewById(R.id.resetButton);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        verifyButton = findViewById(R.id.verifyButton);
        newPasswordCardView=findViewById(R.id.newPasswordCardView);
        otpCardView=findViewById(R.id.otpCardView);
        cardView=findViewById(R.id.cardView);
        progressBar = findViewById(R.id.progressBar);
        progressMessageTextView = findViewById(R.id.progressMessageTextView);

        newPasswordCardView.setVisibility(View.GONE);
        otpCardView.setVisibility(View.GONE);

        // Set click listener for "Back to login" TextView
        backToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click, navigate back to login activity
                Intent intent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: finish current activity to prevent coming back to it with back button
            }
        });


        // Set onClickListener for resetButton
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve email entered by the user
                String email = emailEditText.getText().toString().trim();

                // Check if email is not empty
                if (!email.isEmpty()) {
                    // Close the keyboard
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    // Call the method to handle password reset
                    cardView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressMessageTextView.setVisibility(View.VISIBLE);
                    handlePasswordReset(email);
                } else {
                    // Show a Toast indicating that email is required
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if any of the OTP EditText fields is empty
                boolean otpFieldsEmpty = false;
                for (EditText editText : otpEditTexts) {
                    if (editText.getText().toString().trim().isEmpty()) {
                        otpFieldsEmpty = true;
                        break; // Exit the loop if any field is empty
                    }
                }

                // If any OTP field is empty, show a toast message
                if (otpFieldsEmpty) {
                    Toast.makeText(ForgetPasswordActivity.this, "Please fill all OTP fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Concatenate the values from the OTP EditText fields
                    StringBuilder otpBuilder = new StringBuilder();
                    for (EditText editText : otpEditTexts) {
                        otpBuilder.append(editText.getText().toString().trim());
                    }
                    String otp = otpBuilder.toString();

                    // Retrieve email entered by the user
                    String email = emailEditText.getText().toString().trim();

                    // Close the keyboard
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // Call the method to handle OTP verification
                    otpCardView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressMessageTextView.setVisibility(View.VISIBLE);
                    handleOtpVerification(email, otp);
                }
            }
        });

        // Set onClickListener for changePasswordButton
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve new password and confirm password entered by the user
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Check if passwords match
                if (newPassword.equals(confirmPassword)) {
                    // Close the keyboard
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // Call the method to handle password change
                    newPasswordCardView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressMessageTextView.setVisibility(View.VISIBLE);
                    handleChangePassword(newPassword);
                } else {
                    // Show a Toast indicating that passwords do not match
                    Toast.makeText(ForgetPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize OTP EditText fields
        otpEditTexts = new EditText[]{
                findViewById(R.id.otpEditText1),
                findViewById(R.id.otpEditText2),
                findViewById(R.id.otpEditText3),
                findViewById(R.id.otpEditText4),
                findViewById(R.id.otpEditText5),
                findViewById(R.id.otpEditText6)
        };
        setOtpEditTextListeners();
    }

    private void setOtpEditTextListeners() {
        for (int i = 0; i < otpEditTexts.length; i++) {
            final int currentIndex = i;
            final int previousIndex = (i == 0) ? otpEditTexts.length - 1 : i - 1;
            final int nextIndex = (i + 1) % otpEditTexts.length;

            otpEditTexts[currentIndex].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) { // Check if a digit is entered
                        // Automatically move to the next box if not the last position
                        if (currentIndex != otpEditTexts.length - 1) {
                            otpEditTexts[nextIndex].requestFocus();
                        }
                    } else if (s.length() == 0) { // Check if the digit is deleted
                        // Automatically move to the previous box if not the first position
                        if (currentIndex != 0) {
                            otpEditTexts[previousIndex].requestFocus();
                        }
                    }
                }
            });

            // Handle the case where the user wants to delete the current digit by pressing backspace
            otpEditTexts[currentIndex].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        // Automatically move to the previous box if not the first position
                        if (currentIndex != 0) {
                            otpEditTexts[previousIndex].requestFocus();
                        }
                        // Clear the current box when deleting using backspace
                        otpEditTexts[currentIndex].setText("");
                        return true; // Consume the event
                    }
                    return false; // Let the system handle other key events
                }
            });
        }
    }



    private void handlePasswordReset(String email) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.sendPasswordResetEmail(email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Extract the response message
                        String responseBody = response.body().string();
                        Log.d("PasswordResetResponse", "Response: " + responseBody);

                        // Parse the JSON response
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");

                        if (success) {
                            // Hide newPasswordCardView
                            cardView.setVisibility(View.GONE);

                            // Show otpCardView
                            progressBar.setVisibility(View.GONE);
                            progressMessageTextView.setVisibility(View.GONE);
                            otpCardView.setVisibility(View.VISIBLE);

                            // Handle successful response
                            Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            // Display the message from the response
                            progressBar.setVisibility(View.GONE);
                            progressMessageTextView.setVisibility(View.GONE);
                            cardView.setVisibility(View.VISIBLE);
                            Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response
                    progressBar.setVisibility(View.GONE);
                    progressMessageTextView.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                    Toast.makeText(ForgetPasswordActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                cardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                progressMessageTextView.setVisibility(View.GONE);
                Toast.makeText(ForgetPasswordActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }


    private void handleOtpVerification(String email, String otp) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<ResponseBody> call = apiService.verifyOtpForPasswordReset(email, otp);

        Log.d("OtpVerification", "Verifying OTP: " + otp + " for email: " + email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Extract the response message
                        String responseBody = response.body().string();
                        Log.d("PasswordResetResponse", "Response: " + responseBody);

                        // Parse the JSON response
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");

                        if (success) {
                            // Hide newPasswordCardView
                            otpCardView.setVisibility(View.GONE);

                            // Show otpCardView
                            progressBar.setVisibility(View.GONE);
                            progressMessageTextView.setVisibility(View.GONE);
                            newPasswordCardView.setVisibility(View.VISIBLE);

                            // Handle successful response
                            Toast.makeText(ForgetPasswordActivity.this, "OTP verification successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // Check if the message indicates email is not registered
                            if (message.equals("Email is not registered")) {

                                Toast.makeText(ForgetPasswordActivity.this, "Email is not registered", Toast.LENGTH_SHORT).show();
                            } else {
                                // Display the message from the response
                                progressBar.setVisibility(View.GONE);
                                progressMessageTextView.setVisibility(View.GONE);
                                otpCardView.setVisibility(View.VISIBLE);
                                Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        otpCardView.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Handle unsuccessful response
                    progressBar.setVisibility(View.GONE);
                    progressMessageTextView.setVisibility(View.GONE);
                    otpCardView.setVisibility(View.VISIBLE);
                    Toast.makeText(ForgetPasswordActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                progressBar.setVisibility(View.GONE);
                progressMessageTextView.setVisibility(View.GONE);
                otpCardView.setVisibility(View.VISIBLE);
                Log.e("OtpVerification", "Network error", t);
                Toast.makeText(ForgetPasswordActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void handleChangePassword(String newPassword) {
        String email = emailEditText.getText().toString().trim(); // Assuming you have an emailEditText field

        // Create an instance of the ApiService interface
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Call the API method to change the password
        Call<ResponseBody> call = apiService.changePassword(email, newPassword);

        // Log the email and new password being sent
        Log.d("ChangePassword", "Changing password for email: " + email + ", New password: " + newPassword);

        // Enqueue the API call
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Extract the response body as a string
                        String responseBody = response.body().string();
                        Log.d("ChangePassword", "Response: " + responseBody);


                        Log.d("ChangePassword", "Password changed successfully");
                        progressBar.setVisibility(View.GONE);
                        progressMessageTextView.setVisibility(View.GONE);
                        Toast.makeText(ForgetPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // This ensures the user cannot navigate back to this activity from MainActivity
                    } catch (IOException e) {
                        e.printStackTrace();
                        newPasswordCardView.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Handle unsuccessful response
                    Log.d("ChangePassword", "Failed to change password: " + response.message());
                    newPasswordCardView.setVisibility(View.VISIBLE);
                    Toast.makeText(ForgetPasswordActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Log.e("ChangePassword", "Network error", t);
                newPasswordCardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                progressMessageTextView.setVisibility(View.GONE);
                Toast.makeText(ForgetPasswordActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (newPasswordCardView.getVisibility() == View.VISIBLE) {
            // If new password card is visible, show an alert or confirmation dialog
            // You can customize this part to show an appropriate dialog
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to cancel password reset?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If user confirms, navigate back to the main activity
                            Intent intent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // This ensures the user cannot navigate back to this activity from MainActivity
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else if (otpCardView.getVisibility() == View.VISIBLE) {
            // If OTP card is visible, show an alert or confirmation dialog
            // You can customize this part to show an appropriate dialog
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to cancel OTP verification?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If user confirms, navigate back to the main activity
                            Intent intent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // This ensures the user cannot navigate back to this activity from MainActivity
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            // For other cases, let the system handle the back button press
            super.onBackPressed();
        }
    }


}
