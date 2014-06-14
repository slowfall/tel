package com.tranway.telshine.database;

public class DBInfo {
	public static final int DB_VERSION = 1;
	// database name
	public static final String DB_NAME = "db_telshine";
	// daily exercise table
	public static final String TB_DAILY_EXERCISE = "tb_daily_exercise";
	public static String TB_EVERY_FIFTEEN_MIN = "tb_every_fifteen_min";
	public static String TB_ACTIVITY_INFO = "tb_activity_info";
	
	// exercise information attribute name in database table
	public static final String USE_ID = "use_id";
	public static final String EXERCISE_DATE = "date";
	public static final String EXERCISE_GOAL = "goal_point";
	public static final String EXERCISE_ACHIEVE = "achieve_point";
	public static String KEY_UTC_TIME = "utc_time";
	public static String KEY_STEPS = "steps";
	public static String KEY_DISTANCE = "distance";
	public static String KEY_CALORIE = "calorie";
}
