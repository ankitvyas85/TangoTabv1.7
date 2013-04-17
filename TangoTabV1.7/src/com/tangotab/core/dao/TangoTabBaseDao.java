package com.tangotab.core.dao;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.utils.ValidationUtil;

import android.util.Log;
/**
 * This class will consists all the XML parser configuration informations.
 * 
 * @author dillip.lenka
 *
 */
public class TangoTabBaseDao 
{
	private SAXParserFactory saxParFact;
	private SAXParser saxParser ;
	private XMLReader xmlReader;
	private ConnectionManager conManger;
	
	private static String mark = "-_.!~*'()\"";
	/**
	 * Default constructor where initialize all the instance variables.
	 */
	protected TangoTabBaseDao()
	{
		
		try {
			saxParFact = SAXParserFactory.newInstance();
			saxParser = saxParFact.newSAXParser();
			xmlReader = saxParser.getXMLReader();
			conManger = new ConnectionManager();
		}
		catch (ParserConfigurationException e)
		{	
			Log.e("Exception", "Exception occured in get instance of SaxParser.");
			e.printStackTrace();
		} catch (SAXException e)
		{
			Log.e("Exception", "Exception occured in get instance of SaxParser.");
			e.printStackTrace();
		}
		
	}
	/**
	 * Create an  instance of TangoTabBaseDao
	 * @return
	 */
	public static TangoTabBaseDao getInstance()
	{
		return new TangoTabBaseDao();
	}

	/**
	 * @return the saxParFact
	 */
	public SAXParserFactory getSaxParFact() {
		return saxParFact;
	}

	/**
	 * @param saxParFact the saxParFact to set
	 */
	public void setSaxParFact(SAXParserFactory saxParFact) {
		this.saxParFact = saxParFact;
	}

	/**
	 * @return the saxParser
	 */
	public SAXParser getSaxParser() {
		return saxParser;
	}

	/**
	 * @param saxParser the saxParser to set
	 */
	public void setSaxParser(SAXParser saxParser) {
		this.saxParser = saxParser;
	}

	/**
	 * @return the xmlReader
	 */
	public XMLReader getXmlReader() {
		return xmlReader;
	}

	/**
	 * @param xmlReader the xmlReader to set
	 */
	public void setXmlReader(XMLReader xmlReader) {
		this.xmlReader = xmlReader;
	}
		
	/**
	 * @return the conManger
	 */
	public ConnectionManager getConManger() {
		return conManger;
	}
	/**
	 * @param conManger the conManger to set
	 */
	public void setConManger(ConnectionManager conManger) {
		this.conManger = conManger;
	}
	/**
	 * Encode the given parameter
	 * 
	 * @param argString
	 * @return
	 */
	 public static String encodeURI(String argString)
	 {
		 if(ValidationUtil.isNullOrEmpty(argString))
			 return null;
	        StringBuilder uri = new StringBuilder(); 
	        char[] chars = argString.toCharArray();
	        for(int i = 0; i<chars.length; i++) {
	            char c = chars[i];
	            if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') ||
	               (c >= 'A' && c <= 'Z') || mark.indexOf(c) != -1) {
	                uri.append(c);
	            }
	            else {
	                uri.append("%");
	                uri.append(Integer.toHexString((int)c));
	            }
	        }
	        return uri.toString();
	    }
}
