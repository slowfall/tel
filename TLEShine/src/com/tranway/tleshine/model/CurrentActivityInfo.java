package com.tranway.tleshine.model;

public class CurrentActivityInfo {
	public static final byte COMMAND = (byte) 0x03;
	private long currentUTC;
	private int steps;
	private int distance;
	private int calorie;

	public long getCurrentUTC() {
		return currentUTC;
	}

	public void setCurrentUTC(long currentUTC) {
		this.currentUTC = currentUTC;
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

}
