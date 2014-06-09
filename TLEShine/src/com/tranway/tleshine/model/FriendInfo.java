package com.tranway.tleshine.model;

/**
 * @author ZhaHui more information please read Protocol 9.1
 */
public class FriendInfo {

	private String email;
	private long id = -1;
	private String name;
	private int point = 0;
	private int rank = -1;

	public FriendInfo() {

	}

	public FriendInfo(String email, long id, String name, int point, int rank) {
		this.email = email;
		this.id = id;
		this.name = name;
		this.point = point;
		this.rank = rank;
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

}
