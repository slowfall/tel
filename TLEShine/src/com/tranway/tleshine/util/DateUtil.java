package com.tranway.tleshine.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
	private static final String SIMPLE_FORMAT = "yyyy-MM-dd";
	private static final String[] MONTH = { "January", "February", "March", "April", "May", "June", "July", "August",
			"September", "October", "November", "December" };

	public static String getBirthdayByFormatDate(String formatDate) throws ParseException {
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
	 * get age by format date string
	 * 
	 * @param formatDate
	 *            format date string
	 * @return return age or -1
	 * @throws ParseException
	 */
	public static int getAgeByFormatDate(String formatDate) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(SIMPLE_FORMAT);
		Date date = format.parse(formatDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getAgeByCalendar(calendar);
	}

	private static int getAgeByCalendar(Calendar birthDay) {
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
