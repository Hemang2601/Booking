package com.example.booking;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.resetButton);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        verifyButton = findViewById(R.id.verifyButton);
        newPasswordCardView=findViewById(R.id.newPasswordCardView);
        otpCardView=findViewById(R.id.otpCardView);
        cardView=findViewById(R.id.cardView);

        newPasswordCardView.setVisibility(View.GONE);
        otpCardView.setVisibility(View.GONE);


        // Set onClickListener for resetButton
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve email entered by the user
                String email = emailEditText.getText().toString().trim();

                // Check if email is not empty
                if (!email.isEmpty()) {
                    // Call the method to handle password reset
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

                    // Call the method to handle OTP verification
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
                    // Call the method to handle password change
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
            final int nextIndex = (i + 1) % otpEditTexts.length;

            otpEditTexts[currentIndex].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && currentIndex != 5) { // Check if not the last position
                        otpEditTexts[nextIndex].requestFocus();
                    }
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


                        // Hide newPasswordCardView
                        cardView.setVisibility(View.GONE);

                        // Show otpCardView
                        otpCardView.setVisibility(View.VISIBLE);

                        // Handle successful response
                        Toast.makeText(ForgetPasswordActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(ForgetPasswordActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
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
                            cardView.setVisibility(View.GONE);

                            // Show otpCardView
                            otpCardView.setVisibility(View.VISIBLE);

                            // Handle successful response
                            Toast.makeText(ForgetPasswordActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            // Check if the message indicates email is not registered
                            if (message.equals("Email is not registered")) {
                                // Display a Toast message indicating that email is not registered
                                Toast.makeText(ForgetPasswordActivity.this, "Email is not registered", Toast.LENGTH_SHORT).show();
                            } else {
                                // Display the message from the response
                                Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(ForgetPasswordActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
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

                        // Log the response body
                        Log.d("ChangePassword", "Response: " + responseBody);

                        // Handle successful response
                        Log.d("ChangePassword", "Password changed successfully");
                        Toast.makeText(ForgetPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response
                    Log.d("ChangePassword", "Failed to change password: " + response.message());
                    Toast.makeText(ForgetPasswordActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Log.e("ChangePassword", "Network error", t);
                Toast.makeText(ForgetPasswordActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

}
