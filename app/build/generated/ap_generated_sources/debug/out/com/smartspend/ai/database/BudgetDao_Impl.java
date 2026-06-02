package com.smartspend.ai.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.smartspend.ai.models.Budget;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BudgetDao_Impl implements BudgetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Budget> __insertionAdapterOfBudget;

  private final EntityDeletionOrUpdateAdapter<Budget> __updateAdapterOfBudget;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBudget;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsSynced;

  public BudgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBudget = new EntityInsertionAdapter<Budget>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `budgets` (`id`,`userId`,`totalBudget`,`foodBudget`,`travelBudget`,`shoppingBudget`,`billsBudget`,`entertainmentBudget`,`healthcareBudget`,`month`,`year`,`alertAt80`,`alertAt100`,`isSynced`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Budget entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getUserId());
        }
        statement.bindDouble(3, entity.getTotalBudget());
        statement.bindDouble(4, entity.getFoodBudget());
        statement.bindDouble(5, entity.getTravelBudget());
        statement.bindDouble(6, entity.getShoppingBudget());
        statement.bindDouble(7, entity.getBillsBudget());
        statement.bindDouble(8, entity.getEntertainmentBudget());
        statement.bindDouble(9, entity.getHealthcareBudget());
        statement.bindLong(10, entity.getMonth());
        statement.bindLong(11, entity.getYear());
        final int _tmp = entity.isAlertAt80() ? 1 : 0;
        statement.bindLong(12, _tmp);
        final int _tmp_1 = entity.isAlertAt100() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        final int _tmp_2 = entity.isSynced() ? 1 : 0;
        statement.bindLong(14, _tmp_2);
      }
    };
    this.__updateAdapterOfBudget = new EntityDeletionOrUpdateAdapter<Budget>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `budgets` SET `id` = ?,`userId` = ?,`totalBudget` = ?,`foodBudget` = ?,`travelBudget` = ?,`shoppingBudget` = ?,`billsBudget` = ?,`entertainmentBudget` = ?,`healthcareBudget` = ?,`month` = ?,`year` = ?,`alertAt80` = ?,`alertAt100` = ?,`isSynced` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Budget entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getUserId());
        }
        statement.bindDouble(3, entity.getTotalBudget());
        statement.bindDouble(4, entity.getFoodBudget());
        statement.bindDouble(5, entity.getTravelBudget());
        statement.bindDouble(6, entity.getShoppingBudget());
        statement.bindDouble(7, entity.getBillsBudget());
        statement.bindDouble(8, entity.getEntertainmentBudget());
        statement.bindDouble(9, entity.getHealthcareBudget());
        statement.bindLong(10, entity.getMonth());
        statement.bindLong(11, entity.getYear());
        final int _tmp = entity.isAlertAt80() ? 1 : 0;
        statement.bindLong(12, _tmp);
        final int _tmp_1 = entity.isAlertAt100() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        final int _tmp_2 = entity.isSynced() ? 1 : 0;
        statement.bindLong(14, _tmp_2);
        if (entity.getId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteBudget = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM budgets WHERE userId = ? AND month = ? AND year = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE budgets SET isSynced = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public void insert(final Budget budget) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfBudget.insert(budget);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Budget budget) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfBudget.handle(budget);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteBudget(final String userId, final int month, final int year) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBudget.acquire();
    int _argIndex = 1;
    if (userId == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    _stmt.bindLong(_argIndex, month);
    _argIndex = 3;
    _stmt.bindLong(_argIndex, year);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteBudget.release(_stmt);
    }
  }

  @Override
  public void markAsSynced(final String id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsSynced.acquire();
    int _argIndex = 1;
    if (id == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, id);
    }
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfMarkAsSynced.release(_stmt);
    }
  }

  @Override
  public LiveData<Budget> getBudget(final String userId, final int month, final int year) {
    final String _sql = "SELECT * FROM budgets WHERE userId = ? AND month = ? AND year = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, month);
    _argIndex = 3;
    _statement.bindLong(_argIndex, year);
    return __db.getInvalidationTracker().createLiveData(new String[] {"budgets"}, false, new Callable<Budget>() {
      @Override
      @Nullable
      public Budget call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTotalBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "totalBudget");
          final int _cursorIndexOfFoodBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "foodBudget");
          final int _cursorIndexOfTravelBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "travelBudget");
          final int _cursorIndexOfShoppingBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "shoppingBudget");
          final int _cursorIndexOfBillsBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "billsBudget");
          final int _cursorIndexOfEntertainmentBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "entertainmentBudget");
          final int _cursorIndexOfHealthcareBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "healthcareBudget");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfAlertAt80 = CursorUtil.getColumnIndexOrThrow(_cursor, "alertAt80");
          final int _cursorIndexOfAlertAt100 = CursorUtil.getColumnIndexOrThrow(_cursor, "alertAt100");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final Budget _result;
          if (_cursor.moveToFirst()) {
            _result = new Budget();
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            _result.setId(_tmpId);
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            _result.setUserId(_tmpUserId);
            final double _tmpTotalBudget;
            _tmpTotalBudget = _cursor.getDouble(_cursorIndexOfTotalBudget);
            _result.setTotalBudget(_tmpTotalBudget);
            final double _tmpFoodBudget;
            _tmpFoodBudget = _cursor.getDouble(_cursorIndexOfFoodBudget);
            _result.setFoodBudget(_tmpFoodBudget);
            final double _tmpTravelBudget;
            _tmpTravelBudget = _cursor.getDouble(_cursorIndexOfTravelBudget);
            _result.setTravelBudget(_tmpTravelBudget);
            final double _tmpShoppingBudget;
            _tmpShoppingBudget = _cursor.getDouble(_cursorIndexOfShoppingBudget);
            _result.setShoppingBudget(_tmpShoppingBudget);
            final double _tmpBillsBudget;
            _tmpBillsBudget = _cursor.getDouble(_cursorIndexOfBillsBudget);
            _result.setBillsBudget(_tmpBillsBudget);
            final double _tmpEntertainmentBudget;
            _tmpEntertainmentBudget = _cursor.getDouble(_cursorIndexOfEntertainmentBudget);
            _result.setEntertainmentBudget(_tmpEntertainmentBudget);
            final double _tmpHealthcareBudget;
            _tmpHealthcareBudget = _cursor.getDouble(_cursorIndexOfHealthcareBudget);
            _result.setHealthcareBudget(_tmpHealthcareBudget);
            final int _tmpMonth;
            _tmpMonth = _cursor.getInt(_cursorIndexOfMonth);
            _result.setMonth(_tmpMonth);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            _result.setYear(_tmpYear);
            final boolean _tmpAlertAt80;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAlertAt80);
            _tmpAlertAt80 = _tmp != 0;
            _result.setAlertAt80(_tmpAlertAt80);
            final boolean _tmpAlertAt100;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfAlertAt100);
            _tmpAlertAt100 = _tmp_1 != 0;
            _result.setAlertAt100(_tmpAlertAt100);
            final boolean _tmpIsSynced;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp_2 != 0;
            _result.setSynced(_tmpIsSynced);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Budget getBudgetSync(final String userId, final int month, final int year) {
    final String _sql = "SELECT * FROM budgets WHERE userId = ? AND month = ? AND year = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, month);
    _argIndex = 3;
    _statement.bindLong(_argIndex, year);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfTotalBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "totalBudget");
      final int _cursorIndexOfFoodBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "foodBudget");
      final int _cursorIndexOfTravelBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "travelBudget");
      final int _cursorIndexOfShoppingBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "shoppingBudget");
      final int _cursorIndexOfBillsBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "billsBudget");
      final int _cursorIndexOfEntertainmentBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "entertainmentBudget");
      final int _cursorIndexOfHealthcareBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "healthcareBudget");
      final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
      final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
      final int _cursorIndexOfAlertAt80 = CursorUtil.getColumnIndexOrThrow(_cursor, "alertAt80");
      final int _cursorIndexOfAlertAt100 = CursorUtil.getColumnIndexOrThrow(_cursor, "alertAt100");
      final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
      final Budget _result;
      if (_cursor.moveToFirst()) {
        _result = new Budget();
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        _result.setId(_tmpId);
        final String _tmpUserId;
        if (_cursor.isNull(_cursorIndexOfUserId)) {
          _tmpUserId = null;
        } else {
          _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
        }
        _result.setUserId(_tmpUserId);
        final double _tmpTotalBudget;
        _tmpTotalBudget = _cursor.getDouble(_cursorIndexOfTotalBudget);
        _result.setTotalBudget(_tmpTotalBudget);
        final double _tmpFoodBudget;
        _tmpFoodBudget = _cursor.getDouble(_cursorIndexOfFoodBudget);
        _result.setFoodBudget(_tmpFoodBudget);
        final double _tmpTravelBudget;
        _tmpTravelBudget = _cursor.getDouble(_cursorIndexOfTravelBudget);
        _result.setTravelBudget(_tmpTravelBudget);
        final double _tmpShoppingBudget;
        _tmpShoppingBudget = _cursor.getDouble(_cursorIndexOfShoppingBudget);
        _result.setShoppingBudget(_tmpShoppingBudget);
        final double _tmpBillsBudget;
        _tmpBillsBudget = _cursor.getDouble(_cursorIndexOfBillsBudget);
        _result.setBillsBudget(_tmpBillsBudget);
        final double _tmpEntertainmentBudget;
        _tmpEntertainmentBudget = _cursor.getDouble(_cursorIndexOfEntertainmentBudget);
        _result.setEntertainmentBudget(_tmpEntertainmentBudget);
        final double _tmpHealthcareBudget;
        _tmpHealthcareBudget = _cursor.getDouble(_cursorIndexOfHealthcareBudget);
        _result.setHealthcareBudget(_tmpHealthcareBudget);
        final int _tmpMonth;
        _tmpMonth = _cursor.getInt(_cursorIndexOfMonth);
        _result.setMonth(_tmpMonth);
        final int _tmpYear;
        _tmpYear = _cursor.getInt(_cursorIndexOfYear);
        _result.setYear(_tmpYear);
        final boolean _tmpAlertAt80;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfAlertAt80);
        _tmpAlertAt80 = _tmp != 0;
        _result.setAlertAt80(_tmpAlertAt80);
        final boolean _tmpAlertAt100;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfAlertAt100);
        _tmpAlertAt100 = _tmp_1 != 0;
        _result.setAlertAt100(_tmpAlertAt100);
        final boolean _tmpIsSynced;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfIsSynced);
        _tmpIsSynced = _tmp_2 != 0;
        _result.setSynced(_tmpIsSynced);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Budget> getUnsyncedBudgets(final String userId) {
    final String _sql = "SELECT * FROM budgets WHERE isSynced = 0 AND userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfTotalBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "totalBudget");
      final int _cursorIndexOfFoodBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "foodBudget");
      final int _cursorIndexOfTravelBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "travelBudget");
      final int _cursorIndexOfShoppingBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "shoppingBudget");
      final int _cursorIndexOfBillsBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "billsBudget");
      final int _cursorIndexOfEntertainmentBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "entertainmentBudget");
      final int _cursorIndexOfHealthcareBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "healthcareBudget");
      final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
      final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
      final int _cursorIndexOfAlertAt80 = CursorUtil.getColumnIndexOrThrow(_cursor, "alertAt80");
      final int _cursorIndexOfAlertAt100 = CursorUtil.getColumnIndexOrThrow(_cursor, "alertAt100");
      final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
      final List<Budget> _result = new ArrayList<Budget>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Budget _item;
        _item = new Budget();
        final String _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getString(_cursorIndexOfId);
        }
        _item.setId(_tmpId);
        final String _tmpUserId;
        if (_cursor.isNull(_cursorIndexOfUserId)) {
          _tmpUserId = null;
        } else {
          _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
        }
        _item.setUserId(_tmpUserId);
        final double _tmpTotalBudget;
        _tmpTotalBudget = _cursor.getDouble(_cursorIndexOfTotalBudget);
        _item.setTotalBudget(_tmpTotalBudget);
        final double _tmpFoodBudget;
        _tmpFoodBudget = _cursor.getDouble(_cursorIndexOfFoodBudget);
        _item.setFoodBudget(_tmpFoodBudget);
        final double _tmpTravelBudget;
        _tmpTravelBudget = _cursor.getDouble(_cursorIndexOfTravelBudget);
        _item.setTravelBudget(_tmpTravelBudget);
        final double _tmpShoppingBudget;
        _tmpShoppingBudget = _cursor.getDouble(_cursorIndexOfShoppingBudget);
        _item.setShoppingBudget(_tmpShoppingBudget);
        final double _tmpBillsBudget;
        _tmpBillsBudget = _cursor.getDouble(_cursorIndexOfBillsBudget);
        _item.setBillsBudget(_tmpBillsBudget);
        final double _tmpEntertainmentBudget;
        _tmpEntertainmentBudget = _cursor.getDouble(_cursorIndexOfEntertainmentBudget);
        _item.setEntertainmentBudget(_tmpEntertainmentBudget);
        final double _tmpHealthcareBudget;
        _tmpHealthcareBudget = _cursor.getDouble(_cursorIndexOfHealthcareBudget);
        _item.setHealthcareBudget(_tmpHealthcareBudget);
        final int _tmpMonth;
        _tmpMonth = _cursor.getInt(_cursorIndexOfMonth);
        _item.setMonth(_tmpMonth);
        final int _tmpYear;
        _tmpYear = _cursor.getInt(_cursorIndexOfYear);
        _item.setYear(_tmpYear);
        final boolean _tmpAlertAt80;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfAlertAt80);
        _tmpAlertAt80 = _tmp != 0;
        _item.setAlertAt80(_tmpAlertAt80);
        final boolean _tmpAlertAt100;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfAlertAt100);
        _tmpAlertAt100 = _tmp_1 != 0;
        _item.setAlertAt100(_tmpAlertAt100);
        final boolean _tmpIsSynced;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfIsSynced);
        _tmpIsSynced = _tmp_2 != 0;
        _item.setSynced(_tmpIsSynced);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
