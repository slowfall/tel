package com.tranway.tleshine.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserInfoKeeper {

	private static final String PREFERENCES_NAME = "com_tle_user_info";

	public static final String KEY_EMAIL = "email";
	public static final String KEY_PWD = "password";
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
		edit.putString(KEY_EMAIL, info.getEmail());
		edit.putString(KEY_PWD, info.getPassword());
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
	public static boolean writeUserinfo(Context context, String key, String value) {
		if (null == context || null == key || null == value) {
			return false;
		}

		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor edit = preferences.edit();
		edit.putString(key, value);

		return edit.commit();
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
		info.setPassword(sp.getString(KEY_PWD, ""));
		info.setWeight(sp.getInt(KEY_WEIGHT, 0));
		info.setAge(sp.getInt(KEY_AGE, 0));
		info.setHeight(sp.getInt(KEY_HEIGHT, 0));
		info.setStride(sp.getInt(KEY_STRIDE, 0));
		info.setSex(sp.getInt(KEY_SEX, 0));
		info.setStepsTarget(sp.getInt(KEY_STEPSTARGET, 0));

		return info;
	}

}
