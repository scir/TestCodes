package org.smartcity.util;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class StringUtils {

	public static void main(String[] args) {
		System.out.println(getTodaysDateString());
	}

    /**
     * Checks if a trimmed String is non null and is not empty (length > 0).
     * @param  str  the string to check
     * @return whether a trimmed String is non null and is not empty (length > 0).
     */
    public static boolean isNotEmpty(String str) {
        return (str != null && str.trim().length() > 0);
    }

    /**
     * Checks if a trimmed String is null or empty.
     * @param  str  the string to check
     * @return whether a trimmed String is null and or empty (length == 0).
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.trim().length() == 0);
    }


    public static String getRandomFileName(){
    	return "" + new Date().getTime() ;
    }

    public static String getTodaysDateString(){
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(new Date());
    	return "" + cal.get(Calendar.YEAR) + formatNumber(cal.get(Calendar.MONTH)+1) + formatNumber(cal.get(Calendar.DATE));
    }

    public static String formatNumber(int input){
    	DecimalFormat mFormat= new DecimalFormat("00");
    	return mFormat.format(input);
    }
    
//	public static String splitIds(String input, String separator, DATA_TYPE type){
//		if (input==null || isEmpty(input)) return null ;
//
//		return null ;
//	}
	
}
