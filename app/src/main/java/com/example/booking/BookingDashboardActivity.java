package com.example.booking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class BookingDashboardActivity extends AppCompatActivity {

    // Define your views
    private CardView cardProfileOptionsLogout;
    private boolean isProfileOptionsLogoutVisible = false;
    private CardView cardProfileOptions;
    private CardView cardLogout;
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_dashboard);

        // Initialize your views
        cardProfileOptionsLogout = findViewById(R.id.cardProfileOptionsLogout);
        cardProfileOptions = findViewById(R.id.cardProfileOptions);
        cardLogout = findViewById(R.id.cardLogout);

        // Set OnClickListener for the profile image
        ImageView profileImage = findViewById(R.id.iconImageView);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of the profile options and logout cards
                toggleProfileOptionsVisibility();
            }
        });
//        ImageView arrowIcon = findViewById(R.id.arrowIcon);
//        arrowIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Hide cardProfileOptionsLogout
//                hideProfileOptions();
//            }
//        });

        // Set click listeners for cardProfileOptions and cardLogout
        cardProfileOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfileActivity();
            }
        });

        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        // Initialize other views and set up listeners
        initializeViews();

        // Set OnClickListener for other cards
        setCardClickListeners();

        // Set touch listener to hide cardProfileOptionsLogout when touched outside
        findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && isProfileOptionsLogoutVisible) {
                    float x = event.getX();
                    float y = event.getY();
                    if (!isInsideView(cardProfileOptionsLogout, x, y)) {
                        hideProfileOptions();
                    }
                }
                return false;
            }
        });
    }

    private void toggleProfileOptionsVisibility() {
        if (isProfileOptionsLogoutVisible) {
            hideProfileOptions();
        } else {
            showProfileOptions();
        }
    }

    private void showProfileOptions() {
        cardProfileOptionsLogout.setVisibility(View.VISIBLE);
        isProfileOptionsLogoutVisible = true;
    }

    private void hideProfileOptions() {
        cardProfileOptionsLogout.setVisibility(View.GONE);
        isProfileOptionsLogoutVisible = false;
    }

    private boolean isInsideView(View view, float x, float y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        return (x > viewX && x < (viewX + view.getWidth()) && y > viewY && y < (viewY + view.getHeight()));
    }

    private void initializeViews() {
        TextView userIdTextView = findViewById(R.id.userIdTextView);
        TextView helloUsernameTextView = findViewById(R.id.helloUsernameTextView);

        // Get user_id and username from Intent
        String userId = getIntent().getStringExtra("user_id");
        String username = getIntent().getStringExtra("username");

        // Display user_id and username
        userIdTextView.setText("User ID: " + userId);
        helloUsernameTextView.setText("Hello, " + capitalizeEachWord(username));

        // Set up the image slider
        ViewPager2 imageSlider = findViewById(R.id.imageSlider);
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, getImageList());
        imageSlider.setAdapter(sliderAdapter);

        // Add automatic scrolling
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                int currentItem = imageSlider.getCurrentItem();
                if (currentItem < sliderAdapter.getItemCount() - 1) {
                    imageSlider.setCurrentItem(currentItem + 1);
                } else {
                    imageSlider.setCurrentItem(0);
                }
            }
        };

        // Set the time interval for automatic scrolling
        int delay = 3000; // 3 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                update.run();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void setCardClickListeners() {
        // Initialize your CardView elements
        CardView cardAppointments = findViewById(R.id.cardAppointments);
        CardView cardCancel = findViewById(R.id.cardCancel);
        CardView cardHistory = findViewById(R.id.cardHistory);
        CardView cardStatus = findViewById(R.id.cardStatus);
        CardView cardCheck = findViewById(R.id.cardCheck);
        CardView cardHelp = findViewById(R.id.cardHelp);

        // Set OnClickListener for each card
        cardAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(AppointmentActivity.class);
            }
        });
        cardCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(CancelActivity.class);
            }
        });
        cardHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(HistoryActivity.class);
            }
        });
        cardStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(StatusActivity.class);
            }
        });
        cardCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(CheckActivity.class);
            }
        });
        cardHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToActivity(HelpActivity.class);
            }
        });
    }

    private void navigateToProfileActivity() {
        hideProfileOptions();
        // Navigate to ProfileActivity
        Intent intent = new Intent(BookingDashboardActivity.this, ProfileActivity.class);
        // Pass user_id and username to ProfileActivity
        intent.putExtra("user_id", getIntent().getStringExtra("user_id"));
        intent.putExtra("username", getIntent().getStringExtra("username"));
        startActivity(intent);
    }

    private void navigateToActivity(Class<?> cls) {
        // Navigate to the specified activity with user_id and username
        Intent intent = new Intent(BookingDashboardActivity.this, cls);
        intent.putExtra("user_id", getIntent().getStringExtra("user_id"));
        intent.putExtra("username", getIntent().getStringExtra("username"));
        startActivity(intent);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performLogout();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void performLogout() {
        // Clear user session data from SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        // Redirect to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish current activity (BookingDashboardActivity)

        // Log the logout action
        String logMessage = "User logged out successfully";
        Log.d("Logout", logMessage);
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

    private List<Integer> getImageList() {
        List<Integer> images = new ArrayList<>();
        // Add your image resources to the list
        images.add(R.drawable.image1);
        images.add(R.drawable.image2);
        images.add(R.drawable.image3);
        images.add(R.drawable.image4);
        images.add(R.drawable.image5);
        images.add(R.drawable.image6);
        images.add(R.drawable.image7);
        images.add(R.drawable.image8);
        images.add(R.drawable.image9);
        images.add(R.drawable.image10);

        return images;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        // Finish the activity when "Yes" is clicked
                        finish();
                    }
                }).create().show();
    }
}
