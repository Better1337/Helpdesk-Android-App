package com.example.test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserTicketsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TicketAdapter adapter;
    private List<Ticket> ticketList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tickets);

        recyclerView = findViewById(R.id.recyclerViewUserTickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketAdapter(this, ticketList);
        recyclerView.setAdapter(adapter);

        fetchUserTickets();
    }

    private void fetchUserTickets() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("tickets");
        databaseRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ticketList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ticket ticket = snapshot.getValue(Ticket.class);
                    if(ticket != null) {
                        ticket.setTicketId(snapshot.getKey());
                        ticketList.add(ticket);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserTicketsActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
