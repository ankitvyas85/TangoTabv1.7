package com.tangotab.login.xmlHandler;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.tangotab.signUp.vo.UserVo;

/**
 * This class will be used to parse the response and store into uservo list
 * 
 * @author dillip.lenka
 *
 */

public class UserValidationXmlHandler extends DefaultHandler 
{
	
	boolean in_message,in_user_details, in_first_name, in_last_name, in_zip_code,in_work_zip,in_userId,
			in_mobile_phone = false,in_facebook_share,in_twitter_share;
	public String user_Id,first_name, last_name, zip_code,work_zip, mobile_phone = "",facebook_share,twitter_share;
	public String message="";
	
	public List<UserVo> userDetailsList = new ArrayList<UserVo>(); 
	@Override
	public void startDocument() throws SAXException
	{	
		message = "";
		userDetailsList.clear();
	}

	@Override
	public void startElement(String uri, String localName, String qName,Attributes atts) throws SAXException
	{		
		if (localName.equals("message")){
			in_message = true;			
		} 		
		if (localName.equals("user_details")){
			in_user_details = true;
		}
		else if (localName.equals("user_id")){
			in_userId = true;
		} 
		else if (localName.equals("first_name")) {
			in_first_name = true;
		} else if (localName.equals("last_name")) {
			in_last_name = true;
		} else if (localName.equals("zip_code")) {
			in_zip_code = true;
		}
		else if (localName.equals("work_zip")) {
			in_work_zip = true;
		}
		else if (localName.equals("facebook_share")) {
			in_facebook_share = true;
		}
		else if (localName.equals("twitter_share")) {
			in_twitter_share = true;
		} 
		else if (localName.equals("mobile_phone")) {
			in_mobile_phone = true;
		} 
		
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (in_message) {
			message = new String(ch, start, length);
			
		} 
		if (in_user_details && in_first_name) {
			first_name = new String(ch, start, length);			
		} else if (in_user_details && in_last_name) {
			last_name = new String(ch, start, length);			
		} else if (in_user_details && in_last_name) {
			last_name = new String(ch, start, length);			
		} else if (in_user_details && in_zip_code) {
			zip_code = new String(ch, start, length);			
		}else if (in_user_details && in_work_zip) {
			work_zip = new String(ch, start, length);	
		}else if (in_user_details && in_facebook_share) {
			facebook_share = new String(ch, start, length);	
		}else if (in_user_details && in_twitter_share) {
			twitter_share = new String(ch, start, length);	
		}else if (in_user_details && in_mobile_phone) {
			mobile_phone = new String(ch, start, length);			
		} 
		else if(in_user_details&&in_userId){
			user_Id = new String(ch, start, length);			
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException
	{
		if (localName.equals("message")) {
			in_message = false;
		} 
		if (localName.equals("user_details")) {
			in_user_details = false;
		} else if (localName.equals("first_name")) {
			in_first_name = false;
		} else if (localName.equals("last_name")) {
			in_last_name = false;
		} else if (localName.equals("zip_code")) {
			in_zip_code = false;
		}else if (localName.equals("work_zip")) {
			in_work_zip = false;
		}else if (localName.equals("user_id")) {
			in_userId = false;
		} 
		else if (localName.equals("facebook_share")) {
			in_facebook_share = false;
		}
		else if (localName.equals("twitter_share")) {
			in_twitter_share = false;
		}
		else if (localName.equals("mobile_phone"))
		{
			in_mobile_phone = false;
			UserVo userVo = new UserVo(first_name, last_name, zip_code,work_zip,facebook_share,twitter_share,mobile_phone,user_Id,null,null,null);
			userDetailsList.add(userVo);		
		
		} 
	}

	@Override
	public void endDocument() throws SAXException
	{
		// Nothing to do
	
	}

	public List<UserVo> getUserDetailsList()
	{
		return userDetailsList;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String userDetails()
	{
		return first_name+" "+last_name;
	}
}