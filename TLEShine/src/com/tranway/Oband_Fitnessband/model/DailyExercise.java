package com.tranway.Oband_Fitnessband.model;


public class DailyExercise {
	private long date = 0L;
	private int goal = 0; // goal exercise point
	private int achieve = 0; // achieve exercise point

	public DailyExercise() {
	}

	public DailyExercise(long date, int goal, int achieve) {
		this.date = date;
		this.goal = goal;
		this.achieve = achieve;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getGoal() {
		return goal;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}

	public int getAchieve() {
		return achieve;
	}

	public void setAchieve(int achieve) {
		this.achieve = achieve;
	}

}
