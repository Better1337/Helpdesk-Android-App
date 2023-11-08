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

        // Zainicjuj FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Zainicjuj referencje do widoków
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Dodaj słuchacza dla przycisku logowania
        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            signIn(email, password);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Sprawdzenie, czy użytkownik jest zalogowany
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.isAdmin()) {
                        // Otwórz AdminActivity
                        startActivity(new Intent(MainActivity.this, AdminActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Brak uprawnień administratora", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Wystąpił błąd: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(MainActivity.this, UserActivity.class);
                        intent.putExtra("email", user.getEmail());
                        startActivity(intent);
                        finish();  // Opcjonalnie: zakończ MainActivity, aby użytkownik nie mógł do niej wrócić, naciskając przycisk Wstecz
                    } else {
                        Toast.makeText(MainActivity.this, "Logowanie nieudane", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}