package com.tangotab.calendar.utils;



import java.util.Calendar;

public class CalendarDayMarker {
	private int _year;
	private int _month;
	private int _day;
	private int _color;
	private int _resource;
	
	public CalendarDayMarker(int year, int month, int day, int color, int resource) {
		init(year, month, day, color,resource);
	}

	

	
	public CalendarDayMarker(Calendar c, int color, int resource) {
		init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), color, resource);
	}
	
	private void init(int year, int month, int day, int color, int resource) {
		_year = year;
		_month = month;
		_day = day;
		_color = color;
		_resource = resource;
	}
	
	public void setYear(int year) {
		_year = year;
	}
	
	public int getYear() {
		return _year;
	}

	public void setMonth(int month) {
		_month = month;
	}

	public int getMonth() {
		return _month;
	}

	public void setDay(int day) {
		_day = day;
	}

	public int getDay() {
		return _day;
	}

	public void setColor(int color) {
		_color = color;
	}

	public int getColor() {
		return _color;
	}	
	
	public void setResource(int resource) {
		_resource = resource;
	}

	public int getResource() {
		return _resource;
	}	
}
