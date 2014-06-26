package com.tranway.Oband_Fitnessband.model;

public class FriendInfo implements Comparable<FriendInfo>{

	private String email;
	private long id = -1;
	private String name;
	private int point = 0;
	private int rank = -1;
	private int sex = 0;

	public FriendInfo() {

	}

	public FriendInfo(String email, long id, String name, int point, int rank, int sex) {
		this.email = email;
		this.id = id;
		this.name = name;
		this.point = point;
		this.rank = rank;
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	@Override
	public int compareTo(FriendInfo another) {
		if (another == null) {
			return -1;
		}
		
		return another.point - this.point;
	}

}
