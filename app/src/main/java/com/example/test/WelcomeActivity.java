package com.example.test;// Import necessary classes
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class WelcomeActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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
        }
    }



    private void submitReport(String description, String station, Uri imageUri) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("reports");

        String reportId = database.push().getKey();  // Generuje unikalny identyfikator dla zgłoszenia
        Map<String, Object> report = new HashMap<>();
        report.put("description", description);
        report.put("station", station);
        // TODO: Przesyłanie zdjęcia do Firebase Storage i zapisanie URL do obrazu tutaj
        report.put("imageUri", imageUri.toString());

        if (reportId != null) {
            database.child(reportId).setValue(report)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(WelcomeActivity.this, "Zgłoszenie zostało przesłane", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(WelcomeActivity.this, "Wystąpił błąd: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
}