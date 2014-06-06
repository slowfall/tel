package com.tranway.telshine.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBActivityInfoHelper extends SQLiteOpenHelper {
	private static int DB_VERSION = 1;
	private static String DB_NAME = "db_activity_info";
	public static String TABLE_NAME = "activityinfos";
	public static String KEY_UTC_TIME = "utc_time";
	public static String KEY_STEPS = "steps";
	public static String KEY_DISTANCE = "distance";
	public static String KEY_CALORIE = "calorie";

	public DBActivityInfoHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
				+ KEY_UTC_TIME + " LONG PRIMARY KEY," + KEY_STEPS + " INTEGER,"
				+ KEY_DISTANCE + " INTEGER," + KEY_CALORIE + " INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}
}
