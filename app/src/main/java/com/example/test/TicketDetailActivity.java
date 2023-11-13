package com.example.test;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TicketDetailActivity extends AppCompatActivity {
    // Dodaj pola dla widoków, które będą wyświetlane
    private TextView textViewEmail, textViewDescription, textViewStation, textViewStatus;
    private ImageView imageViewTicket;
    private Button buttonChangeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail); // Musisz stworzyć odpowiedni layout

        // Inicjalizacja widokówS
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewStation = findViewById(R.id.textViewStation);
        textViewStatus = findViewById(R.id.textViewStatus);
        imageViewTicket = findViewById(R.id.imageViewTicket);
        buttonChangeStatus = findViewById(R.id.buttonChangeStatus);

        // Odbierz ID zgłoszenia przekazane z MainActivity
        String ticketId = getIntent().getStringExtra("ticketId");
        if (ticketId != null) {
            loadTicketDetails(ticketId);
        } else {
            Toast.makeText(this, "Ticket ID is missing", Toast.LENGTH_SHORT).show();
            finish(); // Zakończ aktywność, jeśli ID zgłoszenia nie zostało przekazane
        }
    }

    private void loadTicketDetails(String ticketId) {
        DatabaseReference ticketRef = FirebaseDatabase.getInstance().getReference("tickets").child(ticketId);
        ticketRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Ticket ticket = snapshot.getValue(Ticket.class);
                if (ticket != null) {
                    // Ustawianie danych zgłoszenia
                    textViewDescription.setText(ticket.getDescription());
                    textViewStation.setText(ticket.getStation());
                    textViewStatus.setText(ticket.getStatus());
                    // Załaduj obrazek z URL używając Glide
                    Glide.with(TicketDetailActivity.this).load(ticket.getImageUri()).into(imageViewTicket);

                    // Teraz pobierz dane użytkownika, który dodał zgłoszenie
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(ticket.getUserId());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                textViewEmail.setText(user.getEmail());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(TicketDetailActivity.this, "Error loading user", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(TicketDetailActivity.this, "Ticket details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TicketDetailActivity.this, "Error loading ticket", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

