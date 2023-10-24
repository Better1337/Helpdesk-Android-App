package com.example.test;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);

        String email = getIntent().getStringExtra("email");
        welcomeTextView.setText("Witam " + email);
    }
}
