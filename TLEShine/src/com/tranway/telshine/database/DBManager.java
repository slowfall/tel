package com.tranway.telshine.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tranway.tleshine.model.ActivityInfo;
import com.tranway.tleshine.model.DailyExercise;
import com.tranway.tleshine.model.MyApplication;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	/**
	 * add daily exercise information
	 * 
	 * @param exercise
	 *            daily exercise information
	 * 
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long addDailyExerciseInfo(long userId, DailyExercise exercise) {
		if (userId < 0 || exercise == null || db == null) {
			return -1;
		}

		long ret = 0;
		db.beginTransaction();
		try {
			ContentValues mValues = new ContentValues();
			mValues.put(DBInfo.USER_ID, userId);
			mValues.put(DBInfo.EXERCISE_DATE, exercise.getDate());
			mValues.put(DBInfo.EXERCISE_GOAL, exercise.getGoal());
			mValues.put(DBInfo.EXERCISE_ACHIEVE, exercise.getAchieve());
			ret = db.insert(DBInfo.TB_DAILY_EXERCISE, null, mValues);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return ret;
	}

	/**
	 * add daily exercise information list
	 * 
	 * @param exList
	 *            daily exercise information list
	 * 
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long addDailyExerciseInfo(long userId, ArrayList<DailyExercise> exList) {
		if (userId < 0 || exList == null || db == null) {
			return -1;
		}

		long ret = 0;
		db.beginTransaction();
		try {
			ContentValues mValues = new ContentValues();
			for (DailyExercise exercise : exList) {
				mValues.put(DBInfo.USER_ID, userId);
				mValues.put(DBInfo.EXERCISE_DATE, exercise.getDate());
				mValues.put(DBInfo.EXERCISE_GOAL, exercise.getGoal());
				mValues.put(DBInfo.EXERCISE_ACHIEVE, exercise.getAchieve());
				ret = db.insert(DBInfo.TB_DAILY_EXERCISE, null, mValues);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return ret;
	}

	/**
	 * query all daily exercise information list from database
	 * 
	 * @return return daily exercise list or null
	 * */
	public ArrayList<DailyExercise> queryDailyExerciseInfo(long userId) {
		if (db == null) {
			return null;
		}

		db.beginTransaction();
		ArrayList<DailyExercise> exList = new ArrayList<DailyExercise>();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_DAILY_EXERCISE + " where "
					+ DBInfo.USER_ID + "=? " + " order by " + DBInfo.EXERCISE_DATE + " DESC",
					new String[] { String.valueOf(userId) });
			while (cursor.moveToNext()) {
				DailyExercise ex = new DailyExercise();
				ex.setDate(cursor.getLong(cursor.getColumnIndex(DBInfo.EXERCISE_DATE)));
				ex.setGoal(cursor.getInt(cursor.getColumnIndex(DBInfo.EXERCISE_GOAL)));
				ex.setAchieve(cursor.getInt(cursor.getColumnIndex(DBInfo.EXERCISE_ACHIEVE)));
				exList.add(ex);
			}
			cursor.close();
		} finally {
			db.endTransaction();
		}
		return exList;
	}

	/**
	 * query all daily exercise information list from database
	 * 
	 * @param fromDate
	 *            query record from date conditions
	 * 
	 * @return return daily exercise list or null
	 * */
	public ArrayList<DailyExercise> queryDailyExerciseInfo(long userId, long fromDate) {
		if (userId < 0 || db == null) {
			return null;
		}

		if (fromDate < 0) {
			return queryDailyExerciseInfo(userId);
		}

		db.beginTransaction();
		ArrayList<DailyExercise> exList = new ArrayList<DailyExercise>();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_DAILY_EXERCISE + " where "
					+ DBInfo.USER_ID + "=?  and " + DBInfo.EXERCISE_DATE + " > ? " + " order by "
					+ DBInfo.EXERCISE_DATE + " DESC",
					new String[] { String.valueOf(userId), String.valueOf(fromDate) });
			while (cursor.moveToNext()) {
				DailyExercise ex = new DailyExercise();
				ex.setDate(cursor.getLong(cursor.getColumnIndex(DBInfo.EXERCISE_DATE)));
				ex.setGoal(cursor.getInt(cursor.getColumnIndex(DBInfo.EXERCISE_GOAL)));
				ex.setAchieve(cursor.getInt(cursor.getColumnIndex(DBInfo.EXERCISE_ACHIEVE)));
				exList.add(ex);
			}
			cursor.close();
		} finally {
			db.endTransaction();
		}
		return exList;
	}

	/**
	 * query all daily exercise information list from database
	 * 
	 * @param fromDate
	 *            query record from date conditions
	 * @param toDate
	 *            query record to date conditions
	 * 
	 * @return return daily exercise list or null
	 * */
	public ArrayList<DailyExercise> queryDailyExerciseInfo(long userId, long fromDate, long toDate) {
		if (userId < 0 || db == null) {
			return null;
		}

		if (fromDate < 0 && toDate < 0) {
			return queryDailyExerciseInfo(userId);
		} else if (fromDate > 0 && toDate < 0) {
			return queryDailyExerciseInfo(userId, fromDate);
		} else if (fromDate < 0 && toDate > 0) {
			fromDate = 0;
		}

		db.beginTransaction();
		ArrayList<DailyExercise> exList = new ArrayList<DailyExercise>();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_DAILY_EXERCISE + " where "
					+ DBInfo.USER_ID + "=?  and " + DBInfo.EXERCISE_DATE + " > ? and "
					+ DBInfo.EXERCISE_DATE + " < ? " + " order by " + DBInfo.EXERCISE_DATE
					+ " DESC", new String[] { String.valueOf(userId), String.valueOf(fromDate),
					String.valueOf(toDate) });
			while (cursor.moveToNext()) {
				DailyExercise ex = new DailyExercise();
				ex.setDate(cursor.getLong(cursor.getColumnIndex(DBInfo.EXERCISE_DATE)));
				ex.setGoal(cursor.getInt(cursor.getColumnIndex(DBInfo.EXERCISE_GOAL)));
				ex.setAchieve(cursor.getInt(cursor.getColumnIndex(DBInfo.EXERCISE_ACHIEVE)));
				exList.add(ex);
			}
			cursor.close();
		} finally {
			db.endTransaction();
		}
		return exList;
	}

	/**
	 * close database
	 */
	public void closeDB() {
		db.close();
	}

	synchronized public static long addActivityInfo(long userId, ActivityInfo activityInfo) {
		if (userId < 0 || activityInfo == null) {
			return -1;
		}
		DBHelper helper = new DBHelper(MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db == null) {
			return -1;
		}

		long ret = 0;
		db.beginTransaction();
		try {
			ContentValues mValues = new ContentValues();
			mValues.put(DBInfo.USER_ID, userId);
			mValues.put(DBInfo.EXERCISE_GOAL, activityInfo.getGoal());
			mValues.put(DBInfo.KEY_UTC_TIME, activityInfo.getUtcTime());
			mValues.put(DBInfo.KEY_STEPS, activityInfo.getSteps());
			mValues.put(DBInfo.KEY_DISTANCE, activityInfo.getDistance());
			mValues.put(DBInfo.KEY_CALORIE, activityInfo.getCalorie());
			ret = db.replace(DBInfo.TB_ACTIVITY_INFO, null, mValues);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
		return ret;
	}

	synchronized public static List<ActivityInfo> queryActivityInfo(long userId) {
		List<ActivityInfo> activityInfos = new ArrayList<ActivityInfo>();
		if (userId < 0) {
			return activityInfos;
		}
		DBHelper helper = new DBHelper(MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db == null) {
			return activityInfos;
		}

		db.beginTransaction();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_ACTIVITY_INFO + " where "
					+ DBInfo.USER_ID + "=? " + " order by " + DBInfo.KEY_UTC_TIME + " ASC",
					new String[] { String.valueOf(userId) });

			while (cursor.moveToNext()) {
				ActivityInfo info = new ActivityInfo();
				info.setUtcTime(cursor.getLong(cursor.getColumnIndex(DBInfo.KEY_UTC_TIME)));
				info.setGoal(cursor.getInt(cursor.getColumnIndex(DBInfo.EXERCISE_GOAL)));
				info.setSteps(cursor.getInt(cursor.getColumnIndex(DBInfo.KEY_STEPS)));
				info.setDistance(cursor.getColumnIndex(DBInfo.KEY_DISTANCE));
				info.setCalorie(cursor.getInt(cursor.getColumnIndex(DBInfo.KEY_CALORIE)));
				activityInfos.add(info);
			}
			cursor.close();
		} finally {
			db.endTransaction();
		}
		db.close();
		return activityInfos;
	}

	synchronized public static ActivityInfo queryActivityInfoByTime(long userId, long utcTime) {
		ActivityInfo activityInfo = new ActivityInfo();
		if (userId < 0) {
			return activityInfo;
		}
		DBHelper helper = new DBHelper(MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db == null) {
			return activityInfo;
		}

		db.beginTransaction();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_ACTIVITY_INFO + " where "
					+ DBInfo.USER_ID + "=? and " + DBInfo.KEY_UTC_TIME + "=? " + " order by "
					+ DBInfo.KEY_UTC_TIME + " ASC",
					new String[] { String.valueOf(userId), String.valueOf(utcTime) });

			while (cursor.moveToNext()) {
				activityInfo.setUtcTime(cursor.getLong(cursor.getColumnIndex(DBInfo.KEY_UTC_TIME)));
				activityInfo.setGoal(cursor.getInt(cursor.getColumnIndex(DBInfo.EXERCISE_GOAL)));
				activityInfo.setSteps(cursor.getInt(cursor.getColumnIndex(DBInfo.KEY_STEPS)));
				activityInfo.setDistance(cursor.getColumnIndex(DBInfo.KEY_DISTANCE));
				activityInfo.setCalorie(cursor.getInt(cursor.getColumnIndex(DBInfo.KEY_CALORIE)));
				break;
			}
			cursor.close();
		} finally {
			db.endTransaction();
		}
		db.close();

		return activityInfo;
	}

	synchronized public static long addEvery15MinData(long userId, List<Map<String, Object>> every15MinDatas) {
		if (userId < 0 || every15MinDatas == null || every15MinDatas.size() <= 0) {
			return -1;
		}
		DBHelper helper = new DBHelper(MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db == null) {
			return -1;
		}

		Calendar calendar = Calendar.getInstance();
		TimeZone zone = calendar.getTimeZone();
		long offset = zone.getOffset(calendar.getTimeInMillis()) / 1000;
		long ret = 0;
		db.beginTransaction();
		try {
			for (Map<String, Object> every15MinData : every15MinDatas) {
				ContentValues mValues = new ContentValues();
				mValues.put(DBInfo.USER_ID, userId);
				if (every15MinData.containsKey(DBInfo.KEY_UTC_TIME)) {
					mValues.put(DBInfo.KEY_UTC_TIME, (Long) every15MinData.get(DBInfo.KEY_UTC_TIME) - offset);
				}
				if (every15MinData.containsKey(DBInfo.KEY_STEPS)) {
					mValues.put(DBInfo.KEY_STEPS, (Integer) every15MinData.get(DBInfo.KEY_STEPS));
				}
				if (every15MinData.containsKey(DBInfo.KEY_CALORIE)) {
					mValues.put(DBInfo.KEY_CALORIE,
							(Integer) every15MinData.get(DBInfo.KEY_CALORIE));
				}
				ret = db.replace(DBInfo.TB_EVERY_FIFTEEN_MIN, null, mValues);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
		return ret;
	}

	synchronized public static List<Map<String, Object>> queryEvery15MinPackets(long userId, long fromUTC,
			long toUTC) {
		List<Map<String, Object>> every15MinPackets = new ArrayList<Map<String, Object>>();
		if (userId < 0 || fromUTC < 0 || toUTC < 0 || fromUTC > toUTC) {
			return every15MinPackets;
		}
		DBHelper helper = new DBHelper(MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db == null) {
			return every15MinPackets;
		}

//		fromUTC = 0;
//		toUTC = 100000000000l;
		db.beginTransaction();
		try {
			Cursor cursor = db.rawQuery(
					"select * from " + DBInfo.TB_EVERY_FIFTEEN_MIN + " where " + DBInfo.USER_ID
							+ "=? and " + DBInfo.KEY_UTC_TIME + " >= ? and " + DBInfo.KEY_UTC_TIME
							+ " < ? " + " order by " + DBInfo.KEY_UTC_TIME + " DESC",
					new String[] { String.valueOf(userId), String.valueOf(fromUTC),
							String.valueOf(toUTC) });

			while (cursor.moveToNext()) {
				Map<String, Object> data = new TreeMap<String, Object>();
				data.put(DBInfo.KEY_UTC_TIME,
						cursor.getLong(cursor.getColumnIndex(DBInfo.KEY_UTC_TIME)));
				data.put(DBInfo.KEY_STEPS, cursor.getInt(cursor.getColumnIndex(DBInfo.KEY_STEPS)));
				data.put(DBInfo.KEY_CALORIE,
						cursor.getInt(cursor.getColumnIndex(DBInfo.KEY_CALORIE)));
				every15MinPackets.add(data);
			}
			cursor.close();
		} finally {
			db.endTransaction();
		}
		db.close();
		return every15MinPackets;
	}

	synchronized public static long addSleepInfo(long userId, Map<String, Object> sleepData) {
		if (userId < 0 || sleepData == null || sleepData.size() <= 0) {
			return -1;
		}
		DBHelper helper = new DBHelper(MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db == null) {
			return -1;
		}
		Calendar calendar = Calendar.getInstance();
		TimeZone zone = calendar.getTimeZone();
		long offset = zone.getOffset(calendar.getTimeInMillis()) / 1000;
		long ret = 0;
		db.beginTransaction();
		try {
			ContentValues mValues = new ContentValues();
			mValues.put(DBInfo.USER_ID, userId);
			if (sleepData.containsKey(DBInfo.KEY_UTC_TIME)) {
				mValues.put(DBInfo.KEY_UTC_TIME, (Long) sleepData.get(DBInfo.KEY_UTC_TIME) - offset);
			}
			if (sleepData.containsKey(DBInfo.KEY_SLEEP_DEEP_TIME)) {
				mValues.put(DBInfo.KEY_SLEEP_DEEP_TIME, (Long) sleepData.get(DBInfo.KEY_SLEEP_GOAL));
			}
			if (sleepData.containsKey(DBInfo.KEY_SLEEP_DEEP_TIME)) {
				mValues.put(DBInfo.KEY_SLEEP_DEEP_TIME,
						(Long) sleepData.get(DBInfo.KEY_SLEEP_DEEP_TIME));
			}
			if (sleepData.containsKey(DBInfo.KEY_SLEEP_GOAL)) {
				mValues.put(DBInfo.KEY_SLEEP_GOAL, (Long) sleepData.get(DBInfo.KEY_SLEEP_GOAL));
			}
			if (sleepData.containsKey(DBInfo.KEY_SLEEP_SHALLOW_TIME)) {
				mValues.put(DBInfo.KEY_SLEEP_SHALLOW_TIME,
						(Long) sleepData.get(DBInfo.KEY_SLEEP_SHALLOW_TIME));
			}
			ret = db.replace(DBInfo.TB_SLEEP_INFO, null, mValues);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
		return ret;
	}

	synchronized public static List<Map<String, Object>> queryAllSleepInfos(long userId) {
		List<Map<String, Object>> sleepInfos = new ArrayList<Map<String, Object>>();
		if (userId < 0) {
			return sleepInfos;
		}
		DBHelper helper = new DBHelper(MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db == null) {
			return sleepInfos;
		}

		db.beginTransaction();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_SLEEP_INFO + " where "
					+ DBInfo.USER_ID + "=? " + " order by " + DBInfo.KEY_UTC_TIME + " ASC",
					new String[] { String.valueOf(userId) });

			while (cursor.moveToNext()) {
				Map<String, Object> data = new TreeMap<String, Object>();
				data.put(DBInfo.KEY_UTC_TIME,
						cursor.getLong(cursor.getColumnIndex(DBInfo.KEY_UTC_TIME)));
				if (cursor.getLong(cursor.getColumnIndex(DBInfo.KEY_SLEEP_GOAL)) != -1) {
				data.put(DBInfo.KEY_SLEEP_GOAL,
						cursor.getLong(cursor.getColumnIndex(DBInfo.KEY_SLEEP_GOAL)));
				} else {
					data.put(DBInfo.KEY_SLEEP_GOAL, 0);
				}
				data.put(DBInfo.KEY_SLEEP_DEEP_TIME,
						cursor.getLong(cursor.getColumnIndex(DBInfo.KEY_SLEEP_DEEP_TIME)));
				data.put(DBInfo.KEY_SLEEP_SHALLOW_TIME,
						cursor.getLong(cursor.getColumnIndex(DBInfo.KEY_SLEEP_SHALLOW_TIME)));
				sleepInfos.add(data);
			}
			cursor.close();
		} finally {
			db.endTransaction();
		}
		db.close();
		return sleepInfos;
	}
}
