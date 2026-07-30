package com.smartspend.ai.repositories;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    private final MutableLiveData<String> syncError = new MutableLiveData<>();

    public BudgetRepository(Context context) {
        budgetDao = AppDatabase.getInstance(context).budgetDao();
        firestore = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
    }

    public void saveBudget(Budget budget) { persistBudget(budget, false); }
    public void updateBudget(Budget budget) { persistBudget(budget, true); }

    private void persistBudget(Budget budget, boolean update) {
        budget.setUserId(userId);
        budget.setUpdatedAt(System.currentTimeMillis());
        budget.setSynced(false);
        executor.execute(() -> {
            if (update) budgetDao.update(budget); else budgetDao.insert(budget);
            syncBudgetToFirestore(budget);
        });
    }

    public LiveData<Budget> getCurrentMonthBudget() {
        Calendar cal = Calendar.getInstance();
        return budgetDao.getBudget(userId, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    public LiveData<Budget> getBudgetForMonth(int month, int year) {
        return budgetDao.getBudget(userId, month, year);
    }

    private void syncBudgetToFirestore(Budget budget) {
        if (userId.isEmpty()) return;
        String docId = userId + "_" + budget.getMonth() + "_" + budget.getYear();
        firestore.collection("users").document(userId).collection("budgets").document(docId)
                .set(budget)
                .addOnSuccessListener(ignored -> executor.execute(() ->
                        budgetDao.markAsSynced(budget.getId(), userId, budget.getUpdatedAt())))
                .addOnFailureListener(error -> reportError("Budget sync failed", error));
    }

    public void fetchBudgetFromFirestore(int month, int year) {
        if (userId.isEmpty()) return;
        String docId = userId + "_" + month + "_" + year;
        firestore.collection("users").document(userId).collection("budgets").document(docId).get()
                .addOnSuccessListener(doc -> executor.execute(() -> {
                    Budget local = budgetDao.getBudgetSync(userId, month, year);
                    if (doc.exists()) {
                        Budget remote = doc.toObject(Budget.class);
                        if (remote != null) {
                            remote.setUserId(userId);
                            remote.setSynced(true);
                            if (local == null || (local.isSynced() && remote.getUpdatedAt() >= local.getUpdatedAt())) {
                                budgetDao.insert(remote);
                                local = remote;
                            }
                        }
                    }
                    if (local != null && !local.isSynced()) syncBudgetToFirestore(local);
                }))
                .addOnFailureListener(error -> reportError("Budget fetch failed", error));
    }

    private void reportError(String message, Exception error) {
        Log.w(TAG, message, error);
        syncError.postValue(message);
    }

    public LiveData<String> getSyncError() { return syncError; }
    public void close() { executor.shutdown(); }
}