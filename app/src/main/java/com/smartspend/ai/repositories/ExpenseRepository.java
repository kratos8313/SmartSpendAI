package com.smartspend.ai.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

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

    public ExpenseRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        expenseDao = db.expenseDao();
        firestore = FirebaseFirestore.getInstance();
        executor = Executors.newFixedThreadPool(4);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
    }

    // Insert expense - local + cloud
    public void insertExpense(Expense expense) {
        expense.setUserId(userId);
        executor.execute(() -> {
            expenseDao.insert(expense);
            syncToFirestore(expense);
        });
    }

    // Update expense
    public void updateExpense(Expense expense) {
        executor.execute(() -> {
            expenseDao.update(expense);
            syncToFirestore(expense);
        });
    }

    // Delete expense
    public void deleteExpense(Expense expense) {
        executor.execute(() -> {
            expenseDao.delete(expense);
            deleteFromFirestore(expense.getId());
        });
    }

    // Sync to Firestore
    private void syncToFirestore(Expense expense) {
        if (userId.isEmpty()) return;
        firestore.collection("users")
                .document(userId)
                .collection(COLLECTION_EXPENSES)
                .document(expense.getId())
                .set(expense)
                .addOnSuccessListener(aVoid -> {
                    executor.execute(() -> expenseDao.markAsSynced(expense.getId()));
                    Log.d(TAG, "Expense synced: " + expense.getId());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Sync failed", e));
    }

    private void deleteFromFirestore(String expenseId) {
        if (userId.isEmpty()) return;
        firestore.collection("users")
                .document(userId)
                .collection(COLLECTION_EXPENSES)
                .document(expenseId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Deleted from Firestore"))
                .addOnFailureListener(e -> Log.w(TAG, "Delete failed", e));
    }

    // Fetch from Firestore & cache locally
    public void fetchAndSyncFromFirestore() {
        if (userId.isEmpty()) return;
        firestore.collection("users")
                .document(userId)
                .collection(COLLECTION_EXPENSES)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    executor.execute(() -> {
                        for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                            Expense expense = doc.toObject(Expense.class);
                            if (expense != null) {
                                expense.setSynced(true);
                                expenseDao.insert(expense);
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> Log.w(TAG, "Fetch failed", e));
    }

    // LiveData queries
    public LiveData<List<Expense>> getAllExpenses() {
        return expenseDao.getAllExpenses(userId);
    }

    public LiveData<List<Expense>> getRecentExpenses(int limit) {
        return expenseDao.getRecentExpenses(userId, limit);
    }

    public LiveData<List<Expense>> getExpensesByDateRange(long start, long end) {
        return expenseDao.getExpensesByDateRange(userId, start, end);
    }

    public LiveData<List<Expense>> getExpensesByCategory(String category) {
        return expenseDao.getExpensesByCategory(userId, category);
    }

    public LiveData<Double> getTotalInRange(long start, long end) {
        return expenseDao.getTotalSpendingInRange(userId, start, end);
    }

    public LiveData<Double> getCategoryTotalInRange(String category, long start, long end) {
        return expenseDao.getCategorySpendingInRange(userId, category, start, end);
    }

    public LiveData<Expense> getExpenseById(String id) {
        return expenseDao.getExpenseById(id);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotals(long start, long end) {
        return expenseDao.getCategoryTotals(userId, start, end);
    }

    public LiveData<List<Expense>> searchExpenses(String query) {
        return expenseDao.searchExpenses(userId, query);
    }

    public LiveData<Integer> getExpenseCount() {
        return expenseDao.getExpenseCount(userId);
    }

    // Sync unsynced local data
    public void syncPendingExpenses() {
        executor.execute(() -> {
            List<Expense> unsyncedExpenses = expenseDao.getUnsyncedExpenses(userId);
            for (Expense e : unsyncedExpenses) {
                syncToFirestore(e);
            }
        });
    }
}
