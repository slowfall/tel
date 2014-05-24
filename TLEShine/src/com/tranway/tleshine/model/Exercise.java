package com.tranway.tleshine.model;

import com.tranway.tleshine.R;

/**
 * Exercise class
 * 
 * @author shz
 * 
 */
public class Exercise {
	// The time required for the complete moving goal, unit is minute
	private static final int TIME_WALK_LIGHT = 60;
	private static final int TIME_WALK_MODERATE = 90;
	private static final int TIME_WALK_STRENUOUS = 150;
	private static final int TIME_RUN_LIGHT = 20;
	private static final int TIME_RUN_MODERATE = 30;
	private static final int TIME_RUN_STRENUOUS = 60;
	private static final int TIME_SWIM_LIGHT = 30;
	private static final int TIME_SWIM_MODERATE = 45;
	private static final int TIME_SWIM_STRENUOUS = 60;

	private static final int POINT_LIGHT = 600;
	private static final int POINT_MODERATE = 1000;
	private static final int POINT_STRENUOUS = 1600;

	private Intensity intensity = Intensity.LIGHT;
	private Sport method = Sport.WALK;
	private int time = 0;

	/**
	 * exercise intensity
	 */
	public enum Intensity {
		LIGHT(0x01), MODERATE(0x02), STRENUOUS(0x03);

		private int id = 0x01;

		private Intensity(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public static Intensity getIntensityById(int id) {
			if (id == -1) {
				return LIGHT;
			}
			for (Intensity is : Intensity.values()) {
				if (id == is.getId()) {
					return is;
				}
			}
			return null;
		}
	}

	/**
	 * exercise method
	 */
	public enum Sport {
		WALK(0x10), RUN(0x11), SWIM(0x12);

		private int id = 0x01;

		private Sport(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public static Sport getSportById(int id) {
			if (id == -1) {
				return WALK;
			}
			for (Sport s : Sport.values()) {
				if (id == s.getId()) {
					return s;
				}
			}
			return null;
		}
	}

	public Exercise() {
		this.intensity = Intensity.LIGHT;
		this.method = Sport.WALK;
	}

	public Exercise(Intensity intensity, Sport method) {
		this.intensity = intensity;
		this.method = method;
	}

	public int getExerciseGoalTime(Sport method) {
		if (this.intensity == null || this.method == null) {
			return 0;
		}

		switch (this.intensity) {
		case LIGHT:
			if (method == Sport.WALK) {
				return TIME_WALK_LIGHT;
			} else if (method == Sport.RUN) {
				return TIME_RUN_LIGHT;
			} else {
				return TIME_SWIM_LIGHT;
			}
		case MODERATE:
			if (method == Sport.WALK) {
				return TIME_WALK_MODERATE;
			} else if (method == Sport.RUN) {
				return TIME_RUN_MODERATE;
			} else {
				return TIME_SWIM_MODERATE;
			}
		default:
			if (method == Sport.WALK) {
				return TIME_WALK_STRENUOUS;
			} else if (method == Sport.RUN) {
				return TIME_RUN_STRENUOUS;
			} else {
				return TIME_SWIM_STRENUOUS;
			}
		}

	}

	public int getExerciseIntensityTitle() {
		int resId = -1;
		if (intensity == null) {
			return resId;
		}
		switch (intensity) {
		case LIGHT:
			resId = R.string.intensity_light;
			break;
		case MODERATE:
			resId = R.string.intensity_moderate;
			break;
		case STRENUOUS:
			resId = R.string.intensity_strenuous;
			break;
		}

		return resId;
	}

	public int getExercisePoint() {
		int point = 0;
		if (this.intensity == null) {
			return point;
		}
		switch (intensity) {
		case LIGHT:
			point = POINT_LIGHT;
			break;
		case MODERATE:
			point = POINT_MODERATE;
			break;
		case STRENUOUS:
			point = POINT_STRENUOUS;
			break;
		}

		return point;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public Intensity getIntensity() {
		return intensity;
	}

	public void setIntensity(Intensity intensity) {
		this.intensity = intensity;
	}

	public Sport getMethod() {
		return method;
	}

	public void setMethod(Sport method) {
		this.method = method;
	}
}
