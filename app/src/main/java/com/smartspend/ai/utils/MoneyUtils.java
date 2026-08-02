package com.smartspend.ai.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public final class MoneyUtils {
    private MoneyUtils() {}

    public static double normalize(double amount) {
        if (!Double.isFinite(amount)) throw new IllegalArgumentException("Amount must be finite");
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static double normalize(double amount, String currencyCode) {
        if (!Double.isFinite(amount)) throw new IllegalArgumentException("Amount must be finite");
        int digits = 2;
        try { int configured = currencyCode == null ? 2 : Currency.getInstance(currencyCode).getDefaultFractionDigits();
            if (configured >= 0) digits = Math.min(configured, 4); }
        catch (IllegalArgumentException ignored) { }
        return BigDecimal.valueOf(amount).setScale(digits, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static boolean isPositive(double amount) {
        return Double.isFinite(amount) && amount > 0;
    }

    public static double nonNegative(double amount) {
        return amount <= 0 ? 0 : normalize(amount);
    }

    public static double nonNegative(double amount, String currencyCode) {
        return amount <= 0 ? 0 : normalize(amount, currencyCode);
    }
}