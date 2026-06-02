package com.smartspend.ai.models;

public class AiInsight {
    private String type;
    private String title;
    private String message;
    private String iconType;
    private double percentageChange;
    private String category;
    private int severity; // 0=info, 1=warning, 2=alert

    public AiInsight() {}

    public AiInsight(String type, String title, String message, String iconType, int severity) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.iconType = iconType;
        this.severity = severity;
    }

    public static final String TYPE_OVERSPENDING = "overspending";
    public static final String TYPE_SAVINGS = "savings";
    public static final String TYPE_TREND = "trend";
    public static final String TYPE_PREDICTION = "prediction";
    public static final String TYPE_ACHIEVEMENT = "achievement";

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getIconType() { return iconType; }
    public void setIconType(String iconType) { this.iconType = iconType; }

    public double getPercentageChange() { return percentageChange; }
    public void setPercentageChange(double percentageChange) { this.percentageChange = percentageChange; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }
}
