package com.tangotab.core.xmlHandler;

import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

/**
 * This class is used for conversion of data type. This extends Default Handler
 * 
 * @author Dillip.Lenka
 *
 */

public class BaseSAXHandler extends DefaultHandler
{
	public final static String apostrophe = "'";
	public final static String sep = ",";
	public Context context;

	/**
	 * This method converts the input into the string 
	 * @param sb
	 * @return
	 */
	public String sbToString(StringBuffer sb)
	{
		try
		{
			return sb.toString().trim();
		}
		catch(Exception e)
		{
			Log.e(this.getClass().getName(), "sb2String exception:"+e.getMessage() );
		}
		return null;
	}
	
	/**
	 * This method converts the string buffer  into the Long
	 * @param sb
	 * @return
	 */
	public long sbToLong(StringBuffer sb)
	{
		if (sb==null) return 0L;
		return stringToLong(sb.toString());
	}
	
	/**
	 * This method converts the String to Long
	 * @param string
	 * @return
	 */
	public long stringToLong(String string)
	{
		long result = 0L;
		if (string != null && string.length() > 0)
		{
			try
			{
				result = Long.parseLong(string);
			}
			catch(Exception e)
			{
				Log.e(this.getClass().getName(), "sb2String exception:"+e.getMessage() );
			}
		}
		return result;
	}
	/**
	 * This method converts the String Buffer into the integer
	 * @param sb
	 * @return
	 */
	public int sbToInt(StringBuffer sb)
	{
		if (sb==null) return 0;
		return stringToInt(sb.toString());
	}
	/**
	 * This method converts the string to integer
	 * @param string
	 * @return
	 */
	public int stringToInt(String string)
	{
		int result = 0;
		if (string != null && string.length() > 0)
		{
			try
			{
				result = Integer.parseInt(string);
			}
			catch(Exception e)
			{
				Log.e(this.getClass().getName(), "string2Int exception:"+e.getMessage() );
			}
		}
		return result;
	}
	/**
	 * This method converts the String to Float
	 * @param string
	 * @return
	 */
	public float stringToFloat(String string)
		{
			float result = 0f;
			if (string != null && string.length() > 0)
			{
				try
				{
					result = Float.parseFloat(string);
				}
				catch(Exception e)
				{
					Log.e(this.getClass().getName(), "sb2Float exception:"+e.getMessage() );
				}
			}
			return result;
		}
		/**
		 * This method converts the Float to int
		 * @param no
		 * @return
		 */
		public int floatToInt(float no)
		{
			int result=0;
			
			try
			{
				result=(int)no;
			}
			catch (Exception e) 
			{
				Log.e(this.getClass().getName(), "float2Int exception:"+e.getMessage() );
			}
			return result;
		}
	/**
	 * This method converts the StringBuffer to the boolean
	 * @param sb
	 * @return
	 */
	public boolean sbToBoolean(StringBuffer sb)
	{
		int result = 0;
	
		if (sb.length() > 0)
		{
			try
			{
				result = Integer.parseInt(sb.toString());
			}
			catch(Exception e)
			{
				Log.e(this.getClass().getName(), "sb2Boolean exception:"+e.getMessage() );
			}
			
		}
		return (result == 1);
	}
	/**
	 * This method converts the String Buffer to Double
	 * @param sb
	 * @return
	 */
	public double sbToDouble(StringBuffer sb)
	{
		return stringToDouble(sb.toString());
	}
	/**
	 * This method converts the String into the Double
	 * @param strValue
	 * @return
	 */
	public double stringToDouble(String strValue)
	{
		double result = 0;
		
		try
		{
			result = Double.parseDouble(strValue);
		}
		catch(Exception e)
		{
			Log.e(this.getClass().getName(), "string2Double exception:"+e.getMessage() );
		}
		return result;
	}
	
}
