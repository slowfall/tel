package com.tranway.telshine.database;

import java.util.ArrayList;

import com.tranway.tleshine.model.DailyExercise;

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
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_DAILY_EXERCISE + " where " + DBInfo.EXERCISE_DATE
					+ " > ? " + " order by " + DBInfo.EXERCISE_DATE + " DESC",
					new String[] { String.valueOf(fromDate) });
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
			Cursor cursor = db.rawQuery("select * from " + DBInfo.TB_DAILY_EXERCISE + " where " + DBInfo.EXERCISE_DATE
					+ " > ? and " + DBInfo.EXERCISE_DATE + " < ? " + " order by " + DBInfo.EXERCISE_DATE + " DESC",
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
}
