package com.smartspend.ai;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.FirebaseApp;
import com.smartspend.ai.utils.ThemeUtils;
import com.smartspend.ai.workers.NotificationWorker;

public class SmartSpendApp extends Application {

    public static final String CHANNEL_BUDGET_ALERT = "budget_alert";
    public static final String CHANNEL_DAILY_REMINDER = "daily_reminder";
    public static final String CHANNEL_MONTHLY_REPORT = "monthly_report";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        createNotificationChannels();
        ThemeUtils.applyTheme(this);
        NotificationWorker.scheduleDailyReminder(this);
        NotificationWorker.scheduleMonthlyReport(this);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            NotificationChannel budgetChannel = new NotificationChannel(
                    CHANNEL_BUDGET_ALERT,
                    "Budget Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            budgetChannel.setDescription("Alerts when you exceed your budget");

            NotificationChannel reminderChannel = new NotificationChannel(
                    CHANNEL_DAILY_REMINDER,
                    "Daily Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            reminderChannel.setDescription("Daily expense tracking reminders");

            NotificationChannel reportChannel = new NotificationChannel(
                    CHANNEL_MONTHLY_REPORT,
                    "Monthly Reports",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            reportChannel.setDescription("Monthly spending summaries");

            manager.createNotificationChannel(budgetChannel);
            manager.createNotificationChannel(reminderChannel);
            manager.createNotificationChannel(reportChannel);
        }
    }
}
