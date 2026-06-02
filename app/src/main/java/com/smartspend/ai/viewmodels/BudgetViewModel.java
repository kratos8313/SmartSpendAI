package com.smartspend.ai.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.smartspend.ai.models.Budget;
import com.smartspend.ai.repositories.BudgetRepository;

import java.util.Calendar;

public class BudgetViewModel extends AndroidViewModel {

    private final BudgetRepository repository;

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        repository = new BudgetRepository(application);
        Calendar cal = Calendar.getInstance();
        repository.fetchBudgetFromFirestore(
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR)
        );
    }

    public void saveBudget(Budget budget) {
        repository.saveBudget(budget);
    }

    public void updateBudget(Budget budget) {
        repository.updateBudget(budget);
    }

    public LiveData<Budget> getCurrentMonthBudget() {
        return repository.getCurrentMonthBudget();
    }

    public LiveData<Budget> getBudgetForMonth(int month, int year) {
        return repository.getBudgetForMonth(month, year);
    }
}
