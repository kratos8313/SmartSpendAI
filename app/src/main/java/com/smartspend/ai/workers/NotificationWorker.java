package com.smartspend.ai.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.smartspend.ai.utils.NotificationUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationWorker extends Worker {

    private static final String TAG = "NotificationWorker";
    public static final String WORK_DAILY_REMINDER = "daily_reminder";
    public static final String WORK_MONTHLY_REPORT = "monthly_report";

    private final String workType;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        workType = params.getInputData().getString("work_type");
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if (WORK_DAILY_REMINDER.equals(workType)) {
                NotificationUtils.showDailyReminder(getApplicationContext());
            } else if (WORK_MONTHLY_REPORT.equals(workType)) {
                // Monthly report - would fetch actual data in production
                NotificationUtils.showMonthlyReport(getApplicationContext(), 0, 0);
            }
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Worker failed", e);
            return Result.failure();
        }
    }

    public static void scheduleDailyReminder(Context context) {
        androidx.work.Data inputData = new androidx.work.Data.Builder()
                .putString("work_type", WORK_DAILY_REMINDER)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                NotificationWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(calculateInitialDelay(20, 0), TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_DAILY_REMINDER,
                ExistingPeriodicWorkPolicy.KEEP,
                request
        );
    }

    public static void scheduleMonthlyReport(Context context) {
        androidx.work.Data inputData = new androidx.work.Data.Builder()
                .putString("work_type", WORK_MONTHLY_REPORT)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                NotificationWorker.class, 30, TimeUnit.DAYS)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_MONTHLY_REPORT,
                ExistingPeriodicWorkPolicy.KEEP,
                request
        );
    }

    private static long calculateInitialDelay(int targetHour, int targetMinute) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, targetHour);
        target.set(Calendar.MINUTE, targetMinute);
        target.set(Calendar.SECOND, 0);
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1);
        }
        return target.getTimeInMillis() - now.getTimeInMillis();
    }
}
