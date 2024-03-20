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

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetButton;
    private EditText[] otpEditTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.resetButton);

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
}
