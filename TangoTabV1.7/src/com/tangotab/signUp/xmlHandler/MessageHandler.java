package com.tangotab.signUp.xmlHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * Parse the Xml and retrieve the message
 * @author dillip.lenka
 *
 */
public class MessageHandler extends DefaultHandler
{
	
	boolean in_message=false;	
	public static String message = "";
	
	
	@Override
	public void startDocument() throws SAXException 
	{
		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes atts) throws SAXException 
	{
		if (localName.equals("message")) {
			in_message = true;
		}
		
	}
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (in_message) {
			message = new String(ch, start, length);
			
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("message")) {
			in_message = false;
		}
		
	}
	
	@Override
	public void endDocument() throws SAXException
	{		
		
	}
	
	public static String getMessage() {
		return message;
	}

	public static void setMessage(String message) {
		MessageHandler.message = message;
	}
		
}
