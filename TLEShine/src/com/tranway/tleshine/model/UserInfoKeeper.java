package com.tranway.tleshine.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserInfoKeeper {

	private static final String PREFERENCES_NAME = "com_tle_user_info";

	public static final String KEY_ID = "userId";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_NAME = "userName";
	public static final String KEY_PWD = "password";
	public static final String KEY_PHONE = "Phone";
	public static final String KEY_BIRTH = "birthday";
	public static final String KEY_WEIGHT = "weight";
	public static final String KEY_AGE = "age";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_STRIDE = "stride";
	public static final String KEY_SEX = "sex";
	public static final String KEY_STEPSTARGET = "stepsTarget";
	public static final String KEY_CONTINUE_COUNT = "continueCount";
	public static final String KEY_MAX_POINT = "maxPoint";
	public static final String KEY_GOAL_COUNT = "goalCount";
	public static final String KEY_KEEP_SIGN_IN = "keepSignIn";

	/**
	 * unit is milliseconds
	 */
	public static final String KEY_SYNC_BLUETOOTH_TIME = "syncBluetoothTime";
	public static final String KEY_GEN_CODE = "genCode";
	public static final String KEY_GEN_CODE_GET_TIME = "genCodeGetTime";

	public static void writeUserInfo(Context context, JSONObject data) throws JSONException {
		UserInfo info = new UserInfo();
		info.setId(data.getLong(UserInfo.SEVER_KEY_ID));
		info.setEmail(data.getString(UserInfo.SEVER_KEY_EMAIL));
		if (!data.isNull(UserInfo.SEVER_KEY_NAME)) {
			info.setName(data.getString(UserInfo.SEVER_KEY_NAME));
		}
		info.setPassword(data.getString(UserInfo.SEVER_KEY_PASSWORD));
		info.setSex(data.getInt(UserInfo.SEVER_KEY_SEX));
		info.setBirthday(data.getLong(UserInfo.SEVER_KEY_BIRTHDAY));
		info.setHeight((int) (data.getDouble(UserInfo.SEVER_KEY_HEIGHT) * 100));
		info.setWeight((int) (data.getInt(UserInfo.SEVER_KEY_WEIGHT) * 10));
		if (data.isNull(UserInfo.SEVER_KEY_GOAL)) {
			info.setStepsTarget(0);
		} else {
			info.setStepsTarget(data.getInt(UserInfo.SEVER_KEY_GOAL));
		}
		if (data.isNull(UserInfo.SEVER_KEY_STEP_COUNT)) {
			info.setStride(readUserInfo(context, KEY_STRIDE, 0));
		} else {
			info.setStride(data.getInt(UserInfo.SEVER_KEY_STEP_COUNT));
		}
		writeUserInfo(context, info);
	}

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

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putLong(KEY_ID, info.getId());
		edit.putString(KEY_EMAIL, info.getEmail());
		edit.putString(KEY_NAME, info.getName());
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

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
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

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
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

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putLong(key, value);

		return edit.commit();
	}

	public static boolean writeUserInfo(Context context, String key, boolean value) {
		if (null == context || null == key) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putBoolean(key, value);

		return edit.commit();
	}

	/**
	 * read user information from SharedPreferences by key
	 * 
	 * @param context
	 * @param key
	 *            key of value
	 * @param defaultValue
	 *            default value if read key is not exist
	 * 
	 * @return return value or default value
	 */
	public static int readUserInfo(Context context, String key, int defaultValue) {
		if (context == null || key == null) {
			return defaultValue;
		}
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		defaultValue = preferences.getInt(key, defaultValue);
		return defaultValue;
	}

	/**
	 * read user information from SharedPreferences by key
	 * 
	 * @param context
	 * @param key
	 * @param defaultValue
	 * @return return value or default value
	 */
	public static String readUserInfo(Context context, String key, String defaultValue) {
		if (context == null || key == null) {
			return defaultValue;
		}
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		defaultValue = preferences.getString(key, defaultValue);
		return defaultValue;
	}

	public static boolean readUserInfo(Context context, String key, boolean defaultValue) {
		if (context == null || key == null) {
			return defaultValue;
		}
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		defaultValue = preferences.getBoolean(key, defaultValue);
		return defaultValue;
	}

	/**
	 * read user information from SharedPreferences by key
	 * 
	 * @param context
	 * @param key
	 * @param defaultValue
	 * @return return value or default value
	 */
	public static long readUserInfo(Context context, String key, long defaultValue) {
		if (context == null || key == null) {
			return defaultValue;
		}
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		defaultValue = preferences.getLong(key, defaultValue);
		return defaultValue;
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
		info.setName(sp.getString(KEY_NAME, ""));
		info.setId(sp.getLong(KEY_ID, -1));
		info.setPassword(sp.getString(KEY_PWD, ""));
		info.setPhone(sp.getString(KEY_PHONE, ""));
		info.setBirthday(sp.getLong(KEY_BIRTH, -1));
		info.setWeight(sp.getInt(KEY_WEIGHT, -1));
		info.setAge(sp.getInt(KEY_AGE, -1));
		info.setHeight(sp.getInt(KEY_HEIGHT, -1));
		info.setStride(sp.getInt(KEY_STRIDE, -1));
		info.setSex(sp.getInt(KEY_SEX, -1));
		info.setStepsTarget(sp.getInt(KEY_STEPSTARGET, -1));
		// info.setGoal(UserGoalKeeper.readExerciseGoalPoint(context));

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
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.clear();
		return edit.commit();
	}
}
