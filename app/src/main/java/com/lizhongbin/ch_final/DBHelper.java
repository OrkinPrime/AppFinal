package com.lizhongbin.ch_final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "record.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "records";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SCORES = "scores";
    private static final String COLUMN_PLAYER_NAME = "playerName";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'", null);
        boolean tableExists = (cursor != null && cursor.getCount() > 0);
        cursor.close();

        if (!tableExists) {
            // 表不存在，创建新表并插入测试数据
            String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_SCORES + " INTEGER,"
                    + COLUMN_PLAYER_NAME + " TEXT" + ")";
            db.execSQL(CREATE_TABLE);

            ContentValues values = new ContentValues();
            values.put(COLUMN_SCORES, "100");
            values.put(COLUMN_PLAYER_NAME, "测试数据1");
            db.insert(TABLE_NAME, null, values);

            values.clear();
            values.put(COLUMN_SCORES, "200");
            values.put(COLUMN_PLAYER_NAME, "测试数据2");
            db.insert(TABLE_NAME, null, values);

            values.clear();
            values.put(COLUMN_SCORES, "400");
            values.put(COLUMN_PLAYER_NAME, "测试数据3");
            db.insert(TABLE_NAME, null, values);

            values.clear();
            values.put(COLUMN_SCORES, "300");
            values.put(COLUMN_PLAYER_NAME, "测试数据4");
            db.insert(TABLE_NAME, null, values);
        } else {
            // 表已存在，调用onUpgrade()来清空和重新填充数据
            onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION + 1);
            // 注意：在这里我们实际上并没有改变DATABASE_VERSION，
            // 这只是为了触发onUpgrade()方法，实际的数据库版本并没有改变。
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void addRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORES, record.getScores());
        values.put(COLUMN_PLAYER_NAME, record.getPlayerName());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Record> getAllRecords() {
        List<Record> recordsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_SCORES + " DESC"+" LIMIT 10";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Record record = new Record();
                record.setId(Integer.parseInt(cursor.getString(0)));
                record.setScores(Integer.parseInt(cursor.getString(1)));
                record.setPlayerName(cursor.getString(2));
                recordsList.add(record);
            } while (cursor.moveToNext());
        }
        return recordsList;
    }
}
