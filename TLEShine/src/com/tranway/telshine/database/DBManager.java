package com.tranway.telshine.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.tranway.tleshine.model.ActivityInfo;
import com.tranway.tleshine.model.DailyExercise;
import com.tranway.tleshine.model.MyApplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
	public long addDailyExerciseInfo(DailyExercise exercise) {
		if (exercise == null || db == null) {
			return -1;
		}

		long ret = 0;
		db.beginTransaction();
		try {
			ContentValues mValues = new ContentValues();
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
	public long addDailyExerciseInfo(ArrayList<DailyExercise> exList) {
		if (exList == null || db == null) {
			return -1;
		}

		long ret = 0;
		db.beginTransaction();
		try {
			ContentValues mValues = new ContentValues();
			for (DailyExercise exercise : exList) {
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
	public ArrayList<DailyExercise> queryDailyExerciseInfo() {
		if (db == null) {
			return null;
		}

		db.beginTransaction();
		ArrayList<DailyExercise> exList = new ArrayList<DailyExercise>();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_DAILY_EXERCISE + " order by "
					+ DBInfo.EXERCISE_DATE + " DESC", new String[] {});
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
	public ArrayList<DailyExercise> queryDailyExerciseInfo(long fromDate) {
		if (db == null) {
			return null;
		}

		if (fromDate < 0) {
			return queryDailyExerciseInfo();
		}

		db.beginTransaction();
		ArrayList<DailyExercise> exList = new ArrayList<DailyExercise>();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_DAILY_EXERCISE + " where "
					+ DBInfo.EXERCISE_DATE + " > ? " + " order by " + DBInfo.EXERCISE_DATE
					+ " DESC", new String[] { String.valueOf(fromDate) });
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
	public ArrayList<DailyExercise> queryDailyExerciseInfo(long fromDate, long toDate) {
		if (db == null) {
			return null;
		}

		if (fromDate < 0 && toDate < 0) {
			return queryDailyExerciseInfo();
		} else if (fromDate > 0 && toDate < 0) {
			return queryDailyExerciseInfo(fromDate);
		} else if (fromDate < 0 && toDate > 0) {
			fromDate = 0;
		}

		db.beginTransaction();
		ArrayList<DailyExercise> exList = new ArrayList<DailyExercise>();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_DAILY_EXERCISE + " where "
					+ DBInfo.EXERCISE_DATE + " > ? and " + DBInfo.EXERCISE_DATE + " < ? "
					+ " order by " + DBInfo.EXERCISE_DATE + " DESC",
					new String[] { String.valueOf(fromDate), String.valueOf(toDate) });
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

	public static long addActivityInfo(ActivityInfo activityInfo) {
		DBActivityInfoHelper helper = new DBActivityInfoHelper(MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		if (activityInfo == null || db == null) {
			return -1;
		}

		long ret = 0;
		db.beginTransaction();
		try {
			ContentValues mValues = new ContentValues();
			mValues.put(DBActivityInfoHelper.KEY_UTC_TIME, activityInfo.getUtcTime());
			mValues.put(DBActivityInfoHelper.KEY_STEPS, activityInfo.getSteps());
			mValues.put(DBActivityInfoHelper.KEY_DISTANCE, activityInfo.getDistance());
			mValues.put(DBActivityInfoHelper.KEY_CALORIE, activityInfo.getCalorie());
			ret = db.replace(DBActivityInfoHelper.TABLE_NAME, null, mValues);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
		return ret;
	}

	public static List<ActivityInfo> queryActivityInfo() {
		DBActivityInfoHelper helper = new DBActivityInfoHelper(MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		List<ActivityInfo> activityInfos = new ArrayList<ActivityInfo>();
		if (db == null) {
			return activityInfos;
		}

		db.beginTransaction();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBActivityInfoHelper.TABLE_NAME
					+ " order by " + DBActivityInfoHelper.KEY_UTC_TIME + " ASC", null);

			while (cursor.moveToNext()) {
				ActivityInfo info = new ActivityInfo();
				info.setUtcTime(cursor.getLong(cursor
						.getColumnIndex(DBActivityInfoHelper.KEY_UTC_TIME)));
				info.setSteps(cursor.getInt(cursor.getColumnIndex(DBActivityInfoHelper.KEY_STEPS)));
				info.setDistance(cursor.getColumnIndex(DBActivityInfoHelper.KEY_DISTANCE));
				info.setCalorie(cursor.getInt(cursor
						.getColumnIndex(DBActivityInfoHelper.KEY_CALORIE)));
				activityInfos.add(info);
			}
			cursor.close();
		} finally {
			db.endTransaction();
		}
		db.close();
		return activityInfos;
	}

	public static long addEvery15MinData(Map<String, Object> every15MinData) {
		DBEvery15MinPacketHelper helper = new DBEvery15MinPacketHelper(
				MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		if (every15MinData == null || every15MinData.size() != 3 || db == null) {
			return -1;
		}

		long ret = 0;
		db.beginTransaction();
		try {
			ContentValues mValues = new ContentValues();
			if (every15MinData.containsKey(DBEvery15MinPacketHelper.KEY_UTC_TIME)) {
				mValues.put(DBEvery15MinPacketHelper.KEY_UTC_TIME,
						(Long) every15MinData.get(DBEvery15MinPacketHelper.KEY_UTC_TIME));
			}
			if (every15MinData.containsKey(DBEvery15MinPacketHelper.KEY_STEPS)) {
				mValues.put(DBEvery15MinPacketHelper.KEY_STEPS,
						(Integer) every15MinData.get(DBEvery15MinPacketHelper.KEY_STEPS));
			}
			if (every15MinData.containsKey(DBEvery15MinPacketHelper.KEY_CAOLRIE)) {
				mValues.put(DBEvery15MinPacketHelper.KEY_CAOLRIE,
						(Integer) every15MinData.get(DBEvery15MinPacketHelper.KEY_CAOLRIE));
			}
			ret = db.replace(DBEvery15MinPacketHelper.TABLE_NAME, null, mValues);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
		return ret;
	}

	public static List<Map<String, Object>> queryEvery15MinPackets(long fromUTC, long toUTC) {
		DBEvery15MinPacketHelper helper = new DBEvery15MinPacketHelper(
				MyApplication.getAppContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		List<Map<String, Object>> every15MinPackets = new ArrayList<Map<String, Object>>();
		if (db == null) {
			return every15MinPackets;
		}

		db.beginTransaction();
		try {
			Cursor cursor = db.rawQuery("select * from " + DBEvery15MinPacketHelper.TABLE_NAME
					+ " where " + DBEvery15MinPacketHelper.KEY_UTC_TIME + " > ? and "
					+ DBEvery15MinPacketHelper.KEY_UTC_TIME + " < ? " + " order by "
					+ DBEvery15MinPacketHelper.KEY_UTC_TIME + " DESC",
					new String[] { String.valueOf(fromUTC), String.valueOf(toUTC) });

			while (cursor.moveToNext()) {
				Map<String, Object> data = new TreeMap<String, Object>();
				data.put(DBEvery15MinPacketHelper.KEY_UTC_TIME, cursor.getLong(cursor
						.getColumnIndex(DBEvery15MinPacketHelper.KEY_UTC_TIME)));
				data.put(DBEvery15MinPacketHelper.KEY_STEPS,
						cursor.getInt(cursor.getColumnIndex(DBEvery15MinPacketHelper.KEY_STEPS)));
				data.put(DBEvery15MinPacketHelper.KEY_CAOLRIE,
						cursor.getInt(cursor.getColumnIndex(DBEvery15MinPacketHelper.KEY_CAOLRIE)));
				every15MinPackets.add(data);
			}
			cursor.close();
		} finally {
			db.endTransaction();
		}
		db.close();
		return every15MinPackets;
	}
}
