package com.smartspend.ai.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smartspend.ai.database.AppDatabase;
import com.smartspend.ai.database.CategoryTotal;
import com.smartspend.ai.database.ExpenseDao;
import com.smartspend.ai.models.Expense;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {

    private static final String TAG = "ExpenseRepository";
    private static final String COLLECTION_EXPENSES = "expenses";

    private final ExpenseDao expenseDao;
    private final FirebaseFirestore firestore;
    private final ExecutorService executor;
    private final String userId;
    private final MutableLiveData<String> syncError = new MutableLiveData<>();

    public ExpenseRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        expenseDao = db.expenseDao();
        firestore = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
    }

    public void insertExpense(Expense expense) {
        prepareLocalChange(expense, false);
        executor.execute(() -> {
            expenseDao.insert(expense);
            syncExpense(expense);
        });
    }

    public void updateExpense(Expense expense) {
        prepareLocalChange(expense, false);
        executor.execute(() -> {
            expenseDao.update(expense);
            syncExpense(expense);
        });
    }

    public void deleteExpense(Expense expense) {
        prepareLocalChange(expense, true);
        executor.execute(() -> {
            expenseDao.update(expense);
            syncExpense(expense);
        });
    }

    private void prepareLocalChange(Expense expense, boolean deleted) {
        expense.setUserId(userId);
        expense.setDeleted(deleted);
        expense.setUpdatedAt(System.currentTimeMillis());
        expense.setSynced(false);
    }

    private void syncExpense(Expense expense) {
        if (userId.isEmpty()) return;
        if (expense.isDeleted()) {
            firestore.collection("users").document(userId).collection(COLLECTION_EXPENSES)
                    .document(expense.getId()).delete()
                    .addOnSuccessListener(ignored -> executor.execute(() ->
                            expenseDao.deleteById(expense.getId(), userId, expense.getUpdatedAt())))
                    .addOnFailureListener(error -> reportSyncError("Delete sync failed", error));
            return;
        }

        firestore.collection("users").document(userId).collection(COLLECTION_EXPENSES)
                .document(expense.getId()).set(expense)
                .addOnSuccessListener(ignored -> executor.execute(() ->
                        expenseDao.markAsSynced(expense.getId(), userId, expense.getUpdatedAt())))
                .addOnFailureListener(error -> reportSyncError("Expense sync failed", error));
    }

    public void fetchAndSyncFromFirestore() {
        if (userId.isEmpty()) return;
        firestore.collection("users").document(userId).collection(COLLECTION_EXPENSES)
                .orderBy("date", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(snapshots -> executor.execute(() -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                        Expense remote = doc.toObject(Expense.class);
                        if (remote == null) continue;
                        remote.setId(doc.getId());
                        remote.setUserId(userId);
                        remote.setDeleted(false);
                        remote.setSynced(true);
                        Expense local = expenseDao.getExpenseSync(remote.getId(), userId);
                        boolean remoteIsNewer = local == null ||
                                (local.isSynced() && remote.getUpdatedAt() >= local.getUpdatedAt());
                        if (remoteIsNewer) expenseDao.insert(remote);
                    }
                    syncPendingExpensesInternal();
                }))
                .addOnFailureListener(error -> {
                    reportSyncError("Expense fetch failed", error);
                    executor.execute(this::syncPendingExpensesInternal);
                });
    }

    public void syncPendingExpenses() {
        executor.execute(this::syncPendingExpensesInternal);
    }

    private void syncPendingExpensesInternal() {
        for (Expense expense : expenseDao.getUnsyncedExpenses(userId)) syncExpense(expense);
    }

    private void reportSyncError(String message, Exception error) {
        Log.w(TAG, message, error);
        syncError.postValue(message);
    }

    public LiveData<String> getSyncError() { return syncError; }
    public LiveData<List<Expense>> getAllExpenses() { return expenseDao.getAllExpenses(userId); }
    public LiveData<List<Expense>> getRecentExpenses(int limit) { return expenseDao.getRecentExpenses(userId, limit); }
    public LiveData<List<Expense>> getExpensesByDateRange(long start, long end) { return expenseDao.getExpensesByDateRange(userId, start, end); }
    public LiveData<List<Expense>> getExpensesByCategory(String category) { return expenseDao.getExpensesByCategory(userId, category); }
    public LiveData<Double> getTotalInRange(long start, long end) { return expenseDao.getTotalSpendingInRange(userId, start, end); }
    public LiveData<Double> getCategoryTotalInRange(String category, long start, long end) { return expenseDao.getCategorySpendingInRange(userId, category, start, end); }
    public LiveData<Expense> getExpenseById(String id) { return expenseDao.getExpenseById(id, userId); }
    public LiveData<List<CategoryTotal>> getCategoryTotals(long start, long end) { return expenseDao.getCategoryTotals(userId, start, end); }
    public LiveData<List<Expense>> searchExpenses(String query) { return expenseDao.searchExpenses(userId, query); }
    public LiveData<Integer> getExpenseCount() { return expenseDao.getExpenseCount(userId); }
    public void close() { executor.shutdown(); }
}