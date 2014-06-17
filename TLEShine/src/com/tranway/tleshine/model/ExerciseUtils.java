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
	private static final float TIME_WALK_PER_100 = 1.0f;
	private static final float TIME_RUN_PER_100 = 0.4f;
	private static final float TIME_SWIM_PER_100 = 0.5f;

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
			time = (int) (goal * TIME_WALK_PER_100 / 100);
			break;
		case RUN:
			time = (int) (goal * TIME_RUN_PER_100 / 100);
			break;
		case SWIM:
			time = (int) (goal * TIME_SWIM_PER_100 / 100);
			break;
		}

		return time;
	}
}
