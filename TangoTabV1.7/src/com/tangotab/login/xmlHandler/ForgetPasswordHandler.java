package com.tangotab.login.xmlHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
/**
 * This class will be used to parse the response for Forget password.
 * 
 * @author dillip.lenka
 *
 */
public class ForgetPasswordHandler extends DefaultHandler
{
	boolean in_details,in_success=false;
	public static String forgetMessage = "";
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		Log.v("Invoking method startElement() with parameters ", "uri ="+uri+" localName ="+localName+" qName= "+qName);
		if(localName.equals("details"))
		{
			in_details = true;
		}
		else if(localName.equals("success"))
		{
			in_success = true;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException
	{
		Log.v("Invoking method characters() with parameters ", "start ="+start+" length ="+length);
		if(in_details && in_success)
		{
			forgetMessage = new String(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		Log.v("Invoking method endElement() with parameters ", "uri ="+uri+" localName ="+localName+" qName= "+qName);
		if(localName.equals("details"))
		{
			in_details = false;
		}
		else if(localName.equals("success"))
		{
			in_success = false;
		}
		
	}
	/**
	 * Get the message message from xml
	 * @return
	 */
	public static String getForgetMessage()
	{
		Log.v("Method invokation :", " Invoking method getForgetMessage() with parameters");
		return forgetMessage;
	}
	
	
	

}
