package com.tranway.telshine.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// private static final String TAG = "DbHelper";

	public DBHelper(Context context) {
		super(context, DBInfo.DB_NAME, null, DBInfo.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DBInfo.TB_DAILY_EXERCISE
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ DBInfo.EXERCISE_DATE +" LONG,"
				+ DBInfo.EXERCISE_GOAL + " INTEGER,"
				+ DBInfo.EXERCISE_ACHIEVE + " INTEGER)"
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}
}
