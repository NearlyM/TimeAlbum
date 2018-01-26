package com.danale.localfile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {
	public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_TIME_DATE_MINUTE = "yyyy-MM-dd HH:mm";
	public static final String FORMAT_DATE = "yyyy-MM-dd";
	public static final String FORMAT_DATE_DOT = "yyyy.MM.dd";
	public static final String FORMAT_TIME = "HH:mm:ss";
	public static final String FORMAT_DATE_INT = "yyyyMMdd";

	public static String getDateTime(long datetime, String format, String timeZone) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		if (timeZone == null) {
			dateFormat.setTimeZone(TimeZone.getDefault());
		} else {
			dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		return dateFormat.format(new Date(datetime));
	}

	public static long convertStringTimeToLong(String time, String format) {
		return convertStringTimeToLong(time, format,
				TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT, Locale.ENGLISH));
	}

	public static long convertStringTimeToLong(String time, String format, String timeZone) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
			Date date = dateFormat.parse(time);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;

	}


	public static long formatDate(int year, int month, int day) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder builder = new StringBuilder();
		builder.append(year);
		builder.append("-");
		if (month < 10) {
			builder.append(0);
		}
		builder.append(month);
		builder.append("-");
		if (day < 10) {
			builder.append(0);
		}
		builder.append(day);

		builder.append(" 00:00:00");
		try {
			return dateFormat.parse(builder.toString()).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static long getTodayStartTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis();
	}

	public static long getSomeDayAgoStamp(int some){
		long base = 24 * 60 * 60 * 1000;
		long time = getTodayStartTime() - some * base;
		return time;
	}

	public static int getDayCountFromToday(long timestamp){
		long base = 24 * 60 * 60 * 1000;
		long time = getTodayStartTime() - timestamp;
		int count = (int) (time/base) + 1;
		if (time < 0){
			return  0;
		}else {
			return count;
		}
	}
}
