package com.tranway.tleshine.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserInfoKeeper {

	private static final String PREFERENCES_NAME = "com_tle_user_info";

	public static final String KEY_ID = "userId";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PWD = "password";
	public static final String KEY_BIRTH = "birthday";
	public static final String KEY_WEIGHT = "weight";
	public static final String KEY_AGE = "age";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_STRIDE = "stride";
	public static final String KEY_SEX = "sex";
	public static final String KEY_STEPSTARGET = "stepsTarget";

	/**
	 * write user information to SharedPreferences
	 * 
	 * @param context
	 *            application context
	 * @param info
	 *            user information
	 * @return return true if succeed, else return false
	 */
	public static boolean writeUserInfo(Context context, UserInfo info) {
		if (null == context || null == info) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putLong(KEY_ID, info.getId());
		edit.putString(KEY_EMAIL, info.getEmail());
		edit.putString(KEY_PWD, info.getPassword());
		edit.putLong(KEY_BIRTH, info.getBirthday());
		edit.putInt(KEY_WEIGHT, info.getWeight());
		edit.putInt(KEY_AGE, info.getAge());
		edit.putInt(KEY_HEIGHT, info.getHeight());
		edit.putInt(KEY_STRIDE, info.getStride());
		edit.putInt(KEY_SEX, info.getSex());
		edit.putInt(KEY_STEPSTARGET, info.getStepsTarget());

		return edit.commit();
	}

	/**
	 * write user information by SharedPreferences key
	 * 
	 * @param context
	 *            application context
	 * @param key
	 *            SharedPreferences key
	 * @param value
	 *            write value
	 * @return return true if succeed, else return false
	 */
	public static boolean writeUserInfo(Context context, String key, String value) {
		if (null == context || null == key || null == value) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putString(key, value);

		return edit.commit();
	}

	/**
	 * write user information by SharedPreferences key
	 * 
	 * @param context
	 *            application context
	 * @param key
	 *            SharedPreferences key
	 * @param value
	 *            write value
	 * @return return true if succeed, else return false
	 */
	public static boolean writeUserInfo(Context context, String key, int value) {
		if (null == context || null == key) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putInt(key, value);

		return edit.commit();
	}
	
	/**
	 * write user information by SharedPreferences key
	 * 
	 * @param context
	 *            application context
	 * @param key
	 *            SharedPreferences key
	 * @param value
	 *            write value
	 * @return return true if succeed, else return false
	 */
	public static boolean writeUserInfo(Context context, String key, long value) {
		if (null == context || null == key) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putLong(key, value);

		return edit.commit();
	}
	
	public static int readUserInfo(Context context, String key) {
		int value = 0;
		if (context == null || key == null) {
			return value;
		}
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		value = preferences.getInt(key, 0);
		return value;
	}

	/**
	 * read user information from SharedPreferences
	 * 
	 * @param context
	 *            application context
	 * @return return user information or null
	 */
	public static UserInfo readUserInfo(Context context) {
		if (null == context) {
			return null;
		}

		UserInfo info = new UserInfo();
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		info.setEmail(sp.getString(KEY_EMAIL, ""));
		info.setId(sp.getLong(KEY_ID, -1));
		info.setPassword(sp.getString(KEY_PWD, ""));
		info.setBirthday(sp.getLong(KEY_BIRTH, -1));
		info.setWeight(sp.getInt(KEY_WEIGHT, -1));
		info.setAge(sp.getInt(KEY_AGE, -1));
		info.setHeight(sp.getInt(KEY_HEIGHT, -1));
		info.setStride(sp.getInt(KEY_STRIDE, -1));
		info.setSex(sp.getInt(KEY_SEX, -1));
		info.setStepsTarget(sp.getInt(KEY_STEPSTARGET, -1));
		info.setGoal(UserGoalKeeper.readExerciseGoalPoint(context));

		return info;
	}

	/**
	 * clear user information SharedPreferences
	 * 
	 * @param context
	 * 
	 * @return return true if succeed, else return false
	 */
	public static boolean clearUserInfo(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.clear();
		return edit.commit();
	}
}
