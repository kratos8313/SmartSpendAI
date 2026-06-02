package com.smartspend.ai.utils;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyUtils {

    public static String formatAmount(double amount) {
        return formatAmount(amount, "INR");
    }

    public static String formatAmount(double amount, String currencyCode) {
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            format.setCurrency(Currency.getInstance(currencyCode));
            return format.format(amount);
        } catch (Exception e) {
            return String.format(Locale.getDefault(), "₹%.2f", amount);
        }
    }

    public static String getSymbol(String currencyCode) {
        switch (currencyCode) {
            case "INR": return "₹";
            case "USD": return "$";
            case "EUR": return "€";
            case "GBP": return "£";
            case "JPY": return "¥";
            default: return currencyCode;
        }
    }

    public static String formatCompact(double amount) {
        if (amount >= 100000) {
            return String.format(Locale.getDefault(), "₹%.1fL", amount / 100000);
        } else if (amount >= 1000) {
            return String.format(Locale.getDefault(), "₹%.1fK", amount / 1000);
        }
        return String.format(Locale.getDefault(), "₹%.0f", amount);
    }
}
