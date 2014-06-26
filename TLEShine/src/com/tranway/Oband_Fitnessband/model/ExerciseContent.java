package com.tranway.Oband_Fitnessband.model;

import com.tranway.Oband_Fitnessband.model.ExerciseUtils.Sport;

public class ExerciseContent {

	private long fromTime = 0;
	private long toTime = 0;
	private Sport sport = Sport.WALK;
	private int point = 0;

	public ExerciseContent(long fromTime, long toTime, Sport sport, int point) {
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.sport = sport;
		this.point = point;
	}

	public long getFromTime() {
		return fromTime;
	}

	public void setFromTime(long fromTime) {
		this.fromTime = fromTime;
	}

	public long getToTime() {
		return toTime;
	}

	public void setToTime(long toTime) {
		this.toTime = toTime;
	}

	public Sport getSport() {
		return sport;
	}

	public void setSport(Sport sport) {
		this.sport = sport;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

}
