package com.smartspend.ai.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

@Entity(tableName = "expenses")
public class Expense {

    @PrimaryKey
    @NonNull
    private String id;

    private String userId;
    private double amount;
    private String category;
    private String title;
    private String notes;
    private String paymentMode;
    private String location;
    private String tags;
    private long date;
    private String receiptImagePath;
    private boolean isSynced;
    private String currency;
    private double originalAmount;
    private String originalCurrency;

    public Expense() {
        this.id = java.util.UUID.randomUUID().toString();
        this.date = System.currentTimeMillis();
        this.currency = "INR";
        this.isSynced = false;
    }

    public Expense(String id, String userId, double amount, String category,
                   String title, String notes, String paymentMode,
                   String location, String tags, long date) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.title = title;
        this.notes = notes;
        this.paymentMode = paymentMode;
        this.location = location;
        this.tags = tags;
        this.date = date;
        this.currency = "INR";
        this.isSynced = false;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public String getReceiptImagePath() { return receiptImagePath; }
    public void setReceiptImagePath(String receiptImagePath) { this.receiptImagePath = receiptImagePath; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public double getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(double originalAmount) { this.originalAmount = originalAmount; }

    public String getOriginalCurrency() { return originalCurrency; }
    public void setOriginalCurrency(String originalCurrency) { this.originalCurrency = originalCurrency; }

    // Category constants
    public static final String CATEGORY_FOOD = "Food";
    public static final String CATEGORY_TRAVEL = "Travel";
    public static final String CATEGORY_SHOPPING = "Shopping";
    public static final String CATEGORY_BILLS = "Bills";
    public static final String CATEGORY_ENTERTAINMENT = "Entertainment";
    public static final String CATEGORY_HEALTHCARE = "Healthcare";
    public static final String CATEGORY_OTHER = "Other";

    // Payment mode constants
    public static final String PAYMENT_CASH = "Cash";
    public static final String PAYMENT_CARD = "Card";
    public static final String PAYMENT_UPI = "UPI";
    public static final String PAYMENT_NETBANKING = "Net Banking";
    public static final String PAYMENT_WALLET = "Wallet";
}
