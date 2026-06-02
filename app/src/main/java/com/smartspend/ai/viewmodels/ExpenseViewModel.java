package com.smartspend.ai.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smartspend.ai.database.CategoryTotal;
import com.smartspend.ai.models.Expense;
import com.smartspend.ai.repositories.ExpenseRepository;
import com.smartspend.ai.utils.DateUtils;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {

    private final ExpenseRepository repository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>("All");

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);
        repository.fetchAndSyncFromFirestore();
        repository.syncPendingExpenses();
    }

    public void insertExpense(Expense expense) {
        repository.insertExpense(expense);
    }

    public void updateExpense(Expense expense) {
        repository.updateExpense(expense);
    }

    public void deleteExpense(Expense expense) {
        repository.deleteExpense(expense);
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return repository.getAllExpenses();
    }

    public LiveData<List<Expense>> getRecentExpenses(int limit) {
        return repository.getRecentExpenses(limit);
    }

    public LiveData<List<Expense>> getThisMonthExpenses() {
        long[] range = DateUtils.getCurrentMonthRange();
        return repository.getExpensesByDateRange(range[0], range[1]);
    }

    public LiveData<List<Expense>> getLastMonthExpenses() {
        long[] range = DateUtils.getLastMonthRange();
        return repository.getExpensesByDateRange(range[0], range[1]);
    }

    public LiveData<List<Expense>> getExpensesByDateRange(long start, long end) {
        return repository.getExpensesByDateRange(start, end);
    }

    public LiveData<List<Expense>> getExpensesByCategory(String category) {
        return repository.getExpensesByCategory(category);
    }

    public LiveData<Double> getCurrentMonthTotal() {
        long[] range = DateUtils.getCurrentMonthRange();
        return repository.getTotalInRange(range[0], range[1]);
    }

    public LiveData<Double> getLastMonthTotal() {
        long[] range = DateUtils.getLastMonthRange();
        return repository.getTotalInRange(range[0], range[1]);
    }

    public LiveData<Double> getCategoryTotalThisMonth(String category) {
        long[] range = DateUtils.getCurrentMonthRange();
        return repository.getCategoryTotalInRange(category, range[0], range[1]);
    }

    public LiveData<List<CategoryTotal>> getCategoryTotalsThisMonth() {
        long[] range = DateUtils.getCurrentMonthRange();
        return repository.getCategoryTotals(range[0], range[1]);
    }

    public LiveData<Expense> getExpenseById(String id) {
        return repository.getExpenseById(id);
    }

    public LiveData<List<Expense>> searchExpenses(String query) {
        return repository.searchExpenses(query);
    }

    public LiveData<Integer> getExpenseCount() {
        return repository.getExpenseCount();
    }

    // Weekly data - get 4 weeks for comparison
    public LiveData<Double> getWeeklyTotal(int weeksAgo) {
        long[] range = DateUtils.getWeekRange(weeksAgo);
        return repository.getTotalInRange(range[0], range[1]);
    }

    public MutableLiveData<String> getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String query) { searchQuery.setValue(query); }

    public MutableLiveData<String> getSelectedCategory() { return selectedCategory; }
    public void setSelectedCategory(String category) { selectedCategory.setValue(category); }
}
