package com.smartspend.ai.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.smartspend.ai.utils.MoneyUtils;

@Entity(tableName = "budgets")
public class Budget {

    @PrimaryKey
    @NonNull
    private String id;

    private String userId;
    private double totalBudget;
    private double foodBudget;
    private double travelBudget;
    private double shoppingBudget;
    private double billsBudget;
    private double entertainmentBudget;
    private double healthcareBudget;
    private int month;
    private int year;
    private boolean alertAt80;
    private boolean alertAt100;
    private boolean isSynced;
    private long updatedAt;

    public Budget() {
        this.id = java.util.UUID.randomUUID().toString();
        this.alertAt80 = true;
        this.alertAt100 = true;
        this.isSynced = false;
        this.updatedAt = System.currentTimeMillis();
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(double totalBudget) { this.totalBudget = MoneyUtils.nonNegative(totalBudget); }

    public double getFoodBudget() { return foodBudget; }
    public void setFoodBudget(double foodBudget) { this.foodBudget = MoneyUtils.nonNegative(foodBudget); }

    public double getTravelBudget() { return travelBudget; }
    public void setTravelBudget(double travelBudget) { this.travelBudget = MoneyUtils.nonNegative(travelBudget); }

    public double getShoppingBudget() { return shoppingBudget; }
    public void setShoppingBudget(double shoppingBudget) { this.shoppingBudget = MoneyUtils.nonNegative(shoppingBudget); }

    public double getBillsBudget() { return billsBudget; }
    public void setBillsBudget(double billsBudget) { this.billsBudget = MoneyUtils.nonNegative(billsBudget); }

    public double getEntertainmentBudget() { return entertainmentBudget; }
    public void setEntertainmentBudget(double entertainmentBudget) { this.entertainmentBudget = MoneyUtils.nonNegative(entertainmentBudget); }

    public double getHealthcareBudget() { return healthcareBudget; }
    public void setHealthcareBudget(double healthcareBudget) { this.healthcareBudget = MoneyUtils.nonNegative(healthcareBudget); }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public boolean isAlertAt80() { return alertAt80; }
    public void setAlertAt80(boolean alertAt80) { this.alertAt80 = alertAt80; }

    public boolean isAlertAt100() { return alertAt100; }
    public void setAlertAt100(boolean alertAt100) { this.alertAt100 = alertAt100; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public double getBudgetForCategory(String category) {
        switch (category) {
            case Expense.CATEGORY_FOOD: return foodBudget;
            case Expense.CATEGORY_TRAVEL: return travelBudget;
            case Expense.CATEGORY_SHOPPING: return shoppingBudget;
            case Expense.CATEGORY_BILLS: return billsBudget;
            case Expense.CATEGORY_ENTERTAINMENT: return entertainmentBudget;
            case Expense.CATEGORY_HEALTHCARE: return healthcareBudget;
            default: return 0;
        }
    }
}
