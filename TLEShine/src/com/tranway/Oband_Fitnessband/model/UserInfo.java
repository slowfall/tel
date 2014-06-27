package com.tranway.Oband_Fitnessband.model;

/**
 * @author ZhaHui more information please read Protocol 9.1
 */
public class UserInfo {
	public static final int USER_INFO_BYTES_LENGTH_NOT_NEED_UPDATE = 4;
	public static final int USER_INFO_BYTES_LENGTH_NEED_UPDATE = 12;
	public static final byte NOT_NEED_UPDATE_FLAG = (byte) 0xE0;
	public static final byte NEED_UPDATE_FLAG = (byte) 0xE1;

	public static final int SEX_FEMALE = 0x01;
	public static final int SEX_MALE = 0x00;
	public static final String SEVER_KEY_PASSWORD = "Password";
	public static final String SEVER_KEY_EMAIL = "Email";
	public static final String SEVER_KEY_PHONE = "Phone";
	public static final String SEVER_KEY_BIRTHDAY = "BirthDay";
	public static final String SEVER_KEY_SEX = "Gender";
	public static final String SEVER_KEY_HEIGHT = "Height";
	public static final String SEVER_KEY_WEIGHT = "Weight";
	public static final String SEVER_KEY_GOAL = "Goal";
	public static final String SEVER_KEY_STEP_COUNT = "StepCount";
	public static final String SEVER_KEY_ID = "ID";
	public static final String SEVER_KEY_NAME = "Name";
	public static final String SEVER_KEY_CREATE_DATE = "CreateDate";
	public static final String SEVER_KEY_POINT = "Point";
	public static final String SEVER_KEY_USER = "User";

	private int weight = -1; // Weight(Unit: 0.1Kg); 2byte
	private int age = -1; // Age; 1byte
	private int height = -1; // Height(Unit: 1cm); 1byte
	private int stride = -1; // Stride(Unit: 1cm); 1byte
	private int sex = -1; // Sex 0x00:Female 0x01:Male; 1 byte
	private int stepsTarget = -1; // Steps target; 3 byte
	private String email; // User register Email
	private String name; // User register Name
	private String password; // User register Password
	private String phone;
	private long birthday = -1; // User register Birthday
	// private int goal = -1; // Exercise Goal Point
	private long id = -1; // user id

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

	/**
	 * @param weight
	 *            (Unit: 0.1Kg); 2byte
	 */
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

	/**
	 * @param height
	 *            (Unit: 1cm);
	 */
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

	/**
	 * @param stride
	 *            (Unit: 1cm); 1byte
	 */
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

	/**
	 * @param sex
	 *            0x00:Female 0x01:Male; 1 byte
	 */
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	// public int getGoal() {
	// return goal;
	// }
	//
	// public void setGoal(int goal) {
	// this.goal = goal;
	// }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
