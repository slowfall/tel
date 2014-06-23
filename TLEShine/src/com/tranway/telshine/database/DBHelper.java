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
		// create daily exercise table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DBInfo.TB_DAILY_EXERCISE
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + DBInfo.USER_ID + " LONG,"
				+ DBInfo.EXERCISE_DATE + " LONG," + DBInfo.EXERCISE_GOAL + " INTEGER,"
				+ DBInfo.EXERCISE_ACHIEVE + " INTEGER)");
		// create activity info table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DBInfo.TB_ACTIVITY_INFO + "("
				+ DBInfo.KEY_UTC_TIME + " LONG," + DBInfo.USER_ID + " LONG," + DBInfo.EXERCISE_GOAL
				+ " INTEGER," + DBInfo.KEY_STEPS + " INTEGER," + DBInfo.KEY_DISTANCE + " INTEGER,"
				+ DBInfo.KEY_CALORIE + " INTEGER, PRIMARY KEY (" + DBInfo.KEY_UTC_TIME + ", "
				+ DBInfo.USER_ID + "))");
		// create every 15min table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DBInfo.TB_EVERY_FIFTEEN_MIN + "("
				+ DBInfo.KEY_UTC_TIME + " LONG," + DBInfo.USER_ID + " LONG," + DBInfo.KEY_STEPS
				+ " INTEGER," + DBInfo.KEY_CALORIE + " INTEGER, PRIMARY KEY ("
				+ DBInfo.KEY_UTC_TIME + ", " + DBInfo.USER_ID + "))");
		// create sleep data table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DBInfo.TB_SLEEP_INFO + "(" + DBInfo.KEY_UTC_TIME
				+ " LONG," + DBInfo.USER_ID + " LONG," + DBInfo.KEY_SLEEP_GOAL + " LONG,"
				+ DBInfo.KEY_SLEEP_SHALLOW_TIME + " LONG," + DBInfo.KEY_SLEEP_DEEP_TIME
				+ " LONG,  PRIMARY KEY (" + DBInfo.KEY_UTC_TIME + ", " + DBInfo.USER_ID + "))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}
}
