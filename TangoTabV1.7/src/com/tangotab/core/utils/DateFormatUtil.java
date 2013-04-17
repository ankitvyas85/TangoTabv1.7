package com.tangotab.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;
/**
 * This class will be used to do all the date formating and date calculation
 * 
 * @author dillip.lenka
 *
 */
public class DateFormatUtil
{
	/**
	 * Get the date after two weeks .
	 * @param date
	 * @return
	 */
	public static Date dateAfterSomeTimePeriod(String date,String criteria,int time ,String parseFormat)
	{
		Log.v("Invoking dateAfterSomeTimePeriod() method ", "date ="+date+" criteria= "+criteria);
		if(ValidationUtil.isNullOrEmpty(date) || ValidationUtil.isNullOrEmpty(criteria))
			return null;	
		Calendar calender =null;
		try {
			calender = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(parseFormat);
			Date installTo = format.parse(date);			
			calender.setTime(installTo);
			if(criteria.equals("mins"))
				calender.add(Calendar.MINUTE, -time);
			if(criteria.equals("hour"))
				calender.add(Calendar.HOUR, time);
			if(criteria.equals("Week"))
				calender.add(Calendar.WEEK_OF_YEAR, time);
		} catch (ParseException e)
		{
			 Log.e("Error", "Error ocuured in dateAfterTwoWeeks method at the time of parsing the date.");
			return null;
		}
		return calender.getTime();
	}
	/**
	 * 
	 * @param date
	 * @param parseFormat
	 * @return
	 */
	public static Date parseIntoDifferentFormat(String date,String parseFormat)
	{
		Log.v("Invoking parseIntoDifferentFormat() method ", "date ="+date);
		if(ValidationUtil.isNullOrEmpty(date))
			return null;	
		Date installTo =null;
		try {
			SimpleDateFormat format = new SimpleDateFormat(parseFormat);
			installTo = format.parse(date);			
		 } 
		catch (ParseException e)
		{
			 Log.e("Exception:", "Error ocuured in dateAfterTwoWeeks method at the time of parsing the date.");
			return null;
		}
		return installTo;
	}
	
	/**
	 * Convert the date format
	 * @param date
	 * @return
	 */
	public static String getconvertdate(String date)
	{
		if(ValidationUtil.isNullOrEmpty(date))
			return null;
		Log.v("Invoking getconvertdate() method ", "date ="+date);
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
	    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
	    Date parsed = new Date();
	    try
	    {
	        parsed = inputFormat.parse(date);
	    }
	    catch (ParseException e)
	    {
	    	Log.e("Exception:", "Error ocuured in getconvertdate method at the time of parsing the date.");
	        return null;
	    }
	    if(parsed!=null){
	    return outputFormat.format(parsed);
	    }
	    return null;
	}
	/**
	 * Parse the date which are in GMT Format
	 * @param date
	 * @return
	 */
	public static Date parseGMTFormatDate(String date)
	{
		Log.v("Invoking parseGMTFormatDate() method ", "date ="+date);
		if(ValidationUtil.isNullOrEmpty(date))
			return null;
		Date finalDate=null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			final String OLD_FORMAT = "EEE MMM d HH:mm:ss z yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
			Date d = sdf.parse(date);
			sdf.applyPattern("yyyy-MM-d hh:mm:ss");
			String newinstallDate = sdf.format(d);			
			finalDate = format.parse(newinstallDate);	
			Log.v("finalDate is after parse ", "finalDate ="+finalDate);			
		} catch (ParseException e)
		{	
			Log.e("Error", "Error ocuured in parseGMTFormatDate method at the time of parsing the date.");		
			return null;
		}
		return finalDate;
	}
	
	/**
	 * Method will check whether the date is previous date or not.
	 *
	 * @param date  the date
	 * @return true, if successful
	 */
	public static  boolean isClaimDateAfterInstallDate(String installDate,String claimDate) 
	{
		Log.v("Invoking isClaimDateAfterInstallDate() method ", "installDate ="+installDate+" claimDate = "+claimDate);
		if (ValidationUtil.isNullOrEmpty(installDate) || ValidationUtil.isNullOrEmpty(claimDate))
			return false;
		//Parse into yyyy-MM-dd HH:mm:ss format
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date installTo = null;
		Date claimTo = null;
		try {
			installTo =parseGMTFormatDate(installDate);
			claimTo = format.parse(claimDate);			
			Log.v("after parsing completed ", "installDate ="+installTo+" claimDate = "+claimTo);			
		} catch (ParseException e)
		{
			Log.e("Error", "Error ocuured in isClaimDateAfterInstallDate method at the time of parsing the date.");
			return false;
		}
		return claimTo.after(installTo);
	}
}
