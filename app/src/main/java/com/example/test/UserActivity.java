package com.example.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imagePreview;
    private Button buttonMyTickets; // Dodajemy przycisk "Moje zgłoszenia"

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        imagePreview = findViewById(R.id.imagePreview);
        buttonMyTickets = findViewById(R.id.buttonMyTickets); // Znajdujemy przycisk w layout

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        Spinner stationSpinner = findViewById(R.id.stationSpinner);
        Button pickImageButton = findViewById(R.id.pickImageButton);
        Button submitReportButton = findViewById(R.id.submitReportButton);

        String email = getIntent().getStringExtra("email");
        welcomeTextView.setText("Witam " + email);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.station_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stationSpinner.setAdapter(adapter);

        pickImageButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        submitReportButton.setOnClickListener(view -> {
            String description = descriptionEditText.getText().toString();
            String station = stationSpinner.getSelectedItem().toString();
            submitReport(description, station, imageUri);
        });

        // Ustawienie listenera dla przycisku "Moje zgłoszenia"
        buttonMyTickets.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, UserTicketsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void submitReport(String description, String station, Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images");
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("tickets");
        String reportId = databaseRef.push().getKey();

        if (imageUri != null) {
            StorageReference imageRef = storageRef.child(reportId + "_" + imageUri.getLastPathSegment());
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(downloadUri ->
                            saveTicketWithImageUri(description, station, reportId, downloadUri.toString())
                    )
            ).addOnFailureListener(e ->
                    Toast.makeText(UserActivity.this, "Wystąpił błąd podczas przesyłania zdjęcia: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        } else {
            saveTicketWithImageUri(description, station, reportId, null);
        }
    }

    private void saveTicketWithImageUri(String description, String station, String reportId, String imageUrl) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("tickets");

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("description", description);
        ticket.put("station", station);
        ticket.put("imageUri", imageUrl != null ? imageUrl : "");
        ticket.put("status", "otwarty");
        ticket.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseRef.child(reportId).setValue(ticket)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(UserActivity.this, "Zgłoszenie zostało przesłane", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(UserActivity.this, "Wystąpił błąd przy zapisie zgłoszenia: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
