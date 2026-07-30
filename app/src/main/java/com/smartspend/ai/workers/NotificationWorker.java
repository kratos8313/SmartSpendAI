package com.smartspend.ai.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartspend.ai.database.AppDatabase;
import com.smartspend.ai.models.Budget;
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

    @NonNull @Override public Result doWork() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return Result.success();
            if (WORK_DAILY_REMINDER.equals(workType)) {
                NotificationUtils.showDailyReminder(getApplicationContext());
            } else if (WORK_MONTHLY_REPORT.equals(workType)) {
                sendMonthlyReportIfDue(user.getUid());
            }
            return Result.success();
        } catch (Exception error) {
            Log.e(TAG, "Worker failed", error);
            return Result.retry();
        }
    }

    private void sendMonthlyReportIfDue(String userId) {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DAY_OF_MONTH) != 1) return;
        String reportKey = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("notification_state", Context.MODE_PRIVATE);
        if (reportKey.equals(prefs.getString("last_monthly_report", ""))) return;

        Calendar start = (Calendar) now.clone();
        start.add(Calendar.MONTH, -1);
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0); start.set(Calendar.MILLISECOND, 0);
        Calendar end = (Calendar) start.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23); end.set(Calendar.MINUTE, 59); end.set(Calendar.SECOND, 59); end.set(Calendar.MILLISECOND, 999);

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        double total = db.expenseDao().getTotalSpendingInRangeSync(userId, start.getTimeInMillis(), end.getTimeInMillis());
        Budget budget = db.budgetDao().getBudgetSync(userId, start.get(Calendar.MONTH) + 1, start.get(Calendar.YEAR));
        NotificationUtils.showMonthlyReport(getApplicationContext(), total, budget != null ? budget.getTotalBudget() : 0);
        prefs.edit().putString("last_monthly_report", reportKey).apply();
    }

    public static void scheduleDailyReminder(Context context) {
        schedule(context, WORK_DAILY_REMINDER, 20, 0);
    }

    public static void scheduleMonthlyReport(Context context) {
        schedule(context, WORK_MONTHLY_REPORT, 9, 0);
    }

    private static void schedule(Context context, String workType, int hour, int minute) {
        Data data = new Data.Builder().putString("work_type", workType).build();
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(NotificationWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(calculateInitialDelay(hour, minute), TimeUnit.MILLISECONDS)
                .setInputData(data).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(workType, ExistingPeriodicWorkPolicy.UPDATE, request);
    }

    private static long calculateInitialDelay(int targetHour, int targetMinute) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, targetHour); target.set(Calendar.MINUTE, targetMinute); target.set(Calendar.SECOND, 0); target.set(Calendar.MILLISECOND, 0);
        if (!target.after(now)) target.add(Calendar.DAY_OF_YEAR, 1);
        return target.getTimeInMillis() - now.getTimeInMillis();
    }
}