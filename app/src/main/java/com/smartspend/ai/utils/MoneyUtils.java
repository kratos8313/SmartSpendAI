package com.smartspend.ai.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {
    private MoneyUtils() {}

    public static double normalize(double amount) {
        if (!Double.isFinite(amount)) throw new IllegalArgumentException("Amount must be finite");
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static boolean isPositive(double amount) {
        return Double.isFinite(amount) && amount > 0;
    }

    public static double nonNegative(double amount) {
        return amount <= 0 ? 0 : normalize(amount);
    }
}