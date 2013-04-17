package com.tangotab.calendar.utils;



import java.util.ArrayList;

public class CalendarEvent {
	
	private String reference;
	public ArrayList<String> start = new ArrayList<String>();
	
	public CalendarEvent(){}
	public CalendarEvent(String reference, ArrayList<String> start){
		this.reference = reference;
		this.start = start;
	}
	
	public CalendarEvent(CalendarEvent a){
		this(a.reference, a.start);
	}
	
	public String getReference(){
		return reference;
	}
	
	
	public String getStart(int pos){
		return start.get(pos);
	}
	
	public void setReference (String reference) {
		this.reference = reference;
	}
	

	public void setStart (String start) {
		this.start.add(start);
	}
}

