package com.tangotab.core.google.directions.service;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.google.directions.dao.DirectionDetailsDAO;
import com.tangotab.core.google.directions.vo.DirectionsVo;
import com.tangotab.core.google.directions.vo.DirectionsVo.Location;

/**
 * Service to get Direction details using Googles Map API
 *  
 * @author Dillip.Lenka
 *
 */
public class RouteDirectionService 
{
	/**
	 * Get Driving / Route Details using Google Map Services
	 * 
	 * @param {@link Location} - source
	 * @param {@link Location} destination
	 * @return {@link DrivingDirectionsVO}
	 * 
	 * @throws TangoTabException
	 */
	public DirectionsVo getDirections(Location source , Location destination) throws TangoTabException
	{
		try
		{
			return new DirectionDetailsDAO().getDirections(source, destination);
		}
		catch(Exception ex)
		{
			Log.e("GoogleDirection","Exception while retreving Driving Direction",ex);
			throw new TangoTabException(this.getClass().getName(),"getDirections", ex);
		}
	}
	
	/**
	 * Get Latitude a longitude from address
	 * 
	 * @param address
	 * @return
	 * @throws TangoTabException
	 */
	
	public Location getGeoDetailsFromAddress(String address)throws TangoTabException
	{
		try
		{
			return new DirectionDetailsDAO().getGeoDetailsForGivenAddress(address);
		}
		catch(Exception ex)
		{
			Log.e("GoogleDirection","Exception while retreving Lat Long from address",ex);
			throw new TangoTabException(this.getClass().getName(),"getGeoDetailsForGivenAddress", ex);
		}
	}
	
	
}
