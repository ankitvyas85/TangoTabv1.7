package com.tangotab.core.utils;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.tangotab.core.constant.AppConstant;
/**
 * Get location information and latitude and longitude from given address
 * 
 * @author Dillip.Lenka
 *
 */
public class GeoCoderUtil
{
	/**
	 * Get location information from the address.
	 * @param address
	 * @return
	 */
	public static  JSONObject getLocationInfo(String address)
	{
		Log.v("Invoking getLocationInfo from address", "address = "+address);
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
	        address = address.replaceAll(" ","%20");
	        HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
	        HttpClient client = new DefaultHttpClient();
	        HttpResponse response;
	        stringBuilder = new StringBuilder();	
		    response = client.execute(httppost);
		    HttpEntity entity = response.getEntity();
		    InputStream stream = entity.getContent();
		    int readDate;
		    while ((readDate = stream.read()) != -1)
		    {
		        stringBuilder.append((char) readDate);
		    }
        } catch (ClientProtocolException e)
        {
        	Log.e("Exception:", "ClientProtocolException occured at the time of get loacation information from address");
        	return null;
        }
        catch (IllegalArgumentException e)
        {
        	Log.e("Exception:", "IOException occured at the time of get loacation information from address");
        	return null;
        }        
        catch (Exception e)
        {
        	Log.e("Exception:", "IOException occured at the time of get loacation information from address");
        	return null;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) 
        {
        	Log.e("Exception:", "JSONException occured at the time of get location information from address");
            return null;
        }

        return jsonObject;
    }
	/**
	 * Get Latitude and Longitude from address.
	 * 
	 * @param address
	 * @return
	 */
	public static boolean getLatLong(String address)
	{
		 Log.v("Invoking getLatLong from address", "address = "+address);
        try {
        	JSONObject jsonObject = getLocationInfo(address);
        	if(jsonObject!=null)
        	{        	
	            double longitute = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	                .getJSONObject("geometry").getJSONObject("location")
	                .getDouble("lng");
	
	            double latitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
	                .getJSONObject("geometry").getJSONObject("location")
	                .getDouble("lat");
	           Log.v("Laonitude and latitude ", "longitute = "+longitute+" latitude ="+latitude);
	            AppConstant.locationLat =latitude;
	            AppConstant.locationLong =longitute;
	          }        

        } catch (JSONException e)
        {
        	Log.e("Exception:", "JSONException occured at the time of get lat and long from address");
            return false;
        }
        return true;
    }
	
	/**
	 * Get location information from the address.
	 * @param address
	 * @return
	 */
	public static  JSONObject getLocationInfoForUs(String zip)
	{
		Log.v("Invoking getLocationInfoForUs from zip", "postalcode = "+zip);
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
	        HttpPost httppost = new HttpPost("http://api.geonames.org/postalCodeSearchJSON?postalcode="+zip+"&maxRows=10&username=ankit_eng85");
	        HttpClient client = new DefaultHttpClient();
	        HttpResponse response;
	        stringBuilder = new StringBuilder();	
		    response = client.execute(httppost);
		    HttpEntity entity = response.getEntity();
		    InputStream stream = entity.getContent();
		    int readDate;
		    while ((readDate = stream.read()) != -1)
		    {
		        stringBuilder.append((char) readDate);
		    }
        } catch (ClientProtocolException e)
        {
        	Log.e("Exception:", "ClientProtocolException occured at the time of get information for us zip");
        	return null;
        }
        catch (IllegalArgumentException e)
        {
        	Log.e("Exception:", "IOException occured at the time of get loacation information for us zip");
        	return null;
        }        
        catch (Exception e)
        {
        	Log.e("Exception:", "IOException occured at the time of get information for us zip");
        	return null;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) 
        {
        	Log.e("Exception:", "JSONException occured at the time of get information for us zip");
            return null;
        }

        return jsonObject;
    }
}
