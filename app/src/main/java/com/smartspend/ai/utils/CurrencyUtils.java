package com.smartspend.ai.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class CurrencyUtils {
    private static final String PREFS = "currency_preferences";
    private static final String KEY_ACCOUNT_CURRENCY = "account_currency";
    private static final String DEFAULT_CURRENCY = "INR";
    private static final String[] SUPPORTED_CODES =
            {"INR", "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "SGD", "AED"};
    private static final Set<String> SUPPORTED = new HashSet<>(Arrays.asList(SUPPORTED_CODES));
    private static volatile String accountCurrency = DEFAULT_CURRENCY;

    private CurrencyUtils() {}

    public static void initialize(Context context) {
        accountCurrency = preferences(context).getString(KEY_ACCOUNT_CURRENCY, DEFAULT_CURRENCY);
        if (!SUPPORTED.contains(accountCurrency)) accountCurrency = DEFAULT_CURRENCY;
    }

    public static String getAccountCurrency() { return accountCurrency; }

    public static void setAccountCurrency(Context context, String currencyCode) {
        String normalized = normalizeCode(currencyCode);
        if (!SUPPORTED.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported currency: " + currencyCode);
        }
        accountCurrency = normalized;
        preferences(context).edit().putString(KEY_ACCOUNT_CURRENCY, normalized).apply();
    }

    public static String[] getSupportedCodes() { return SUPPORTED_CODES.clone(); }

    public static String getDisplayName(String currencyCode) {
        String code = normalizeCode(currencyCode);
        try {
            Currency currency = Currency.getInstance(code);
            return code + " — " + currency.getDisplayName(Locale.getDefault());
        } catch (IllegalArgumentException error) {
            return code;
        }
    }

    public static String formatAmount(double amount) { return formatAmount(amount, accountCurrency); }

    public static String formatAmount(double amount, String currencyCode) {
        String code = normalizeCode(currencyCode);
        try {
            Currency currency = Currency.getInstance(code);
            NumberFormat format = NumberFormat.getCurrencyInstance(localeFor(code));
            int digits = currency.getDefaultFractionDigits() < 0 ? 2 : currency.getDefaultFractionDigits();
            format.setCurrency(currency);
            format.setMinimumFractionDigits(digits);
            format.setMaximumFractionDigits(digits);
            return format.format(amount);
        } catch (IllegalArgumentException error) {
            return code + " " + String.format(Locale.getDefault(), "%.2f", amount);
        }
    }

    public static String getSymbol(String currencyCode) {
        String code = normalizeCode(currencyCode);
        try {
            return Currency.getInstance(code).getSymbol(localeFor(code));
        } catch (IllegalArgumentException error) {
            return code;
        }
    }

    public static String formatCompact(double amount) { return formatCompact(amount, accountCurrency); }

    public static String formatCompact(double amount, String currencyCode) {
        double absolute = Math.abs(amount);
        double divisor = 1;
        String suffix = "";
        if (absolute >= 1_000_000_000) {
            divisor = 1_000_000_000;
            suffix = "B";
        } else if (absolute >= 1_000_000) {
            divisor = 1_000_000;
            suffix = "M";
        } else if (absolute >= 1_000) {
            divisor = 1_000;
            suffix = "K";
        }
        String value = divisor == 1
                ? String.format(Locale.getDefault(), "%.0f", amount)
                : String.format(Locale.getDefault(), "%.1f", amount / divisor);
        return getSymbol(currencyCode) + value + suffix;
    }

    private static String normalizeCode(String code) {
        return code == null || code.trim().isEmpty()
                ? DEFAULT_CURRENCY
                : code.trim().toUpperCase(Locale.US);
    }

    private static Locale localeFor(String code) {
        switch (code) {
            case "INR": return new Locale("en", "IN");
            case "USD": return Locale.US;
            case "EUR": return Locale.GERMANY;
            case "GBP": return Locale.UK;
            case "JPY": return Locale.JAPAN;
            case "CAD": return Locale.CANADA;
            case "AUD": return new Locale("en", "AU");
            case "SGD": return new Locale("en", "SG");
            case "AED": return new Locale("en", "AE");
            default: return Locale.getDefault();
        }
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
