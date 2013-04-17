package com.tangotab.myOffers.xmlHandler;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.util.Log;

import com.tangotab.core.xmlHandler.BaseSAXHandler;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
/**
 * Parse the XML Response and generate a list of Offer details object
 * @author Dillip.Lenka
 *
 */
public class OfferDetailHandler extends BaseSAXHandler
{
	private StringBuffer sBuffer = null;
	private int intParsingScope = PARSING_INACTIVE;
	private static final int PARSING_INACTIVE = 0;
	private static final int PARSE_FEEDS_LIST = 1;
	
	List<OffersDetailsVo> offersList= null;
	OffersDetailsVo offersDetailsVo = null;
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		if (localName.equals("mydeal_details"))
		{
			offersList = new ArrayList<OffersDetailsVo>();
		}
		else if (localName.equals("detail"))
		{
			offersDetailsVo = new OffersDetailsVo();
		}
		intParsingScope = PARSE_FEEDS_LIST;
		if (intParsingScope != PARSING_INACTIVE)
			sBuffer = new StringBuffer();
	}

	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException
	{
		switch (intParsingScope)
		{
			case (PARSE_FEEDS_LIST):
			{
				if(localName.equalsIgnoreCase("noOfdeals"))
				{
					offersDetailsVo.setNoOfDeals(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("con_res_id"))
				{
					offersDetailsVo.setConResId(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("deal_id"))
				{
					offersDetailsVo.setDealId(sbToString(sBuffer));					
				}
				else if(localName.equalsIgnoreCase("deal_name"))
				{
					offersDetailsVo.setDealName(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("isconsumershownup"))
				{
					offersDetailsVo.setIsConsumerShownUp(sbToString(sBuffer));					
				}
				else if(localName.equalsIgnoreCase("deal_manager_emailid"))
				{
					offersDetailsVo.setDealManagerEmailId(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("host_manager_emailid"))
				{
					offersDetailsVo.setHostManagerEmailId(sbToString(sBuffer));					
				}
				else if(localName.equalsIgnoreCase("deal_description"))
				{
					offersDetailsVo.setDealDescription(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("deal_restrictions"))
				{
					offersDetailsVo.setDealRestriction(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("start_time"))
				{
					offersDetailsVo.setStartTime(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("end_time"))
				{
					offersDetailsVo.setEndTime(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("business_name"))
				{
					offersDetailsVo.setBusinessName(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("reserved_time_stamp"))
				{
					offersDetailsVo.setReserveTimeStamp(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("address"))
				{
					offersDetailsVo.setAddress(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("image_url"))
				{
					offersDetailsVo.setImageUrl(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("longitude"))
				{
					offersDetailsVo.setLongitude(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("latitude"))
				{
					offersDetailsVo.setLatitude(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("current_date"))
				{
					offersDetailsVo.setCurrentDate(sbToString(sBuffer));	
				}
				else if(localName.equalsIgnoreCase("detail"))
				{
					offersList.add(offersDetailsVo);
				}
			}
			break;
		}
	}

	/** Called to get tag characters ( ex:- <event>AndroidPeople</event>
	 * -- to get event Character ) */
	public void characters(char[] ch, int start, int length) 
	{
		if (intParsingScope == PARSING_INACTIVE || ch == null || length == 0 || sBuffer==null)
			return;
		try
		{
			sBuffer.append(ch, start, length);
		}
		catch (Exception e)
		{
			Log.e(this.getClass().getName(), "characters sBuffer exception:"+e.getMessage() );
		}
	}

	public List<OffersDetailsVo> getOffersDetailList() 
	{
		return offersList;
	}
	public String getLast_seen_timestamp() {
		return ""; 
	}
	public void setLast_seen_timestamp(String last_seen_timestamp) 
	{
	
	}
}
