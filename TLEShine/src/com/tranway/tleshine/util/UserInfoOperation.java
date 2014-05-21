package com.tranway.tleshine.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class UserInfoOperation {
	private static final String SIMPLE_FORMAT = "yyyy-MM-dd";
	private static final String[] MONTH = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };

	/**
	 * convert date[ 1990-08-19 ] to [ September 19, 1990 ]
	 * 
	 * @param formatDate format date string, such as [ 1990-08-19 ]
	 * @return such as [ September 19, 1990 ]
	 * 
	 * @throws ParseException
	 */
	public static String convertDateToBirthday(String formatDate) throws ParseException {
		DecimalFormat df = new DecimalFormat("00");
		SimpleDateFormat format = new SimpleDateFormat(SIMPLE_FORMAT);
		Date date = format.parse(formatDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		return MONTH[month] + " " + df.format(day) + ", " + year;
	}

	/**
	 * convert date[ 1990-08-19 ] to age
	 * 
	 * @param formatDate format date string, such as [ 1990-08-19 ]
	 * @return return age or -1
	 * @throws ParseException
	 */
	public static int convertDateToAge(String formatDate) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(SIMPLE_FORMAT);
		Date date = format.parse(formatDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return canculateAgeByCalendar(calendar);
	}

	/**
	 * convert height value to string [ 1.70 米 ]
	 * 
	 * @param high 170 cm
	 * @return 1.70 米
	 */
	public static String convertHighToString(int high) {
		DecimalFormat df = new DecimalFormat("#0.00");
		String s = df.format((float) high / 100) + " 米";
		return s;
	}

	/**
	 * convert weight value to string [ 50.4 公斤 ]
	 * 
	 * @param weight 504*0.1KG
	 * @return 50.4 公斤
	 */
	public static String convertWeightToString(int weight) {
		DecimalFormat df = new DecimalFormat("#0.0");
		String s = df.format((float) weight / 10) + " 公斤";
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

}
