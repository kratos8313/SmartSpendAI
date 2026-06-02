package com.smartspend.ai.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.smartspend.ai.R;
import com.smartspend.ai.SmartSpendApp;
import com.smartspend.ai.activities.MainActivity;

public class NotificationUtils {

    private static int notificationId = 1000;

    public static void showBudgetAlert(Context context, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SmartSpendApp.CHANNEL_BUDGET_ALERT)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Budget Alert 💰")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(notificationId++, builder.build());
    }

    public static void showDailyReminder(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("navigate_to", "add_expense");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SmartSpendApp.CHANNEL_DAILY_REMINDER)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Track Your Expenses 📝")
                .setContentText("Don't forget to log today's expenses!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(notificationId++, builder.build());
    }

    public static void showMonthlyReport(Context context, double total, double budget) {
        String message;
        if (budget > 0) {
            double percentage = (total / budget) * 100;
            message = String.format("Monthly summary: Spent %s (%.0f%% of budget)",
                    CurrencyUtils.formatCompact(total), percentage);
        } else {
            message = "Monthly summary: Total spent " + CurrencyUtils.formatCompact(total);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("navigate_to", "analytics");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SmartSpendApp.CHANNEL_MONTHLY_REPORT)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Monthly Report 📊")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(notificationId++, builder.build());
    }
}
