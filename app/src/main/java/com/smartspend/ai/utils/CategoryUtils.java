package com.smartspend.ai.utils;

import android.graphics.Color;

import com.smartspend.ai.R;
import com.smartspend.ai.models.Expense;

public class CategoryUtils {

    public static int getCategoryIcon(String category) {
        if (category == null) return R.drawable.ic_expense;
        switch (category) {
            case Expense.CATEGORY_FOOD: return R.drawable.ic_food;
            case Expense.CATEGORY_TRAVEL: return R.drawable.ic_travel;
            case Expense.CATEGORY_SHOPPING: return R.drawable.ic_shopping;
            case Expense.CATEGORY_BILLS: return R.drawable.ic_bills;
            case Expense.CATEGORY_ENTERTAINMENT: return R.drawable.ic_entertainment;
            case Expense.CATEGORY_HEALTHCARE: return R.drawable.ic_health;
            default: return R.drawable.ic_expense;
        }
    }

    public static int getCategoryColor(String category) {
        if (category == null) return Color.parseColor("#9E9E9E");
        switch (category) {
            case Expense.CATEGORY_FOOD: return Color.parseColor("#FF6B6B");
            case Expense.CATEGORY_TRAVEL: return Color.parseColor("#4ECDC4");
            case Expense.CATEGORY_SHOPPING: return Color.parseColor("#45B7D1");
            case Expense.CATEGORY_BILLS: return Color.parseColor("#96CEB4");
            case Expense.CATEGORY_ENTERTAINMENT: return Color.parseColor("#FFEAA7");
            case Expense.CATEGORY_HEALTHCARE: return Color.parseColor("#DDA0DD");
            default: return Color.parseColor("#9E9E9E");
        }
    }

    public static int getCategoryColorRes(String category) {
        if (category == null) return R.color.category_other;
        switch (category) {
            case Expense.CATEGORY_FOOD: return R.color.category_food;
            case Expense.CATEGORY_TRAVEL: return R.color.category_travel;
            case Expense.CATEGORY_SHOPPING: return R.color.category_shopping;
            case Expense.CATEGORY_BILLS: return R.color.category_bills;
            case Expense.CATEGORY_ENTERTAINMENT: return R.color.category_entertainment;
            case Expense.CATEGORY_HEALTHCARE: return R.color.category_healthcare;
            default: return R.color.category_other;
        }
    }

    public static String[] getAllCategories() {
        return new String[]{
                Expense.CATEGORY_FOOD,
                Expense.CATEGORY_TRAVEL,
                Expense.CATEGORY_SHOPPING,
                Expense.CATEGORY_BILLS,
                Expense.CATEGORY_ENTERTAINMENT,
                Expense.CATEGORY_HEALTHCARE,
                Expense.CATEGORY_OTHER
        };
    }

    public static String[] getAllPaymentModes() {
        return new String[]{
                Expense.PAYMENT_CASH,
                Expense.PAYMENT_CARD,
                Expense.PAYMENT_UPI,
                Expense.PAYMENT_NETBANKING,
                Expense.PAYMENT_WALLET
        };
    }
}
