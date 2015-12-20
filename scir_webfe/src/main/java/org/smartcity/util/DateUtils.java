package org.smartcity.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static void main(String[] args) {
		System.out.println("Formatted Date: " + formatDate(new Date(), "yyyy-MM-dd'T'HH:mm:ss'Z'", "NULL"));
	}
	
	public static Date getDate(int year, int month, int date){
		return getDate(year, month, date, 0, 0, 0);
	}
	
	public static Date getDate(int year, int month, int date, int hour, int minutes, int seconds){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, date, hour, minutes, seconds);
		return new Date(cal.getTimeInMillis()) ;
	}
	
	public static String formatDate(Date date, String pattern, String valueIfNull){
		if (date==null) return valueIfNull ;
		
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
		//System.out.println("Format: " + pattern  + ", Value: " + formattedDate);
		//return formattedDate;
	}
	
	public static Boolean isValidDate(String inputDate, String dateFormat){
		return getDate(inputDate, dateFormat)!=null ? true : false ;
	}
	
	public static Date getDate(String inputDate, String dateFormat){
		if (inputDate==null || inputDate.length() < 8) {		// length of 8 check added to avoid any date length less than 8
			return null ;
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			sdf.setLenient(false);
			return sdf.parse(inputDate);
		} catch (ParseException e) {
			return null ;
		}
	}
	
	public static long getDuration(Date startDate, Date endDate) {
		long diff = endDate.getTime() - startDate.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return diffDays;
	}

	// Testing Methods
	protected static void testDuration() {
		Calendar calendar1 = Calendar.getInstance();	Calendar calendar2 = Calendar.getInstance();
		Date startDate, endDate ;

		calendar1.set(2007, 01, 10);
		calendar2.set(2007, 07, 01);		
		startDate = calendar1.getTime(); endDate = calendar2.getTime();
		System.out.println("For Dates: " + startDate + " and " + endDate + ", the diff is: " + getDuration(startDate, endDate));
		
		calendar1.set(2012, 0, 01);
		calendar2.set(2012, 0, 01);		
		startDate = calendar1.getTime(); endDate = calendar2.getTime();
		System.out.println("For Dates: " + startDate + " and " + endDate + ", the diff is: " + getDuration(startDate, endDate));

		calendar1.set(2012, 0, 01);
		calendar2.set(2012, 0, 31);		
		startDate = calendar1.getTime(); endDate = calendar2.getTime();
		System.out.println("For Dates: " + startDate + " and " + endDate + ", the diff is: " + getDuration(startDate, endDate));
		
		calendar1.set(2012, 1, 01);
		calendar2.set(2012, 2, 01);		
		startDate = calendar1.getTime(); endDate = calendar2.getTime();
		System.out.println("For Dates: " + startDate + " and " + endDate + ", the diff is: " + getDuration(startDate, endDate));
	}

}
