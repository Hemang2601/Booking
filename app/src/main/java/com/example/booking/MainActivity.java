package com.example.booking;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout;
    private EditText emailEditText;
    private TextInputLayout passwordInputLayout;
    private EditText passwordEditText;
    private TextView forgotPasswordTextView;
    private Button loginButton;
    private TextView registerTextView;
    private CardView cardView;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_USER_LOGGED_IN = "userLoggedIn";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        emailInputLayout = findViewById(R.id.emailInputLayout);
        emailEditText = findViewById(R.id.emailEditText);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        passwordEditText = findViewById(R.id.passwordEditText);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
        cardView = findViewById(R.id.cardView);

        // Check if the user is already logged in
        if (isUserLoggedIn()) {
            redirectToDashboard();
        }

        // Set onClickListener for loginButton
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve data from EditText fields
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Check if email and password are not empty
                if (!email.isEmpty() && !password.isEmpty()) {
                    // Call the login API using Retrofit
                    loginUser(email, password);
                } else {
                    // Show a Toast indicating that email and password are required
                    Toast.makeText(MainActivity.this, "Email and password are required", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Set onClickListener for registerTextView
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the register activity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();  // Optional: Close the current activity
            }
        });
    }


    private void setLoggedInStatus(boolean isLoggedIn, String userId, String username) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(KEY_USER_LOGGED_IN, isLoggedIn);

        if (isLoggedIn) {
            editor.putString(KEY_USER_ID, userId);
            editor.putString(KEY_USERNAME, username);
        }

        editor.apply();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_USER_LOGGED_IN, false);
    }

    private void redirectToDashboard() {
        Intent intent = new Intent(MainActivity.this, BookingDashboardActivity.class);

        // Retrieve user_id and username from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, "");
        String username = prefs.getString(KEY_USERNAME, "");

        // Pass user_id and username to the BookingDashboardActivity
        intent.putExtra("user_id", userId);
        intent.putExtra("username", username);

        startActivity(intent);
        finish();  // Optional: Close the current activity
    }

    private void loginUser(String email, String password) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<LoginResponse> call = apiService.loginUser(new LoginRequest(email, password));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                handleLoginResponse(response);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Handle failure, such as network issues
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }


        });

    }

    private void handleLoginResponse(Response<LoginResponse> response) {
        if (response.isSuccessful()) {
            LoginResponse loginResponse = response.body();
            if (loginResponse != null && loginResponse.isSuccess()) {
                // Log the entire response
                Log.d("LoginResponse", "Response: " + new Gson().toJson(loginResponse));

                // Handle successful login response
                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                // Set login status to true
                setLoggedInStatus(true, String.valueOf(loginResponse.getUserId()), loginResponse.getUsername());

                // Redirect to the BookingDashboardActivity with user_id and username
                redirectToDashboard();
            } else {
                // Handle unsuccessful login response
                if (loginResponse != null) {
                    String message = loginResponse.getMessage();
                    if (message != null && !message.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Login Failed: " + message, Toast.LENGTH_SHORT).show();
                    } else {
                        // Default error message if message is null or empty
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Default error message if loginResponse is null
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Log the HTTP status code for non-successful responses
            Log.e("LoginResponse", "Unsuccessful Response Code: " + response.code());

            // Handle other unsuccessful responses
            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

}
