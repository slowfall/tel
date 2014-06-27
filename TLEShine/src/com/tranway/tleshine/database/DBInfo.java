package com.tranway.tleshine.database;

public class DBInfo {
	public static final int DB_VERSION = 1;
	// database name
	public static final String DB_NAME = "db_telshine";
	// daily exercise table
	public static final String TB_DAILY_EXERCISE = "tb_daily_exercise";
	public static String TB_EVERY_FIFTEEN_MIN = "tb_every_fifteen_min";
	public static String TB_ACTIVITY_INFO = "tb_activity_info";
	public static String TB_SLEEP_INFO = "tb_sleep_info";
	
	// exercise information attribute name in database table
	public static final String USER_ID = "use_id";
	public static final String EXERCISE_DATE = "date";
	public static final String EXERCISE_GOAL = "goal_point";
	public static final String EXERCISE_ACHIEVE = "achieve_point";
	/**
	 * unit is second
	 */
	public static String KEY_UTC_TIME = "utc_time";
	public static String KEY_STEPS = "steps";
	public static String KEY_DISTANCE = "distance";
	public static String KEY_CALORIE = "calorie";
	/**
	 * unit is second
	 */
	public static String KEY_SLEEP_GOAL = "sleep_goal";
	/**
	 * unit is second
	 */
	public static final String KEY_SLEEP_SHALLOW_TIME = "sleep_shallow_time";
	/**
	 * unit is second
	 */
	public static final String KEY_SLEEP_DEEP_TIME = "sleep_deep_time";
	public static final String KEY_SLEEP_PACKET = "sleep_packet";
}
