package com.tranway.tleshine.model;

public class ActivityInfo {
	public static final byte COMMAND = (byte) 0x03;
	private long utcTime;
	private int goal;
	private int steps;
	private int distance;
	private int calorie;

	public long getUtcTime() {
		return utcTime;
	}

	public void setUtcTime(long utcTime) {
		this.utcTime = utcTime;
	}

	public int getGoal() {
		return goal;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getCalorie() {
		return calorie;
	}

	public void setCalorie(int calorie) {
		this.calorie = calorie;
	}

	@Override
	public String toString() {
		return super.toString() + ", utcTime:" + this.utcTime +", goal:" + this.goal + ", steps:"
				+ this.steps + ", distance:" + this.distance + ", calorie:"
				+ this.calorie;
	}

}
