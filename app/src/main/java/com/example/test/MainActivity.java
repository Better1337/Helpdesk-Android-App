package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Nullable;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize references to views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Add click listener to login button
        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            signIn(email, password);
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user); // Pass the user to checkUserRole
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(FirebaseUser firebaseUser) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Intent intent;
                    if (user.isAdmin()) {
                        intent = new Intent(MainActivity.this, AdminActivity.class);
                    } else {
                        intent = new Intent(MainActivity.this, UserActivity.class);
                    }
                    intent.putExtra("email", firebaseUser.getEmail()); // Pass email to the next activity
                    startActivity(intent);
                    finish(); // Finish MainActivity
                } else {
                    // If user is not found, show a message and sign out
                    Toast.makeText(MainActivity.this, "User data not found.",
                            Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    // Restart this activity or go back to login screen
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting user data failed, log a message
                Toast.makeText(MainActivity.this, "Failed to read user data.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
