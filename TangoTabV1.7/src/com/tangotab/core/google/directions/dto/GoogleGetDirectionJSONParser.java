package com.tangotab.core.google.directions.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.tangotab.core.google.directions.vo.DirectionsVo;
import com.tangotab.core.google.directions.vo.DirectionsVo.DirectionEntry;
import com.tangotab.core.google.directions.vo.DirectionsVo.Location;
/**
 * Json Response Parser for Google Get Direction API
 * 
 * @author Dillip.Lenka
 *
 */
public class GoogleGetDirectionJSONParser 
{
	/**
	 * Convert the JSON Response into app specific VO
	 * @param jsonResponse
	 * @param baseEntity
	 * @return
	 * @throws JSONException
	 */
	public DirectionsVo convert(String jsonResponse, DirectionsVo baseEntity) throws JSONException 
	{
		JSONObject jsonResponseObject = new JSONObject(jsonResponse);
		
		//Check if the JSON Response has 'routes' details
		if (jsonResponseObject.has("routes"))
		{
			//Get the Route Object
			JSONArray jsonRouteArray = jsonResponseObject.getJSONArray("routes");			
			JSONObject jsonRoute = (JSONObject) jsonRouteArray.get(0);
			
			//Get the Legs Object
			JSONArray jsonLegsArray = jsonRoute.getJSONArray("legs");
			JSONObject jsonLegs = (JSONObject) jsonLegsArray.get(0);
			
			//Add the Journey Overall Details
			baseEntity.setTotalDistance(getValidString(((JSONObject)jsonLegs.get("distance")), "text"));
			baseEntity.setTotalDuration(getValidString(((JSONObject)jsonLegs.get("duration")), "text"));
			baseEntity.getSource().setName(getValidString(jsonLegs, "start_address"));
			baseEntity.getDestination().setName(getValidString(jsonLegs, "end_address"));
			
			//Get the Json Steps Object
			JSONArray jsonStepsArray = jsonLegs.getJSONArray("steps");
			
			//Iterate over Each Step Object
			for (int i=0 ; i < jsonStepsArray.length() ; i++)
			{
				JSONObject jsonStep = (JSONObject) jsonStepsArray.get(i);
				
				DirectionEntry entry = new DirectionEntry();
				entry.setDistance(getValidString(jsonStep.getJSONObject("distance") , "text"));
				entry.setDuration(getValidString(jsonStep.getJSONObject("duration") , "text"));
				entry.setInstruction(getValidString(jsonStep , "html_instructions"));
				
				baseEntity.setDrivingDirections(entry);
				
				//decodePointsData(getValidString(jsonStep.getJSONObject("polyline") , "points"), baseEntity);
			}
			
			//Parse the Points
			decodePointsData(getValidString(jsonRoute.getJSONObject("overview_polyline") , "points"), baseEntity);
		}
		
		return baseEntity;
	}
	
	/**
	 * Parse the Json Response and Retrieve the Location Details 
	 * @param jsonResponse
	 * @return {@link Location}
	 * @throws JSONException
	 */
	public Location parseGeoDetails(String jsonResponse)throws JSONException 
	{
		Location location = new Location();
		
		try
		{
			JSONObject jsonResponseObject = new JSONObject(jsonResponse);
			if (jsonResponseObject.has("Placemark"))
			{
				JSONArray jsonPlacemarkArray = jsonResponseObject.getJSONArray("Placemark");	
				
				for (int i=0 ; i < jsonPlacemarkArray.length() ; i++)
				{
					JSONObject jsonPlacemarkEntry = jsonPlacemarkArray.getJSONObject(i);
					
					//check if point details is available within the response
					if(jsonPlacemarkEntry.has("Point"))
					{
						JSONObject jsonPoint = jsonPlacemarkEntry.getJSONObject("Point");
						
						//Check if it has Co-ordinates
						if (jsonPoint.has("coordinates"))
						{
							JSONArray jsonCoordinates = jsonPoint.getJSONArray("coordinates");
							location.setLatitude(jsonCoordinates.getString(1));
							location.setLongitude(jsonCoordinates.getString(0));
							break;
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			Log.e("Google GeoDetails", e.getLocalizedMessage());
			
			location.setLatitude("0");
			location.setLongitude("0");
		}
		
		return location;
	}
	
	/**
	 * Parse the encoded PolyLine Data 
	 * @param encoded
	 * @param entity
	 */
	private void decodePointsData(String encoded , DirectionsVo entity)
	{
		// replace two backslashes by one (some error from the transmission)
		encoded = encoded.replace("\\\\", "\\");
		 
		//decoding
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
 
        while (index < len) 
        {
            int b, shift = 0, result = 0;
            do 
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
 
            shift = 0;
            result = 0;
            
            do 
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
		    lng += dlng;
		 
		    GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6), (int) (((double) lng / 1E5) * 1E6));
		    
		    entity.setMapRouteOverlayDetails(p);
		}
	}
	
	
	/**
	 * Generic Method for Extracting the String details from the JsonResponse Data Object
	 * @param jsonObject
	 * @param key
	 * @return {@link String}
	 */
	private String getValidString(JSONObject jsonObject, String key) 
	{
		if (jsonObject == null)
			return "";

		try 
		{
			if (jsonObject.has(key)) 
				return jsonObject.getString(key);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return "";
	}
}
