package com.example.test;

public class Ticket {
    private String userId;
    private String description;
    private String imageUri;
    private String station;
    private String status;
    private String ticketId;
    // Konstruktor domyślny wymagany przez Firebase
    public Ticket() {
    }

    // Konstruktor z wszystkimi polami
    public Ticket(String userId, String description, String imageUri, String station, String status,String ticketId) {
        this.userId = userId;
        this.description = description;
        this.imageUri = imageUri;
        this.station = station;
        this.status = status;
        this.ticketId= ticketId;
    }

    // Gettery i settery
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getTicketId() {
        return ticketId;
    }
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}

