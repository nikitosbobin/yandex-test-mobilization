package com.nikit.bobin.wordstranslate.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nikit.bobin.wordstranslate.core.Ensure;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

public abstract class AbstractDatabaseOneTableContext<TItem>
        implements Closeable {
    protected final String databaseName;
    protected final String tableName;
    private String columnsDescription;
    private SQLiteDatabase database;
    private ArrayList<OnItemsUpdateListener> onItemsUpdateListeners;
    private String primaryKeyName;

    public AbstractDatabaseOneTableContext(
            String databaseName,
            String tableName,
            String columnsDescription,
            String primaryKeyName) {
        Ensure.notNullOrEmpty(databaseName, "databaseName");
        Ensure.notNullOrEmpty(tableName, "tableName");
        Ensure.notNullOrEmpty(columnsDescription, "columnsDescription");
        Ensure.notNullOrEmpty(primaryKeyName, "primaryKeyName");

        this.databaseName = databaseName;
        this.tableName = tableName;
        this.columnsDescription = columnsDescription;
        this.primaryKeyName = primaryKeyName;
        onItemsUpdateListeners = new ArrayList<>();
    }

    abstract TItem deserialize(Cursor cursor);
    abstract ContentValues serialize(TItem item);

    protected boolean add(TItem item, boolean notify) {
        ContentValues values = serialize(item);

        boolean anyInserted = database.insertWithOnConflict(
                tableName,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE) != -1;
        notifyDataChanged(anyInserted && notify);
        return anyInserted;
    }

    protected boolean insertOrUpdate(TItem item, boolean notify) {
        ContentValues values = serialize(item);
        int id = -1;
        if (values.containsKey(primaryKeyName))
            id = values.getAsInteger(primaryKeyName);

        boolean anyInserted = database.insertWithOnConflict(
                tableName,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE) != -1;
        if (!anyInserted) {
            boolean anyUpdated = database.update(
                    tableName,
                    values,
                    primaryKeyName + "=?",
                    new String[]{id + ""}) != 0;
            notifyDataChanged(anyUpdated && notify);
            return anyUpdated;
        }
        notifyDataChanged(notify);
        return true;
    }

    protected boolean delete(String whereClause, String[] whereArgs, boolean notify) {
        if (!isConnected()) return false;
        boolean anyDeleted = database.delete(tableName, whereClause, whereArgs) > 0;
        notifyDataChanged(anyDeleted && notify);
        return anyDeleted;
    }

    protected void clearTable(boolean notify) {
        if (!isConnected()) return;
        if (database.delete(tableName, null, null) > 0) notifyDataChanged(notify);
    }

    protected ArrayList<TItem> extractAllData(
            String[] columns,
            String where,
            String[] whereArgs,
            String orderBy) {
        Cursor cursor = database.query(tableName, columns, where, whereArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            ArrayList<TItem> result = new ArrayList<>();
            do {
                TItem item = deserialize(cursor);
                result.add(item);
            } while (cursor.moveToNext());
            cursor.close();
            return result;
        }
        return new ArrayList<>(0);
    }

    public boolean isConnected() {
        return database != null && database.isOpen();
    }

    protected long getCount(String where,
                            String[] whereArgs) {
        Cursor cursor = database.query(tableName, new String[] {"count(*)"}, where, whereArgs, null, null, null);
        long result = 0L;
        if (cursor.moveToFirst())
            result = cursor.getLong(0);
        cursor.close();
        return result;
    }

    private SQLiteDatabase getDataBase(Context context) {
        String createTableQuery = String.format(
                "create table if not exists %s (%s)",
                tableName,
                columnsDescription);
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(
                databaseName,
                Context.MODE_PRIVATE,
                null);
        sqLiteDatabase.execSQL(createTableQuery);
        return sqLiteDatabase;
    }

    public void connect(Context context) {
        if (!isConnected())
            database = getDataBase(context);
    }

    @Override
    public void close() throws IOException {
        if (isConnected())
            database.close();
    }

    private void notifyDataChanged(boolean notify) {
        if (notify && onItemsUpdateListeners.size() != 0) {
            for (OnItemsUpdateListener listener : onItemsUpdateListeners)
                listener.onDatabaseChange();
        }
    }

    public void addOnItemsUpdateListener(OnItemsUpdateListener onItemsUpdateListener) {
        if (!onItemsUpdateListeners.contains(onItemsUpdateListener))
            onItemsUpdateListeners.add(onItemsUpdateListener);
    }

    public interface OnItemsUpdateListener {
        void onDatabaseChange();
    }
}
