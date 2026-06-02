package com.smartspend.ai.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.smartspend.ai.models.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("DELETE FROM expenses WHERE id = :id")
    void deleteById(String id);

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses(String userId);

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    LiveData<List<Expense>> getRecentExpenses(String userId, int limit);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    LiveData<List<Expense>> getExpensesByDateRange(String userId, long startDate, long endDate);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND category = :category ORDER BY date DESC")
    LiveData<List<Expense>> getExpensesByCategory(String userId, String category);

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    LiveData<Double> getTotalSpendingInRange(String userId, long startDate, long endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND category = :category AND date >= :startDate AND date <= :endDate")
    LiveData<Double> getCategorySpendingInRange(String userId, String category, long startDate, long endDate);

    @Query("SELECT * FROM expenses WHERE isSynced = 0 AND userId = :userId")
    List<Expense> getUnsyncedExpenses(String userId);

    @Query("UPDATE expenses SET isSynced = 1 WHERE id = :id")
    void markAsSynced(String id);

    @Query("SELECT * FROM expenses WHERE id = :id")
    LiveData<Expense> getExpenseById(String id);

    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE userId = :userId AND date >= :startDate AND date <= :endDate GROUP BY category ORDER BY total DESC")
    LiveData<List<CategoryTotal>> getCategoryTotals(String userId, long startDate, long endDate);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND (title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%') ORDER BY date DESC")
    LiveData<List<Expense>> searchExpenses(String userId, String query);

    @Query("SELECT COUNT(*) FROM expenses WHERE userId = :userId")
    LiveData<Integer> getExpenseCount(String userId);
}
