package com.tangotab.core.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility to help in validating fields.
 *
 * @author Dillip.Lenka
 *
 */
public class ValidationUtil
{

	/**
	 * Checks if is null.
	 *
	 * @param value the value
	 * @return true, if is null
	 */
	public static boolean isNull(String value) {
		return (value == null);
	}

	/**
	 * Checks if is null.
	 *
	 * @param value the value
	 * @return true, if is null
	 */
	public static boolean isNull(Object value) {
		return (value == null);
	}

	/**
	 * Checks if is null or empty.
	 *
	 * @param value the value
	 * @return true, if is null or empty
	 */
	public static boolean isNullOrEmpty(String value) {
		return (value == null || value.length()<=0);
	}

	/**
	 * Checks if is null or empty.
	 *
	 * @param value the value
	 * @return true, if is null or empty
	 */
	public static boolean isNullOrEmpty(List value) {
		return (value == null || value.isEmpty());
	}

	/**
	 * Checks if is null or empty.
	 *
	 * @param value the value
	 * @return true, if is null or empty
	 */
	public static boolean isNullOrEmpty(Map value) {
		return (value == null || value.isEmpty());
	}

	/**
	 * Checks if is positive.
	 *
	 * @param field to be validated.
	 * @return true if given number is positive.
	 */
	public static boolean isPositive(final int field) {
		return field > 0;
	}

	/**
	 * Checks if is positive.
	 *
	 * @param field to be validated.
	 * @return true if given number is positive.
	 */
	public static boolean isPositive(final long field) {
		return field > 0;
	}

	/**
	 * Checks if is null or false.
	 *
	 * @param value the value
	 * @return true, if is null or false
	 */
	public static boolean isNullOrFalse(Boolean value) {
		return (value == null || value == false);
	}

	/**
	 * Checks if is null.
	 *
	 * @param value the value
	 * @return true, if is null
	 */
	public static boolean isNull(Number value) {
		return (value == null);
	}

	/**
	 * Checks if is integer.
	 *
	 * @param input the input
	 * @return true, if is integer
	 */
	public static boolean isInteger(String input)
	{
		if(isNullOrEmpty(input))
			return false;
		try {
			Long.parseLong(input.trim());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * Check for null and set value.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String checkForNullAndSetValue(String value) {
		String returnValue = "";
		if (ValidationUtil.isNullOrEmpty(value)) {
			returnValue = "0";
		} else {
			returnValue = value;
		}
		return returnValue;
	}
	
	/**
	 * This method will do the email validation 
	 * @param emailstring
	 * @return
	 */
	public static boolean eMailValidation(String emailstring)
	{
		if(ValidationUtil.isNullOrEmpty(emailstring))
			return false;
		Pattern emailPattern = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
				+ "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
				+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
		Matcher emailMatcher = emailPattern.matcher(emailstring);
		return emailMatcher.matches();
	}
	


	public  static boolean validateUSAZip(String zipCode)
	{
		if(isNullOrEmpty(zipCode))
			return false;
		try{
		String zipCodePattern ="^\\d{5}(-\\d{4})?$";
		if(zipCode.matches(zipCodePattern))
			return true;
		else
			return false;
		}catch(Exception e)
		{
			return false;
		}
	}
	
	public static boolean validateCanadZip(String zipCode)
	{
		if(isNullOrEmpty(zipCode))
			return false;
		try{
		String zipCodePattern ="^[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy]{1}\\d{1}[A-Za-z]{1} *\\d{1}[A-Za-z]{1}\\d{1}$";
		if(zipCode.matches(zipCodePattern))
			return true;
		else
			return false;
		}catch(Exception e)
		{
			return false;
		}
	}
	
	public static boolean isValidZip(String zipcode) {
		String formattedAddress[] = null;
		String countryCode = null;
		JSONObject jsonObject = GeoCoderUtil.getLocationInfo(zipcode);
		try {
			jsonObject = jsonObject.getJSONArray("results").getJSONObject(0);
			formattedAddress = jsonObject.getString("formatted_address").split(",");
			if(!ValidationUtil.isNull(formattedAddress)){
				countryCode = formattedAddress[formattedAddress.length-1].trim();
				if(countryCode.equalsIgnoreCase("canada") || countryCode.equalsIgnoreCase("usa")){
					return true;
				}else{
					return false;
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isValidUSZip(String zipcode){
		
		JSONObject jsonObject = GeoCoderUtil.getLocationInfoForUs(zipcode);
		String countryCode = null;
		JSONArray jsonArray = null;
		if(!ValidationUtil.isNull(jsonObject)){
				try {
					jsonArray = jsonObject.getJSONArray("postalCodes");
					if(!ValidationUtil.isNull(jsonArray)){
						for(int count = 0;count<jsonArray.length();count++){
								countryCode = jsonArray.getJSONObject(count).getString("countryCode");
								if(countryCode.equalsIgnoreCase("US") || countryCode.equalsIgnoreCase("CA")){
									return true;
								}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
	
		return false;
	}
	
	public static Map<String,String> getLatLonFromAddress(String zipcode){
		JSONObject jsonObject = GeoCoderUtil.getLocationInfo(zipcode);
		Map<String,String> mapLocation = new HashMap<String, String>();
		try{
			if(jsonObject!=null)
			{
			jsonObject = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
			mapLocation.put("lat", jsonObject.getString("lat"));
			mapLocation.put("lng", jsonObject.getString("lng"));
			return mapLocation;
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		mapLocation.put("lat", "0.0");
		mapLocation.put("lat", "0.0");
		return null;
	}
}
