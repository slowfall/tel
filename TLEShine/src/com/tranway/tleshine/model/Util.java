package com.tranway.tleshine.model;

import android.util.Log;

public class Util {
	/**
	 * @param level level can be Log.INFO, Log.DEBUG, Log.VERBOSE, Log.WARN or Log.ERROR
	 * @param tag log tag
	 * @param msg log message
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
}
