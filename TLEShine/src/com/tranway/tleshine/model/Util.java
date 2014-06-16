package com.tranway.tleshine.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Util {
	private static final String SHARE_PREFERENCES_NAME = "tle.preferences";

	/**
	 * @param level
	 *            level can be Log.INFO, Log.DEBUG, Log.VERBOSE, Log.WARN or
	 *            Log.ERROR
	 * @param tag
	 *            log tag
	 * @param msg
	 *            log message
	 */
	private static void log(int level, String tag, String msg) {
		int key = level;
		switch (key) {
		case Log.VERBOSE:
			Log.v(tag, msg);
			break;
		case Log.DEBUG:
			Log.d(tag, msg);
			break;
		case Log.INFO:
			Log.i(tag, msg);
			break;
		case Log.WARN:
			Log.w(tag, msg);
			break;
		case Log.ERROR:
			Log.e(tag, msg);
			break;
		default:
			break;
		}
	}

	public static void logV(String tag, String msg) {
		log(Log.VERBOSE, tag, msg);
	}

	public static void logD(String tag, String msg) {
		log(Log.DEBUG, tag, msg);
	}

	public static void logI(String tag, String msg) {
		log(Log.INFO, tag, msg);
	}

	public static void logW(String tag, String msg) {
		log(Log.WARN, tag, msg);
	}

	public static void logE(String tag, String msg) {
		log(Log.ERROR, tag, msg);
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static boolean readPreferences(Context context, String key, boolean defValue) {
		if (context == null || key == null) {
			return defValue;
		}
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		return preferences.getBoolean(key, defValue);
	}

	public static void writePreferences(Context context, String key, boolean value) {
		if (context == null || key == null) {
			return;
		}
		SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	

	public static List<byte[]> getTestBytesList() {
		byte[] info = new byte[] { (byte) 0x04, (byte) 0x01, (byte) 0x54, (byte) 0x43, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0xFF, (byte) 0x1D, (byte) 0xBC,
				(byte) 0x5A, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x20,
				(byte) 0x20, (byte) 0x20, (byte) 0x20 };
		byte[] info2 = new byte[] { (byte) 0x04, (byte) 0x02, (byte) 0x54, 0x20 };
		byte[] info3 = new byte[] { (byte) 0x04, (byte) 0x03, (byte) 0x54,(byte) 0x20 };
		List<byte[]> list = new ArrayList<byte[]>();
		list.add(info);
		list.add(info2);
		list.add(info);
		list.add(info2);
		list.add(info);
		list.add(info2);
		list.add(info);
		list.add(info2);
		list.add(info);
		list.add(info2);
		list.add(info);
		list.add(info2);
		list.add(info);
		list.add(info2);
		list.add(info3);
		return list;
	}
}
