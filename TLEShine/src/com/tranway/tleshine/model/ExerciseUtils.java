package com.tranway.tleshine.model;

/**
 * Exercise class
 * 
 * @author shz
 * 
 */
public class ExerciseUtils {

	public static int GOAL_POINT_LIGHT = 600;
	public static int GOAL_POINT_MODERATE = 1000;
	public static int GOAL_POINT_STRENUOUS = 1600;

	// The time required for the complete moving goal, unit is minute
	private static final int TIME_WALK_PER_100 = 10;
	private static final int TIME_RUN_PER_100 = 4;
	private static final int TIME_SWIM_PER_100 = 5;

	/**
	 * exercise method
	 */
	public enum Sport {
		WALK, RUN, SWIM;
	}

	public static int getAchieveGoalTime(int goal, Sport sport) {
		int time = -1;
		if (goal < 0) {
			return time;
		}

		switch (sport) {
		case WALK:
			time = goal * TIME_WALK_PER_100 / 100;
			break;
		case RUN:
			time = goal * TIME_RUN_PER_100 / 100;
			break;
		case SWIM:
			time = goal * TIME_SWIM_PER_100 / 100;
			break;
		}

		return time;
	}
}
