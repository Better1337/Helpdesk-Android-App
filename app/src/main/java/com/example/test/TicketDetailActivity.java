package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TicketDetailActivity extends AppCompatActivity {
    private TextView textViewEmail, textViewDescription, textViewStation, textViewStatus;
    private ImageView imageViewTicket;
    private Button buttonChangeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewStation = findViewById(R.id.textViewStation);
        textViewStatus = findViewById(R.id.textViewStatus);
        imageViewTicket = findViewById(R.id.imageViewTicket);
        buttonChangeStatus = findViewById(R.id.buttonChangeStatus);
        buttonChangeStatus.setVisibility(View.GONE); // Ukryj przycisk zmiany statusu na początek

        String ticketId = getIntent().getStringExtra("ticketId");
        if (ticketId != null) {
            loadTicketDetails(ticketId);
            determineUserAccess(ticketId);
        } else {
            Toast.makeText(this, "Ticket ID is missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadTicketDetails(String ticketId) {
        DatabaseReference ticketRef = FirebaseDatabase.getInstance().getReference("tickets").child(ticketId);
        ticketRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Ticket ticket = snapshot.getValue(Ticket.class);
                if (ticket != null) {
                    textViewDescription.setText(ticket.getDescription());
                    textViewStation.setText(ticket.getStation());
                    textViewStatus.setText(ticket.getStatus());
                    Glide.with(TicketDetailActivity.this).load(ticket.getImageUri()).into(imageViewTicket);

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(ticket.getUserId());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null && user.getEmail() != null) {
                                textViewEmail.setText(user.getEmail());
                            } else {
                                textViewEmail.setText("Brak danych e-mail");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(TicketDetailActivity.this, "Błąd ładowania e-maila użytkownika", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(TicketDetailActivity.this, "Szczegóły zgłoszenia nie zostały znalezione", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TicketDetailActivity.this, "Błąd ładowania zgłoszenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void determineUserAccess(String ticketId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null && currentUser.isAdmin()) {
                    buttonChangeStatus.setVisibility(View.VISIBLE);
                    setupChangeStatusButton(ticketId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TicketDetailActivity.this, "Nie udało się zweryfikować roli użytkownika.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChangeStatusButton(String ticketId) {
        buttonChangeStatus.setOnClickListener(v -> {
            final String currentStatus = textViewStatus.getText().toString();
            final String newStatus = currentStatus.equals("otwarty") ? "zamknięty" : "otwarty";

            DatabaseReference ticketRef = FirebaseDatabase.getInstance().getReference("tickets").child(ticketId);
            ticketRef.child("status").setValue(newStatus)
                    .addOnSuccessListener(aVoid -> {
                        textViewStatus.setText(newStatus);
                        Toast.makeText(TicketDetailActivity.this, "Status zmieniony na: " + newStatus, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(TicketDetailActivity.this, "Błąd podczas zmiany statusu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
