package com.example.notifications;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String USAGE_TABLE = "USAGE_TABLE";
    public static final String USAGE_DATE= "USAGE_DATE";
    public static final String USAGE_MILLIS= "USAGE_TIME";
    public static final String COLUMN_ID = "ID";

    public DBHelper(@Nullable Context context) {
        super(context, "usage.db", null, 1);
    }

    // called first time a db object is accessed
    // creates the db
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableStatement = "CREATE TABLE " + USAGE_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USAGE_DATE + " TEXT, " + USAGE_MILLIS + " INT)";

        db.execSQL(createTableStatement);

    }

    // called if DB version number changes
    // prevents apps from breaking when db changes
    // forward compatability - not worrying about this for now
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addOne(UsageDBSchema usageDB){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USAGE_DATE, usageDB.getDate());
        cv.put(USAGE_MILLIS, usageDB.getUsageInMillis());

        long insert = db.insert(USAGE_TABLE, null, cv);

        db.close();

        if (insert == -1) {
            return false;
        }
        return true;

    }

    public List<UsageDBSchema> getAllUsage() {

        List<UsageDBSchema> returnList = new ArrayList<>();

        // get data from DB

        String queryString = "SELECT * FROM " + USAGE_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {

            // loop through all the results

            do {

                int usageID = cursor.getInt(0);
                String usageDate = cursor.getString(1);
                int usageTime = cursor.getInt(2);

                UsageDBSchema newUsage = new UsageDBSchema(usageID, usageDate, usageTime);
                returnList.add(newUsage);

            } while (cursor.moveToNext());

        }

        else {

            //do nothing

        }

        cursor.close();
        db.close();

        return returnList;

    }

    public void clearDatabase() {


        SQLiteDatabase db = this.getReadableDatabase();
        String clearDBQuery = "DELETE FROM " + USAGE_TABLE;
        db.execSQL(clearDBQuery);
        db.close();
    }

}
