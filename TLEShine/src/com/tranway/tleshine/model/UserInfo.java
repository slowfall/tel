package com.tranway.tleshine.model;

public class UserInfo {
	/**
	 * more information please read Protocol 9.7 and 9.8
	 */
	public static final int USER_INFO_BYTES_LENGTH_NOT_NEED_UPDATE = 4;
	/**
	 * more information please read Protocol 9.7 and 9.8
	 */
	public static final int USER_INFO_BYTES_LENGTH_NEED_UPDATE = 12;
	public static final byte NOT_NEED_UPDATE_FLAG = (byte) 0xE0;
	public static final byte NEED_UPDATE_FLAG = (byte) 0xE1;
	
	public static final int SEX_FEMALE = 0x00;
	public static final int SEX_MALE = 0x01;
	
	private int weight; // Weight(Unit: 0.1Kg); 2byte
	private int age; // Age; 1byte
	private int height; // Height(Unit: 1cm); 1byte
	private int stride; // Stride(Unit: 1cm); 1byte
	private int sex; // Sex 0x00:Female 0x01:Male; 1 byte
	private int stepsTarget; // Steps target; 3 byte
	private String email; // User register Email
	private String password; // User register Password
	private String birthday; // User register Birthday

	public UserInfo() {

	}

	public UserInfo(int weight, int age, int height, int stride, int sex, int stepsTarget) {
		this.weight = weight;
		this.age = age;
		this.height = height;
		this.stride = stride;
		this.sex = sex;
		this.stepsTarget = stepsTarget;
	}

	/**
	 * Weight(Unit: 0.1Kg); 2byte
	 * 
	 * @return weight
	 */
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * Age; 1byte
	 * 
	 * @return age
	 */
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * Height(Unit: 1cm); 1byte
	 * 
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Stride(Unit: 1cm); 1byte
	 * 
	 * @return stride
	 */
	public int getStride() {
		return stride;
	}

	public void setStride(int stride) {
		this.stride = stride;
	}

	/**
	 * Sex 0x00:Female 0x01:Male; 1 byte
	 * 
	 * @return sex
	 */
	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	/**
	 * Steps target; 3 byte
	 * 
	 * @return steps target
	 */
	public int getStepsTarget() {
		return stepsTarget;
	}

	public void setStepsTarget(int stepsTarget) {
		this.stepsTarget = stepsTarget;
	}

	/**
	 * Register email
	 * 
	 * @return user register email
	 */
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
}
