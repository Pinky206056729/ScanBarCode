package com.siphokazimatsheke.scanstuff.datasource;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BarCodeScanner.db";
    private static final int DATABASE_VERSION = 1;

    //tables
    public static final String BAR_CODE_TABLE = "BarCode";

    public static final String ID_BAR_CODE_COLUMN = "id";
    public static final String BAR_CODE_NUMBER_COLUMN = "barCodeNumber";
    public static final String BAR_CODE_COUNT_COLUMN = "barCodeCount";
    public static final String BAR_CODE_ITEM_NAME_COLUMN = "barCodeItemName";

    private static final String BAR_CODE_TABLE_CREATE = "create table "
            + BAR_CODE_TABLE + "("
            + BAR_CODE_NUMBER_COLUMN + " text PRIMARY KEY NOT NULL, "
            + ID_BAR_CODE_COLUMN + " INTEGER , "
            + BAR_CODE_COUNT_COLUMN + " integer, "
            + BAR_CODE_ITEM_NAME_COLUMN  + " text);";

    public DBSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(BAR_CODE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
