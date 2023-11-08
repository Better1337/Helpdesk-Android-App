package com.example.test;// Import necessary classes
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
import com.example.test.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class WelcomeActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imagePreview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        imagePreview = findViewById(R.id.imagePreview);

        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        Spinner stationSpinner = findViewById(R.id.stationSpinner);
        Button pickImageButton = findViewById(R.id.pickImageButton);
        Button submitReportButton = findViewById(R.id.submitReportButton);

        String email = getIntent().getStringExtra("email");
        welcomeTextView.setText("Witam " + email);

        // Set up the spinner with station numbers
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.station_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stationSpinner.setAdapter(adapter);

        // Set up the image picker button
        pickImageButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        // Set up the submit button
        submitReportButton.setOnClickListener(view -> {
            String description = descriptionEditText.getText().toString();
            String station = stationSpinner.getSelectedItem().toString();
            submitReport(description, station, imageUri);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);  // Ustaw obraz w ImageView
            imagePreview.setVisibility(View.VISIBLE);  // Pokaż ImageView
        }
    }



    private void submitReport(String description, String station, Uri imageUri) {
        // Uzyskaj referencję do miejsca, gdzie będą przechowywane zdjęcia w Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images");

        // Generuj unikalny identyfikator dla zgłoszenia
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("tickets");
        String reportId = databaseRef.push().getKey();

        if (imageUri != null) {
            // Przesyłaj zdjęcie, jeśli istnieje
            StorageReference imageRef = storageRef.child(reportId + "_" + imageUri.getLastPathSegment());
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Po pomyślnym przesłaniu zdjęcia, zapisz zgłoszenie z URL zdjęcia
                        saveTicketWithImageUri(description, station, reportId, downloadUri.toString());
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(WelcomeActivity.this, "Wystąpił błąd podczas przesyłania zdjęcia: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        } else {
            // Jeśli nie ma zdjęcia, zapisz zgłoszenie bez URL obrazu
            saveTicketWithImageUri(description, station, reportId, null);
        }
    }

    private void saveTicketWithImageUri(String description, String station, String reportId, String imageUrl) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("tickets");

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("description", description);
        ticket.put("station", station);
        ticket.put("imageUri", imageUrl != null ? imageUrl : ""); // Użyj pustego ciągu, jeśli nie ma URL
        ticket.put("status", "otwarty");

        // Ustaw wartość zgłoszenia w bazie danych
        databaseRef.child(reportId).setValue(ticket)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(WelcomeActivity.this, "Zgłoszenie zostało przesłane", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(WelcomeActivity.this, "Wystąpił błąd przy zapisie zgłoszenia: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

}