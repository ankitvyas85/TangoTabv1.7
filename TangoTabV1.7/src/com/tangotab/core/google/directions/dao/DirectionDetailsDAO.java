package com.tangotab.core.google.directions.dao;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.google.directions.dto.GoogleGetDirectionJSONParser;
import com.tangotab.core.google.directions.vo.DirectionsVo;
import com.tangotab.core.google.directions.vo.DirectionsVo.Location;
import com.tangotab.core.utils.RestServiceUtil;
import com.tangotab.core.utils.ValidationUtil;

/**
 * Direction Details DAO. THis class is responsiable for hitting the webservice and getting all of the direction related data.
 * 
 * @author Dillip.Lenka
 *
 */
public class DirectionDetailsDAO 
{
	/**
	 * Invokes the Webservice and returns direction Details
	 * 
	 * @param {@link Location} : source
	 * @param {@link Location} :destination
	 * @return {@link DirectionsVo}
	 * 
	 * @throws Exception
	 */
	public DirectionsVo getDirections(Location source , Location destination) throws Exception
	{
		String mapURL = constructURL(source.getLatitude(), source.getLongitude(), destination.getLatitude(), destination.getLongitude());
		Log.i("Maps GetDirection REST API URL : ", mapURL);
		
		// Get method
		HttpGet get = new HttpGet(mapURL);
		get.setHeader("Content-Type", "application/json");
		
		try
		{
			RestServiceUtil restAPI = new RestServiceUtil();
			String jsonResponse = restAPI.executeHTTPGet(get);
		
			DirectionsVo directionsVo = new DirectionsVo();
			directionsVo.setSource(source);
			directionsVo.setDestination(destination);
			
			GoogleGetDirectionJSONParser jsonParser = new GoogleGetDirectionJSONParser();
			return jsonParser.convert(jsonResponse, directionsVo);
		} 
		catch (Exception e) 
		{
			Log.e(this.getClass().getCanonicalName(), "Failed Invoking GetDirections REST URL " + mapURL, e);
			throw e;
		}
	}
	
	/**
	 * Constructs the REST API URL for getting directions
	 * 
	 * @param sourceLatitude
	 * @param SourceLongitude
	 * @param destinationLatitude
	 * @param destinationLongitude
	 */
	private String constructURL(String sourceLatitude, String sourceLongitude , String destinationLatitude , String destinationLongitude)
	{
		StringBuilder urlString = new StringBuilder();
		 
		urlString.append("http://maps.googleapis.com/maps/api/directions/json?");		
		urlString.append("origin=");
		urlString.append(sourceLatitude);
		urlString.append(",");
		urlString.append(sourceLongitude);
		urlString.append("&destination=");
		urlString.append(destinationLatitude);
		urlString.append(",");
		urlString.append(destinationLongitude);
		urlString.append("&sensor=false");
		
		return urlString.toString();
	}
	
	/**
	 * Get GeoDetails for Given Address
	 * @param address
	 * @return
	 * @throws Exception
	 */
	public Location getGeoDetailsForGivenAddress(String address) throws Exception
	{
		StringBuilder googleUrl = new StringBuilder();
		if(!ValidationUtil.isNullOrEmpty(address)){
			googleUrl.append(AppConstant.GOOGLE_SERVICE_URL).append(address).append("&output=json");
		}
		else
			throw new Exception("Invalid address can not retrieve lat long value.");
		
		HttpGet get = new HttpGet(googleUrl.toString().replace(" ", "%20"));
		get.setHeader("Content-Type", "application/json");
		
		try
		{
			RestServiceUtil restAPI = new RestServiceUtil();
			String jsonResponse = restAPI.executeHTTPGet(get);
			
			GoogleGetDirectionJSONParser jsonParser = new GoogleGetDirectionJSONParser();
			return jsonParser.parseGeoDetails(jsonResponse);
		} 
		catch (Exception e) 
		{
			Log.e(this.getClass().getCanonicalName(), "Failed Invoking Google REST URL for lat and long " + googleUrl, e);
			throw e;
		}
	}
}
