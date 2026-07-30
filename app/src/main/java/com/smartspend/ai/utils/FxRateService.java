package com.smartspend.ai.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FxRateService {
    private static final String ENDPOINT = "https://api.frankfurter.dev/v2/rate/";
    private static final String PREFS = "fx_rate_cache";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private FxRateService() {}

    public static final class Rate {
        public final double value;
        public final String date;
        public final boolean cached;
        Rate(double value, String date, boolean cached) { this.value = value; this.date = date; this.cached = cached; }
    }
    public interface Callback { void onSuccess(Rate rate); void onFailure(String message); }

    public static void getRate(Context context, String base, String quote, long expenseDate, Callback callback) {
        if (base.equals(quote)) { callback.onSuccess(new Rate(1, formatDate(expenseDate), false)); return; }
        String requestedDate = formatDate(Math.min(expenseDate, System.currentTimeMillis()));
        String key = base + "_" + quote + "_" + requestedDate;
        EXECUTOR.execute(() -> {
            try {
                URL url = new URL(ENDPOINT + base + "/" + quote + "?date=" + requestedDate);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(8000); connection.setReadTimeout(8000);
                connection.setRequestProperty("Accept", "application/json");
                int status = connection.getResponseCode();
                if (status < 200 || status >= 300) throw new IllegalStateException("Rate provider returned " + status);
                StringBuilder body = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String line; while ((line = reader.readLine()) != null) body.append(line);
                } finally { connection.disconnect(); }
                JSONObject data = new JSONObject(body.toString());
                double value = data.getDouble("rate"); String rateDate = data.optString("date", requestedDate);
                if (!base.equals(data.optString("base")) || !quote.equals(data.optString("quote")))
                    throw new IllegalStateException("Unexpected currency pair");
                if (!Double.isFinite(value) || value <= 0) throw new IllegalStateException("Invalid exchange rate");
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                        .putLong(key + "_bits", Double.doubleToLongBits(value)).putString(key + "_date", rateDate).apply();
                deliverSuccess(callback, new Rate(value, rateDate, false));
            } catch (Exception error) {
                SharedPreferences cache = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                if (cache.contains(key + "_bits")) {
                    double value = Double.longBitsToDouble(cache.getLong(key + "_bits", 0));
                    deliverSuccess(callback, new Rate(value, cache.getString(key + "_date", requestedDate), true));
                } else deliverFailure(callback, "No reference rate is available for " + base + " to " + quote + ". Check your connection or choose another currency.");
            }
        });
    }

    private static String formatDate(long millis) { return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date(millis)); }
    private static void deliverSuccess(Callback callback, Rate rate) { new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(rate)); }
    private static void deliverFailure(Callback callback, String message) { new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(message)); }
}