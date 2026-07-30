package com.smartspend.ai.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.annotation.NonNull;

import com.smartspend.ai.models.Budget;
import com.smartspend.ai.models.Expense;

@Database(entities = {Expense.class, Budget.class}, version = 3, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ExpenseDao expenseDao();
    public abstract BudgetDao budgetDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE expenses ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE expenses ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE budgets ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0");
            database.execSQL("UPDATE expenses SET updatedAt = date WHERE updatedAt = 0");
        }
    };


    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE expenses ADD COLUMN exchangeRate REAL NOT NULL DEFAULT 1.0");
            database.execSQL("ALTER TABLE expenses ADD COLUMN exchangeRateTimestamp INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE expenses ADD COLUMN exchangeRateSource TEXT");
            database.execSQL("UPDATE expenses SET originalAmount = amount WHERE originalAmount = 0");
            database.execSQL("UPDATE expenses SET originalCurrency = currency WHERE originalCurrency IS NULL OR originalCurrency = ''");
        }
    };
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "smartspend_db"
                            )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
