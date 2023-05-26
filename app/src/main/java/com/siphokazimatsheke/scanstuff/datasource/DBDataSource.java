package com.siphokazimatsheke.scanstuff.datasource;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.siphokazimatsheke.scanstuff.models.BarCodeModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBDataSource {

    private SQLiteDatabase database;
    private DBSQLiteHelper databaseHelper;

    public DBDataSource(Context context) {
        databaseHelper = new DBSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
        databaseHelper.close();
    }

    public void deleteDBDatasource() {
        File dbFile = new File(database.getPath());
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    public void begin() {
        database.beginTransaction();
    }

    public void commit() {
        database.setTransactionSuccessful();
    }

    public void end() {
        database.endTransaction();
    }

    @Deprecated
    public void beginTransaction(String type) {
        database.beginTransaction();
    }

    @Deprecated
    public void endTransaction() {
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    @Deprecated
    public void rollback() {
        // database.endTransaction();
    }

    public boolean existBarCode(String id) {
        if (id == null) {
            return false;
        }

        String sql = "SELECT " + DBSQLiteHelper.BAR_CODE_NUMBER_COLUMN +
                " FROM " + DBSQLiteHelper.BAR_CODE_TABLE +
                " WHERE " + DBSQLiteHelper.BAR_CODE_NUMBER_COLUMN + " = '" + id + "'";

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            return cursor.getCount() == 1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void saveBarCode(BarCodeModel barCodeModel) {
        if (existBarCode(barCodeModel.getScannedBarCodeNumber())) {
            updateBarCode(barCodeModel);
        } else {
            insertBarCode(barCodeModel);
        }
    }

    public void deleteBarCode(BarCodeModel barCodeModel) {
        if (!existBarCode(barCodeModel.getScannedBarCodeNumber())) {
            return;
        }
        String rowId = barCodeModel.getScannedBarCodeNumber();
        database.delete(DBSQLiteHelper.BAR_CODE_TABLE, DBSQLiteHelper.BAR_CODE_NUMBER_COLUMN + " ='" + rowId + "'", null);
    }


    private void updateBarCode(BarCodeModel barCodeModel) {
        String rowId = barCodeModel.getScannedBarCodeNumber();
        ContentValues contentValues = getBarCodeContextValues(barCodeModel);
        database.update(DBSQLiteHelper.BAR_CODE_TABLE, contentValues, DBSQLiteHelper.BAR_CODE_NUMBER_COLUMN + " ='" + rowId + "'", null);
    }

    private void insertBarCode(BarCodeModel barCodeModel) {
        ContentValues contentValues = getBarCodeContextValues(barCodeModel);
        database.insertOrThrow(DBSQLiteHelper.BAR_CODE_TABLE, null, contentValues);
    }

    public List<BarCodeModel> loadBarCodeList() {
        List<BarCodeModel> barCodeModelList = new ArrayList<>();

        String sql = "SELECT " + DBSQLiteHelper.ID_BAR_CODE_COLUMN + "," +
                DBSQLiteHelper.BAR_CODE_ITEM_NAME_COLUMN + ","
                + DBSQLiteHelper.BAR_CODE_COUNT_COLUMN + "," +
                DBSQLiteHelper.BAR_CODE_NUMBER_COLUMN +
                " FROM " + DBSQLiteHelper.BAR_CODE_TABLE;

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                BarCodeModel barCodeDBCursorModel = cursorToBarCodeListData(cursor);
                if (barCodeDBCursorModel != null) {
                    barCodeModelList.add(barCodeDBCursorModel);
                }
                cursor.moveToNext();
            }

           // Collections.sort(barCodeDBModelList);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return barCodeModelList;
    }

    public BarCodeModel loadBarCodeListData(String id) {
        BarCodeModel barCodeModel = null;

        String sql = "SELECT " + DBSQLiteHelper.BAR_CODE_ITEM_NAME_COLUMN + "," +
                DBSQLiteHelper.BAR_CODE_COUNT_COLUMN + "," +
                DBSQLiteHelper.BAR_CODE_NUMBER_COLUMN + "," +
                " FROM " + DBSQLiteHelper.BAR_CODE_TABLE +
                " WHERE " + DBSQLiteHelper.ID_BAR_CODE_COLUMN + " = '" + id + "'";

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                barCodeModel = cursorToBarCodeListData(cursor);
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return barCodeModel;
    }

    private ContentValues getBarCodeContextValues(BarCodeModel barCodeModel) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBSQLiteHelper.ID_BAR_CODE_COLUMN, barCodeModel.getId());
        contentValues.put(DBSQLiteHelper.BAR_CODE_ITEM_NAME_COLUMN, barCodeModel.getName());
        contentValues.put(DBSQLiteHelper.BAR_CODE_COUNT_COLUMN, barCodeModel.getCountBarCode());
        contentValues.put(DBSQLiteHelper.BAR_CODE_NUMBER_COLUMN, barCodeModel.getScannedBarCodeNumber());

        return contentValues;
    }

    @SuppressLint("Range")
    public BarCodeModel cursorToBarCodeListData(Cursor cursor) {
        BarCodeModel model = new BarCodeModel();

        model.setId(cursor.getInt(cursor.getColumnIndex(DBSQLiteHelper.ID_BAR_CODE_COLUMN)));
        model.setCountBarCode(cursor.getInt(cursor.getColumnIndex(DBSQLiteHelper.BAR_CODE_COUNT_COLUMN)));
        model.setName(cursor.getString(cursor.getColumnIndex(DBSQLiteHelper.BAR_CODE_ITEM_NAME_COLUMN)));
        model.setScannedBarCodeNumber(cursor.getString(cursor.getColumnIndex(DBSQLiteHelper.BAR_CODE_NUMBER_COLUMN)));

        return model;
    }

}