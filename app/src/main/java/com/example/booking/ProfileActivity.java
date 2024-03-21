package com.example.booking;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private TextView userIdTextView;
    private TextView helloUsernameTextView;
    private TextView nameTextView, emailTextView, phoneTextView, addressTextView,newPasswordLabelTextView;
    private EditText newPasswordEditText;
    private Button changePasswordButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d(TAG, "onCreate: ProfileActivity created");

        userIdTextView = findViewById(R.id.userIdTextView);
        helloUsernameTextView = findViewById(R.id.helloUsernameTextView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        addressTextView = findViewById(R.id.addressTextView);
        newPasswordLabelTextView = findViewById(R.id.newPasswordLabelTextView);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        progressBar = findViewById(R.id.progressBar);

        // Retrieve user_id and username from Intent extras
        String userId = getIntent().getStringExtra("user_id");
        String username = getIntent().getStringExtra("username");

        // Display user_id and username in TextViews
        userIdTextView.setText("User ID: " + userId);
        helloUsernameTextView.setText(capitalizeEachWord(username));

        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Username: " + username);

        // Fetch user data with delay
        fetchUserDataWithDelay(userId);

        // Change Password Button Click Listener
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user_id from Intent extras
                String userId = getIntent().getStringExtra("user_id");

                // Get the new password from the EditText
                String newPassword = newPasswordEditText.getText().toString().trim();

                // Check if the new password is not empty
                if (!TextUtils.isEmpty(newPassword)) {
                    // Call the changePassword function
                    changePassword(userId, newPassword);
                } else {
                    // Show a message to the user indicating that the new password is empty
                    Toast.makeText(ProfileActivity.this, "Please enter a new password", Toast.LENGTH_SHORT).show();
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

    private void fetchUserDataWithDelay(String userId) {
        // Show loader after a delay of 2 seconds
        progressBar.setVisibility(View.VISIBLE);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        fetchUserData(userId);
                    }
                }, 1000);
    }

    private void fetchUserData(String userId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        UserIdRequest userIdRequest = new UserIdRequest(userId);

        Call<ResponseBody> call = apiService.fetchUserData(userIdRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE); // Hide the progress bar
                if (response.isSuccessful()) {
                    try {
                        String userDataJson = response.body().string();
                        // Log the raw JSON response
                        Log.d("UserData", "Raw JSON: " + userDataJson);

                        // Parse the JSON response
                        JSONObject jsonObject = new JSONObject(userDataJson);

                        // Check if the "user_id" key exists in the JSON response
                        if (jsonObject.has("user_id")) {
                            int userId = jsonObject.getInt("user_id");
                            String username = jsonObject.getString("username");
                            String email = jsonObject.getString("email");
                            String phoneNumber = jsonObject.getString("phone_number");
                            String address = jsonObject.getString("address");

                            // Update TextViews with user data
                            nameTextView.setText(username);
                            emailTextView.setText(email);
                            phoneTextView.setText(phoneNumber);
                            addressTextView.setText(address);

                            // Now you have the data, you can use it as needed
                            Log.d("UserData", "User ID: " + userId);
                            Log.d("UserData", "Username: " + username);
                            Log.d("UserData", "Email: " + email);
                            Log.d("UserData", "Phone Number: " + phoneNumber);
                            Log.d("UserData", "Address: " + address);
                            findViewById(R.id.profileCardView).setVisibility(View.VISIBLE);
                        } else {
                            // Handle the case where "user_id" key is missing in the JSON response
                            Log.e("UserData", "No user_id found in JSON response");
                            // Show an error message or handle the situation accordingly
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        // Handle error parsing response body
                    }
                } else {
                    // Handle error response
                    Toast.makeText(ProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure, such as network issues
                progressBar.setVisibility(View.GONE); // Hide the progress bar
                Toast.makeText(ProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword(String userId, String newPassword) {
        // Log the user ID and password
        Log.d(TAG, "Changing password for user ID: " + userId + ", New password: " + newPassword);

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Create an instance of ApiService
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Create a request object with user id and new password
        UserIdPasswordRequest request = new UserIdPasswordRequest(userId, newPassword);

        // Make the API call to change password
        Call<ResponseBody> call = apiService.changePassword(request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    try {
                        // Password updated successfully
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        Log.d(TAG, "Success: " + success + ", Message: " + message);

                        Toast.makeText(ProfileActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();

//                         Make the change password field invisible again
                         newPasswordLabelTextView.setVisibility(View.GONE);
                         newPasswordEditText.setVisibility(View.GONE);
                         changePasswordButton.setVisibility(View.GONE);
                        // Hide the keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getRootView().getWindowToken(), 0);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        // Failed to update password
                        String errorMessage = response.errorBody().string();
                        Log.e(TAG, "Failed to update password: " + errorMessage);
                        Toast.makeText(ProfileActivity.this, "Failed to change password: " + errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Handle failure, such as network issues
                Toast.makeText(ProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error", t);
            }
        });
    }

}
