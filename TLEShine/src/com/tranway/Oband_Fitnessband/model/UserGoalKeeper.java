package com.tranway.Oband_Fitnessband.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserGoalKeeper {

	private static final String PREFERENCES_NAME = "com_tle_user_goal";

	public static final String KEY_GOAL_POINT = "goal_exercise_point";
	public static final String KEY_GOAL_TIME = "goal_sleep_time";
	public static final String KEY_GOAL_TIME_RANGE = "goal_sleep_time_range";

	// /**
	// * write user setting exercise goal point to SharedPreferences
	// *
	// * @param context
	// * context
	// * @param point
	// * setting goal point
	// * @return return true if complete, else return false
	// */
	// public static boolean writeExerciseGoal(Context context, int point) {
	// if (null == context || point <= 0) {
	// return false;
	// }
	//
	// SharedPreferences preferences =
	// context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
	// Editor edit = preferences.edit();
	// edit.putInt(KEY_GOAL_POINT, point);
	//
	// return edit.commit();
	// }
	//
	// /**
	// * read user settings exercise goal from SharedPreferences
	// *
	// * @param context
	// * application context
	// * @return return user settings goal or 0
	// */
	// public static int readExerciseGoalPoint(Context context) {
	// if (null == context) {
	// return -1;
	// }
	//
	// SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME,
	// Context.MODE_APPEND);
	// return sp.getInt(KEY_GOAL_POINT, 0);
	// }

	/**
	 * write user setting sleep goal time to SharedPreferences
	 * 
	 * @param context
	 *            context
	 * @param time
	 *            setting goal time
	 * @return return true if complete, else return false
	 */
	public static boolean writeSleepGoal(Context context, int time) {
		if (null == context || time <= 0) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putInt(KEY_GOAL_TIME, time);

		return edit.commit();
	}

	/**
	 * read user settings exercise goal range from SharedPreferences
	 * 
	 * @param context
	 *            application context
	 * @return return user settings goal or 00:00-00:00
	 */
	public static String readSleepGoalTimeRange(Context context) {
		if (null == context) {
			return "00:00-00:00";
		}

		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		return sp.getString(KEY_GOAL_TIME_RANGE, "00:00-00:00");
	}

	/**
	 * write user setting sleep goal time range to SharedPreferences
	 * 
	 * @param context
	 *            context
	 * @param time
	 *            format setting time
	 * @return return true if complete, else return false
	 */
	public static boolean writeSleepGoalRange(Context context, String range) {
		if (null == context || range == null) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putString(KEY_GOAL_TIME_RANGE, range);

		return edit.commit();
	}

	/**
	 * read user settings sleep goal from SharedPreferences
	 * 
	 * @param context
	 *            application context
	 * @return return user settings goal or -1
	 */
	public static int readSleepGoalTime(Context context) {
		if (null == context) {
			return -1;
		}

		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		return sp.getInt(KEY_GOAL_TIME, -1);
	}

}
