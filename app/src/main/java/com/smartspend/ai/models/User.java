package com.smartspend.ai.models;

public class User {
    private String uid;
    private String name;
    private String email;
    private String photoUrl;
    private String currency;
    private boolean biometricEnabled;
    private boolean darkModeEnabled;
    private boolean notificationsEnabled;
    private long createdAt;

    public User() {
        this.currency = "INR";
        this.notificationsEnabled = true;
        this.createdAt = System.currentTimeMillis();
    }

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.currency = "INR";
        this.notificationsEnabled = true;
        this.createdAt = System.currentTimeMillis();
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isBiometricEnabled() { return biometricEnabled; }
    public void setBiometricEnabled(boolean biometricEnabled) { this.biometricEnabled = biometricEnabled; }

    public boolean isDarkModeEnabled() { return darkModeEnabled; }
    public void setDarkModeEnabled(boolean darkModeEnabled) { this.darkModeEnabled = darkModeEnabled; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
