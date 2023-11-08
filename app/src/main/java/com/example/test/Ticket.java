package com.example.test;

public class Ticket {
    private String description;
    private String station;
    private String imageUri;
    private String status;
    private String userId;

    // Konstruktor domyślny wymagany przez Firebase
    public Ticket() {
    }

    // Konstruktor z parametrami
    public Ticket(String description, String station, String imageUri, String status, String userId) {
        this.description = description;
        this.station = station;
        this.imageUri = imageUri;
        this.status = status;
        this.userId = userId;
    }

    // Gettery i settery dla każdego pola
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
