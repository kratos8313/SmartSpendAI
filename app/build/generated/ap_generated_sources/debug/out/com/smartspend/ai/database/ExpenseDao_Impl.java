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
import com.smartspend.ai.models.Expense;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
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
public final class ExpenseDao_Impl implements ExpenseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Expense> __insertionAdapterOfExpense;

  private final EntityDeletionOrUpdateAdapter<Expense> __deletionAdapterOfExpense;

  private final EntityDeletionOrUpdateAdapter<Expense> __updateAdapterOfExpense;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsSynced;

  public ExpenseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExpense = new EntityInsertionAdapter<Expense>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `expenses` (`id`,`userId`,`amount`,`category`,`title`,`notes`,`paymentMode`,`location`,`tags`,`date`,`receiptImagePath`,`isSynced`,`currency`,`originalAmount`,`originalCurrency`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Expense entity) {
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
        statement.bindDouble(3, entity.getAmount());
        if (entity.getCategory() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategory());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getTitle());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getNotes());
        }
        if (entity.getPaymentMode() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPaymentMode());
        }
        if (entity.getLocation() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLocation());
        }
        if (entity.getTags() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getTags());
        }
        statement.bindLong(10, entity.getDate());
        if (entity.getReceiptImagePath() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getReceiptImagePath());
        }
        final int _tmp = entity.isSynced() ? 1 : 0;
        statement.bindLong(12, _tmp);
        if (entity.getCurrency() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getCurrency());
        }
        statement.bindDouble(14, entity.getOriginalAmount());
        if (entity.getOriginalCurrency() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getOriginalCurrency());
        }
      }
    };
    this.__deletionAdapterOfExpense = new EntityDeletionOrUpdateAdapter<Expense>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `expenses` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Expense entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfExpense = new EntityDeletionOrUpdateAdapter<Expense>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `expenses` SET `id` = ?,`userId` = ?,`amount` = ?,`category` = ?,`title` = ?,`notes` = ?,`paymentMode` = ?,`location` = ?,`tags` = ?,`date` = ?,`receiptImagePath` = ?,`isSynced` = ?,`currency` = ?,`originalAmount` = ?,`originalCurrency` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Expense entity) {
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
        statement.bindDouble(3, entity.getAmount());
        if (entity.getCategory() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategory());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getTitle());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getNotes());
        }
        if (entity.getPaymentMode() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPaymentMode());
        }
        if (entity.getLocation() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLocation());
        }
        if (entity.getTags() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getTags());
        }
        statement.bindLong(10, entity.getDate());
        if (entity.getReceiptImagePath() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getReceiptImagePath());
        }
        final int _tmp = entity.isSynced() ? 1 : 0;
        statement.bindLong(12, _tmp);
        if (entity.getCurrency() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getCurrency());
        }
        statement.bindDouble(14, entity.getOriginalAmount());
        if (entity.getOriginalCurrency() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getOriginalCurrency());
        }
        if (entity.getId() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM expenses WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE expenses SET isSynced = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public void insert(final Expense expense) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfExpense.insert(expense);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Expense expense) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfExpense.handle(expense);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Expense expense) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfExpense.handle(expense);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteById(final String id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
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
      __preparedStmtOfDeleteById.release(_stmt);
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
  public LiveData<List<Expense>> getAllExpenses(final String userId) {
    final String _sql = "SELECT * FROM expenses WHERE userId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"expenses"}, false, new Callable<List<Expense>>() {
      @Override
      @Nullable
      public List<Expense> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptImagePath");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfOriginalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "originalAmount");
          final int _cursorIndexOfOriginalCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "originalCurrency");
          final List<Expense> _result = new ArrayList<Expense>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Expense _item;
            _item = new Expense();
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
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _item.setAmount(_tmpAmount);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            _item.setCategory(_tmpCategory);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            _item.setTitle(_tmpTitle);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final String _tmpPaymentMode;
            if (_cursor.isNull(_cursorIndexOfPaymentMode)) {
              _tmpPaymentMode = null;
            } else {
              _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            }
            _item.setPaymentMode(_tmpPaymentMode);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            _item.setLocation(_tmpLocation);
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            _item.setTags(_tmpTags);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            _item.setDate(_tmpDate);
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            _item.setReceiptImagePath(_tmpReceiptImagePath);
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            _item.setSynced(_tmpIsSynced);
            final String _tmpCurrency;
            if (_cursor.isNull(_cursorIndexOfCurrency)) {
              _tmpCurrency = null;
            } else {
              _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            }
            _item.setCurrency(_tmpCurrency);
            final double _tmpOriginalAmount;
            _tmpOriginalAmount = _cursor.getDouble(_cursorIndexOfOriginalAmount);
            _item.setOriginalAmount(_tmpOriginalAmount);
            final String _tmpOriginalCurrency;
            if (_cursor.isNull(_cursorIndexOfOriginalCurrency)) {
              _tmpOriginalCurrency = null;
            } else {
              _tmpOriginalCurrency = _cursor.getString(_cursorIndexOfOriginalCurrency);
            }
            _item.setOriginalCurrency(_tmpOriginalCurrency);
            _result.add(_item);
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
  public LiveData<List<Expense>> getRecentExpenses(final String userId, final int limit) {
    final String _sql = "SELECT * FROM expenses WHERE userId = ? ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return __db.getInvalidationTracker().createLiveData(new String[] {"expenses"}, false, new Callable<List<Expense>>() {
      @Override
      @Nullable
      public List<Expense> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptImagePath");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfOriginalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "originalAmount");
          final int _cursorIndexOfOriginalCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "originalCurrency");
          final List<Expense> _result = new ArrayList<Expense>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Expense _item;
            _item = new Expense();
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
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _item.setAmount(_tmpAmount);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            _item.setCategory(_tmpCategory);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            _item.setTitle(_tmpTitle);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final String _tmpPaymentMode;
            if (_cursor.isNull(_cursorIndexOfPaymentMode)) {
              _tmpPaymentMode = null;
            } else {
              _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            }
            _item.setPaymentMode(_tmpPaymentMode);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            _item.setLocation(_tmpLocation);
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            _item.setTags(_tmpTags);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            _item.setDate(_tmpDate);
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            _item.setReceiptImagePath(_tmpReceiptImagePath);
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            _item.setSynced(_tmpIsSynced);
            final String _tmpCurrency;
            if (_cursor.isNull(_cursorIndexOfCurrency)) {
              _tmpCurrency = null;
            } else {
              _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            }
            _item.setCurrency(_tmpCurrency);
            final double _tmpOriginalAmount;
            _tmpOriginalAmount = _cursor.getDouble(_cursorIndexOfOriginalAmount);
            _item.setOriginalAmount(_tmpOriginalAmount);
            final String _tmpOriginalCurrency;
            if (_cursor.isNull(_cursorIndexOfOriginalCurrency)) {
              _tmpOriginalCurrency = null;
            } else {
              _tmpOriginalCurrency = _cursor.getString(_cursorIndexOfOriginalCurrency);
            }
            _item.setOriginalCurrency(_tmpOriginalCurrency);
            _result.add(_item);
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
  public LiveData<List<Expense>> getExpensesByDateRange(final String userId, final long startDate,
      final long endDate) {
    final String _sql = "SELECT * FROM expenses WHERE userId = ? AND date >= ? AND date <= ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endDate);
    return __db.getInvalidationTracker().createLiveData(new String[] {"expenses"}, false, new Callable<List<Expense>>() {
      @Override
      @Nullable
      public List<Expense> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptImagePath");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfOriginalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "originalAmount");
          final int _cursorIndexOfOriginalCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "originalCurrency");
          final List<Expense> _result = new ArrayList<Expense>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Expense _item;
            _item = new Expense();
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
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _item.setAmount(_tmpAmount);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            _item.setCategory(_tmpCategory);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            _item.setTitle(_tmpTitle);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final String _tmpPaymentMode;
            if (_cursor.isNull(_cursorIndexOfPaymentMode)) {
              _tmpPaymentMode = null;
            } else {
              _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            }
            _item.setPaymentMode(_tmpPaymentMode);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            _item.setLocation(_tmpLocation);
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            _item.setTags(_tmpTags);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            _item.setDate(_tmpDate);
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            _item.setReceiptImagePath(_tmpReceiptImagePath);
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            _item.setSynced(_tmpIsSynced);
            final String _tmpCurrency;
            if (_cursor.isNull(_cursorIndexOfCurrency)) {
              _tmpCurrency = null;
            } else {
              _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            }
            _item.setCurrency(_tmpCurrency);
            final double _tmpOriginalAmount;
            _tmpOriginalAmount = _cursor.getDouble(_cursorIndexOfOriginalAmount);
            _item.setOriginalAmount(_tmpOriginalAmount);
            final String _tmpOriginalCurrency;
            if (_cursor.isNull(_cursorIndexOfOriginalCurrency)) {
              _tmpOriginalCurrency = null;
            } else {
              _tmpOriginalCurrency = _cursor.getString(_cursorIndexOfOriginalCurrency);
            }
            _item.setOriginalCurrency(_tmpOriginalCurrency);
            _result.add(_item);
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
  public LiveData<List<Expense>> getExpensesByCategory(final String userId, final String category) {
    final String _sql = "SELECT * FROM expenses WHERE userId = ? AND category = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    if (category == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, category);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"expenses"}, false, new Callable<List<Expense>>() {
      @Override
      @Nullable
      public List<Expense> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptImagePath");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfOriginalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "originalAmount");
          final int _cursorIndexOfOriginalCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "originalCurrency");
          final List<Expense> _result = new ArrayList<Expense>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Expense _item;
            _item = new Expense();
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
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _item.setAmount(_tmpAmount);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            _item.setCategory(_tmpCategory);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            _item.setTitle(_tmpTitle);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final String _tmpPaymentMode;
            if (_cursor.isNull(_cursorIndexOfPaymentMode)) {
              _tmpPaymentMode = null;
            } else {
              _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            }
            _item.setPaymentMode(_tmpPaymentMode);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            _item.setLocation(_tmpLocation);
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            _item.setTags(_tmpTags);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            _item.setDate(_tmpDate);
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            _item.setReceiptImagePath(_tmpReceiptImagePath);
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            _item.setSynced(_tmpIsSynced);
            final String _tmpCurrency;
            if (_cursor.isNull(_cursorIndexOfCurrency)) {
              _tmpCurrency = null;
            } else {
              _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            }
            _item.setCurrency(_tmpCurrency);
            final double _tmpOriginalAmount;
            _tmpOriginalAmount = _cursor.getDouble(_cursorIndexOfOriginalAmount);
            _item.setOriginalAmount(_tmpOriginalAmount);
            final String _tmpOriginalCurrency;
            if (_cursor.isNull(_cursorIndexOfOriginalCurrency)) {
              _tmpOriginalCurrency = null;
            } else {
              _tmpOriginalCurrency = _cursor.getString(_cursorIndexOfOriginalCurrency);
            }
            _item.setOriginalCurrency(_tmpOriginalCurrency);
            _result.add(_item);
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
  public LiveData<Double> getTotalSpendingInRange(final String userId, final long startDate,
      final long endDate) {
    final String _sql = "SELECT SUM(amount) FROM expenses WHERE userId = ? AND date >= ? AND date <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endDate);
    return __db.getInvalidationTracker().createLiveData(new String[] {"expenses"}, false, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
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
  public LiveData<Double> getCategorySpendingInRange(final String userId, final String category,
      final long startDate, final long endDate) {
    final String _sql = "SELECT SUM(amount) FROM expenses WHERE userId = ? AND category = ? AND date >= ? AND date <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    if (category == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, category);
    }
    _argIndex = 3;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 4;
    _statement.bindLong(_argIndex, endDate);
    return __db.getInvalidationTracker().createLiveData(new String[] {"expenses"}, false, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
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
  public List<Expense> getUnsyncedExpenses(final String userId) {
    final String _sql = "SELECT * FROM expenses WHERE isSynced = 0 AND userId = ?";
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
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
      final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
      final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptImagePath");
      final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
      final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
      final int _cursorIndexOfOriginalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "originalAmount");
      final int _cursorIndexOfOriginalCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "originalCurrency");
      final List<Expense> _result = new ArrayList<Expense>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Expense _item;
        _item = new Expense();
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
        final double _tmpAmount;
        _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
        _item.setAmount(_tmpAmount);
        final String _tmpCategory;
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _tmpCategory = null;
        } else {
          _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
        }
        _item.setCategory(_tmpCategory);
        final String _tmpTitle;
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _tmpTitle = null;
        } else {
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        }
        _item.setTitle(_tmpTitle);
        final String _tmpNotes;
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _tmpNotes = null;
        } else {
          _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
        }
        _item.setNotes(_tmpNotes);
        final String _tmpPaymentMode;
        if (_cursor.isNull(_cursorIndexOfPaymentMode)) {
          _tmpPaymentMode = null;
        } else {
          _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
        }
        _item.setPaymentMode(_tmpPaymentMode);
        final String _tmpLocation;
        if (_cursor.isNull(_cursorIndexOfLocation)) {
          _tmpLocation = null;
        } else {
          _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
        }
        _item.setLocation(_tmpLocation);
        final String _tmpTags;
        if (_cursor.isNull(_cursorIndexOfTags)) {
          _tmpTags = null;
        } else {
          _tmpTags = _cursor.getString(_cursorIndexOfTags);
        }
        _item.setTags(_tmpTags);
        final long _tmpDate;
        _tmpDate = _cursor.getLong(_cursorIndexOfDate);
        _item.setDate(_tmpDate);
        final String _tmpReceiptImagePath;
        if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
          _tmpReceiptImagePath = null;
        } else {
          _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
        }
        _item.setReceiptImagePath(_tmpReceiptImagePath);
        final boolean _tmpIsSynced;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
        _tmpIsSynced = _tmp != 0;
        _item.setSynced(_tmpIsSynced);
        final String _tmpCurrency;
        if (_cursor.isNull(_cursorIndexOfCurrency)) {
          _tmpCurrency = null;
        } else {
          _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
        }
        _item.setCurrency(_tmpCurrency);
        final double _tmpOriginalAmount;
        _tmpOriginalAmount = _cursor.getDouble(_cursorIndexOfOriginalAmount);
        _item.setOriginalAmount(_tmpOriginalAmount);
        final String _tmpOriginalCurrency;
        if (_cursor.isNull(_cursorIndexOfOriginalCurrency)) {
          _tmpOriginalCurrency = null;
        } else {
          _tmpOriginalCurrency = _cursor.getString(_cursorIndexOfOriginalCurrency);
        }
        _item.setOriginalCurrency(_tmpOriginalCurrency);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<Expense> getExpenseById(final String id) {
    final String _sql = "SELECT * FROM expenses WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"expenses"}, false, new Callable<Expense>() {
      @Override
      @Nullable
      public Expense call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptImagePath");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfOriginalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "originalAmount");
          final int _cursorIndexOfOriginalCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "originalCurrency");
          final Expense _result;
          if (_cursor.moveToFirst()) {
            _result = new Expense();
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
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _result.setAmount(_tmpAmount);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            _result.setCategory(_tmpCategory);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            _result.setTitle(_tmpTitle);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _result.setNotes(_tmpNotes);
            final String _tmpPaymentMode;
            if (_cursor.isNull(_cursorIndexOfPaymentMode)) {
              _tmpPaymentMode = null;
            } else {
              _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            }
            _result.setPaymentMode(_tmpPaymentMode);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            _result.setLocation(_tmpLocation);
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            _result.setTags(_tmpTags);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            _result.setDate(_tmpDate);
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            _result.setReceiptImagePath(_tmpReceiptImagePath);
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            _result.setSynced(_tmpIsSynced);
            final String _tmpCurrency;
            if (_cursor.isNull(_cursorIndexOfCurrency)) {
              _tmpCurrency = null;
            } else {
              _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            }
            _result.setCurrency(_tmpCurrency);
            final double _tmpOriginalAmount;
            _tmpOriginalAmount = _cursor.getDouble(_cursorIndexOfOriginalAmount);
            _result.setOriginalAmount(_tmpOriginalAmount);
            final String _tmpOriginalCurrency;
            if (_cursor.isNull(_cursorIndexOfOriginalCurrency)) {
              _tmpOriginalCurrency = null;
            } else {
              _tmpOriginalCurrency = _cursor.getString(_cursorIndexOfOriginalCurrency);
            }
            _result.setOriginalCurrency(_tmpOriginalCurrency);
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
  public LiveData<List<CategoryTotal>> getCategoryTotals(final String userId, final long startDate,
      final long endDate) {
    final String _sql = "SELECT category, SUM(amount) as total FROM expenses WHERE userId = ? AND date >= ? AND date <= ? GROUP BY category ORDER BY total DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endDate);
    return __db.getInvalidationTracker().createLiveData(new String[] {"expenses"}, false, new Callable<List<CategoryTotal>>() {
      @Override
      @Nullable
      public List<CategoryTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategory = 0;
          final int _cursorIndexOfTotal = 1;
          final List<CategoryTotal> _result = new ArrayList<CategoryTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryTotal _item;
            _item = new CategoryTotal();
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _item.category = null;
            } else {
              _item.category = _cursor.getString(_cursorIndexOfCategory);
            }
            _item.total = _cursor.getDouble(_cursorIndexOfTotal);
            _result.add(_item);
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
  public LiveData<List<Expense>> searchExpenses(final String userId, final String query) {
    final String _sql = "SELECT * FROM expenses WHERE userId = ? AND (title LIKE '%' || ? || '%' OR notes LIKE '%' || ? || '%' OR tags LIKE '%' || ? || '%') ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    _argIndex = 2;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 3;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 4;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"expenses"}, false, new Callable<List<Expense>>() {
      @Override
      @Nullable
      public List<Expense> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfReceiptImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "receiptImagePath");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfOriginalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "originalAmount");
          final int _cursorIndexOfOriginalCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "originalCurrency");
          final List<Expense> _result = new ArrayList<Expense>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Expense _item;
            _item = new Expense();
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
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _item.setAmount(_tmpAmount);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            _item.setCategory(_tmpCategory);
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            _item.setTitle(_tmpTitle);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final String _tmpPaymentMode;
            if (_cursor.isNull(_cursorIndexOfPaymentMode)) {
              _tmpPaymentMode = null;
            } else {
              _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            }
            _item.setPaymentMode(_tmpPaymentMode);
            final String _tmpLocation;
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _tmpLocation = null;
            } else {
              _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            }
            _item.setLocation(_tmpLocation);
            final String _tmpTags;
            if (_cursor.isNull(_cursorIndexOfTags)) {
              _tmpTags = null;
            } else {
              _tmpTags = _cursor.getString(_cursorIndexOfTags);
            }
            _item.setTags(_tmpTags);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            _item.setDate(_tmpDate);
            final String _tmpReceiptImagePath;
            if (_cursor.isNull(_cursorIndexOfReceiptImagePath)) {
              _tmpReceiptImagePath = null;
            } else {
              _tmpReceiptImagePath = _cursor.getString(_cursorIndexOfReceiptImagePath);
            }
            _item.setReceiptImagePath(_tmpReceiptImagePath);
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            _item.setSynced(_tmpIsSynced);
            final String _tmpCurrency;
            if (_cursor.isNull(_cursorIndexOfCurrency)) {
              _tmpCurrency = null;
            } else {
              _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            }
            _item.setCurrency(_tmpCurrency);
            final double _tmpOriginalAmount;
            _tmpOriginalAmount = _cursor.getDouble(_cursorIndexOfOriginalAmount);
            _item.setOriginalAmount(_tmpOriginalAmount);
            final String _tmpOriginalCurrency;
            if (_cursor.isNull(_cursorIndexOfOriginalCurrency)) {
              _tmpOriginalCurrency = null;
            } else {
              _tmpOriginalCurrency = _cursor.getString(_cursorIndexOfOriginalCurrency);
            }
            _item.setOriginalCurrency(_tmpOriginalCurrency);
            _result.add(_item);
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
  public LiveData<Integer> getExpenseCount(final String userId) {
    final String _sql = "SELECT COUNT(*) FROM expenses WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"expenses"}, false, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
