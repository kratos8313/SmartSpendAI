package com.smartspend.ai.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartspend.ai.database.AppDatabase;
import com.smartspend.ai.database.BudgetDao;
import com.smartspend.ai.models.Budget;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetRepository {

    private static final String TAG = "BudgetRepository";

    private final BudgetDao budgetDao;
    private final FirebaseFirestore firestore;
    private final ExecutorService executor;
    private final String userId;

    public BudgetRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        budgetDao = db.budgetDao();
        firestore = FirebaseFirestore.getInstance();
        executor = Executors.newFixedThreadPool(2);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
    }

    public void saveBudget(Budget budget) {
        budget.setUserId(userId);
        executor.execute(() -> {
            budgetDao.insert(budget);
            syncBudgetToFirestore(budget);
        });
    }

    public void updateBudget(Budget budget) {
        executor.execute(() -> {
            budgetDao.update(budget);
            syncBudgetToFirestore(budget);
        });
    }

    public LiveData<Budget> getCurrentMonthBudget() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        return budgetDao.getBudget(userId, month, year);
    }

    public LiveData<Budget> getBudgetForMonth(int month, int year) {
        return budgetDao.getBudget(userId, month, year);
    }

    private void syncBudgetToFirestore(Budget budget) {
        if (userId.isEmpty()) return;
        String docId = userId + "_" + budget.getMonth() + "_" + budget.getYear();
        firestore.collection("users")
                .document(userId)
                .collection("budgets")
                .document(docId)
                .set(budget)
                .addOnSuccessListener(aVoid -> {
                    executor.execute(() -> budgetDao.markAsSynced(budget.getId()));
                    Log.d(TAG, "Budget synced");
                })
                .addOnFailureListener(e -> Log.w(TAG, "Budget sync failed", e));
    }

    public void fetchBudgetFromFirestore(int month, int year) {
        if (userId.isEmpty()) return;
        String docId = userId + "_" + month + "_" + year;
        firestore.collection("users")
                .document(userId)
                .collection("budgets")
                .document(docId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Budget budget = doc.toObject(Budget.class);
                        if (budget != null) {
                            budget.setSynced(true);
                            executor.execute(() -> budgetDao.insert(budget));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Budget fetch failed", e));
    }
}
