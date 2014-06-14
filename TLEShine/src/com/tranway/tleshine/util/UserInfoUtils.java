package com.tranway.tleshine.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.util.Log;

import com.tranway.tleshine.model.UserInfo;

@SuppressLint("SimpleDateFormat")
public class UserInfoUtils {
	private static final String SIMPLE_FORMAT = "yyyy-MM-dd";

	/**
	 * convert birthday string to UNIX date
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * 
	 * @return UNIX date
	 */
	public static long convertBirthdayToUnixDate(int year, int month, int day) {
		String birthday = year + "-" + month + "-" + day;
		long date = 0;
		try {
			date = new SimpleDateFormat(SIMPLE_FORMAT).parse(birthday).getTime() / 1000L;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * convert date[ 1990-08-19 ] to [ September 19, 1990 ]
	 * 
	 * @param formatDate
	 *            format date string, such as [ 1990-08-19 ]
	 * @return such as [ 1990年 09月 10日 ]
	 * 
	 * @throws ParseException
	 */
	public static String convertDateToBirthday(long time) throws ParseException {
		DecimalFormat df = new DecimalFormat("00");
		Date date = new Date(time * 1000L);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		return year + "年  " + df.format(month) + "月  " + df.format(day) + "日";
	}

	/**
	 * convert date[ 1990-08-19 ] to age
	 * 
	 * @param formatDate
	 *            format date string, such as [ 1990-08-19 ]
	 * @return return age or -1
	 * @throws ParseException
	 */
	public static int convertDateToAge(long time) throws ParseException {
		Date date = new Date(time * 1000L);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return canculateAgeByCalendar(calendar);
	}

	/**
	 * convert height value to string [ 1.70 米 ]
	 * 
	 * @param high
	 *            170 cm
	 * @return 1.70 M
	 */
	public static String convertHighToString(int high) {
		DecimalFormat df = new DecimalFormat("#0.00");
		String s = df.format((float) high / 100) + " M";
		return s;
	}

	/**
	 * convert weight value to string [ 50.4 公斤 ]
	 * 
	 * @param weight
	 *            504*0.1KG
	 * @return 50.4 KG
	 */
	public static String convertWeightToString(int weight) {
		DecimalFormat df = new DecimalFormat("#0.0");
		String s = df.format((float) weight / 10) + " KG";
		return s;
	}

	private static int canculateAgeByCalendar(Calendar birthDay) {
		Calendar cal = Calendar.getInstance();
		if (cal.before(birthDay)) {
			return -1;
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		int yearBirth = birthDay.get(Calendar.YEAR);
		int monthBirth = birthDay.get(Calendar.MONTH);
		int dayOfMonthBirth = birthDay.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;
		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				age--;
			}
		}

		return age;
	}

	/**
	 * convert minute to hour
	 * 
	 * @param min
	 * 
	 * @return format hour
	 */
	public static String convertMinToHour(int min) {
		if (min < 60) {
			return min + " 分钟";
		} else {
			float h = (float) min / 60;
			DecimalFormat df = new DecimalFormat("#.0");
			return df.format(h) + " 小时";
		}
	}

	public static Map<String, String> convertUserInfoToParamsMap(UserInfo info) {
		Map<String, String> data = new HashMap<String, String>();
		if (info == null) {
			return data;
		}
		data.put(UserInfo.SEVER_KEY_EMAIL, info.getEmail());
		data.put(UserInfo.SEVER_KEY_NAME, info.getName());
		data.put(UserInfo.SEVER_KEY_PASSWORD, info.getPassword());
		data.put(UserInfo.SEVER_KEY_BIRTHDAY, String.valueOf(info.getBirthday()));
		Log.d("------", "birthday: " + info.getBirthday());
		data.put(UserInfo.SEVER_KEY_SEX, String.valueOf(info.getSex()));
		data.put(UserInfo.SEVER_KEY_HEIGHT, String.valueOf(info.getHeight()));
		data.put(UserInfo.SEVER_KEY_WEIGHT, String.valueOf(info.getWeight()));
		data.put(UserInfo.SEVER_KEY_GOAL, String.valueOf(info.getStepsTarget()));
		return data;
	}
}
