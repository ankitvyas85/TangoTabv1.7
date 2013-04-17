package com.tangotab.core.google.directions.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

public class DirectionsVo implements Serializable
{
	private static final long serialVersionUID = 1L;

	//Total Distance
	private String totalDistance;
	private String totalDuration;
	
	//Source / Destination Details
	private Location source;
	private Location destination;
	
	//Driving Direction Details
	private List<DirectionEntry> drivingDirections = new ArrayList<DirectionEntry>();
	private List<GeoPoint> mapRouteOverlayDetails = new ArrayList<GeoPoint>();
	
	
	/*
	 * Getter Setters Methods
	 */
	public String getTotalDistance() 
	{
		return totalDistance;
	}

	public void setTotalDistance(String totalDistance) 
	{
		this.totalDistance = totalDistance;
	}

	public String getTotalDuration() 
	{
		return totalDuration;
	}

	public void setTotalDuration(String totalDuration) 
	{
		this.totalDuration = totalDuration;
	}
	
	public Location getSource() 
	{
		return source;
	}

	public void setSource(Location source) 
	{
		this.source = source;
	}

	public Location getDestination() 
	{
		return destination;
	}

	public void setDestination(Location destination) 
	{
		this.destination = destination;
	}

	public List<DirectionEntry> getDrivingDirections() 
	{
		return drivingDirections;
	}

	public void setDrivingDirections(DirectionEntry drivingDirection) 
	{
		this.drivingDirections.add(drivingDirection);
	}

	public List<GeoPoint> getMapRouteOverlayDetails() 
	{
		return mapRouteOverlayDetails;
	}

	public void setMapRouteOverlayDetails(GeoPoint mapRoutePoint) 
	{
		this.mapRouteOverlayDetails.add(mapRoutePoint);
	}

	/**
	 * Destination details entity object
	 * 
	 * @author Swapneel.J
	 *
	 */
	public static class Location implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private String name;
		private String latitude = "0";
		private String longitude = "0";
		
		public String getName() 
		{
			return name;
		}
		
		public void setName(String name) 
		{
			this.name = name;
		}
		
		public String getLatitude() 
		{
			return latitude;
		}
		
		public void setLatitude(String latitude) 
		{
			this.latitude = latitude;
		}
		
		public String getLongitude() 
		{
			return longitude;
		}
		
		public void setLongitude(String longitude) 
		{
			this.longitude = longitude;
		}
	}
	
	/**
	 * Direction Entry Class
	 * @author Swapneel.J
	 *
	 */
	public static class DirectionEntry implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private String distance;
		private String duration;
		private String instruction;
		
		public String getDistance() 
		{
			return distance;
		}
		
		public void setDistance(String distance) 
		{
			this.distance = distance;
		}
		
		public String getDuration() 
		{
			return duration;
		}
		
		public void setDuration(String duration) 
		{
			this.duration = duration;
		}
		
		public String getInstruction() 
		{
			return instruction;
		}
		
		public void setInstruction(String instruction) 
		{
			this.instruction = instruction;
		}
	}
}
