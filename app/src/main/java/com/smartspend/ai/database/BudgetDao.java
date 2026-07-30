package com.smartspend.ai.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.smartspend.ai.models.Budget;

@Dao
public interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Query("SELECT * FROM budgets WHERE userId = :userId AND month = :month AND year = :year LIMIT 1")
    LiveData<Budget> getBudget(String userId, int month, int year);

    @Query("SELECT * FROM budgets WHERE userId = :userId AND month = :month AND year = :year LIMIT 1")
    Budget getBudgetSync(String userId, int month, int year);

    @Query("DELETE FROM budgets WHERE userId = :userId AND month = :month AND year = :year")
    void deleteBudget(String userId, int month, int year);

    @Query("SELECT * FROM budgets WHERE isSynced = 0 AND userId = :userId")
    java.util.List<Budget> getUnsyncedBudgets(String userId);

    @Query("UPDATE budgets SET isSynced = 1 WHERE id = :id AND userId = :userId AND updatedAt = :updatedAt")
    void markAsSynced(String id, String userId, long updatedAt);
}
