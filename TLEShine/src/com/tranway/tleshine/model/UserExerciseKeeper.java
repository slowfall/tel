package com.tranway.tleshine.model;

import com.tranway.tleshine.model.Exercise.Intensity;
import com.tranway.tleshine.model.Exercise.Sport;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserExerciseKeeper {

	private static final String PREFERENCES_NAME = "com_tle_user_exercise";

	public static final String KEY_INTENSITY = "intensity";
	public static final String KEY_SPORT = "sport";

	/**
	 * write user setting exercise to SharedPreferences
	 * 
	 * @param context
	 * @param exercise
	 * @return return true if succeed, else return false
	 */
	public static boolean writeExerciseGoal(Context context, Exercise exercise) {
		if (null == context || null == exercise) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putInt(KEY_INTENSITY, exercise.getIntensity().getId());
		edit.putInt(KEY_SPORT, exercise.getMethod().getId());

		return edit.commit();
	}

	/**
	 * write user setting exercise intensity to SharedPreferences
	 * 
	 * @param context
	 * @param intensity
	 * @return return true if succeed, else return false
	 */
	public static boolean writeExercisIntensity(Context context, Intensity intensity) {
		if (null == context || null == intensity) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putInt(KEY_INTENSITY, intensity.getId());

		return edit.commit();
	}

	/**
	 * read user information from SharedPreferences
	 * 
	 * @param context
	 *            application context
	 * @return return user information or null
	 */
	public static Exercise readExerciseGoal(Context context) {
		if (null == context) {
			return null;
		}

		Exercise exercise = new Exercise();
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		int intensity = sp.getInt(KEY_INTENSITY, -1);
		exercise.setIntensity(Intensity.getIntensityById(intensity));
		int method = sp.getInt(KEY_SPORT, -1);
		exercise.setMethod(Sport.getSportById(method));

		return exercise;
	}

}
