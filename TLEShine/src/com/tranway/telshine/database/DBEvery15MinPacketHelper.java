package com.tranway.telshine.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBEvery15MinPacketHelper extends SQLiteOpenHelper {
	private static int DB_VERSION = 1;
	public static String TABLE_NAME = "every_fifteen_min";
	public static String KEY_UTC_TIME = "utc_time";
	public static String KEY_STEPS = "steps";
	public static String KEY_CAOLRIE = "calorie";

	public DBEvery15MinPacketHelper(Context context) {
		super(context, DBInfo.DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + KEY_UTC_TIME
				+ " LONG PRIMARY KEY," + KEY_STEPS + " INTEGER," + KEY_CAOLRIE + " INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}
}
