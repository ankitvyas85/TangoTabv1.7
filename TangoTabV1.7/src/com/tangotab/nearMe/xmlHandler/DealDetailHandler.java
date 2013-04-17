package com.tangotab.nearMe.xmlHandler;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.util.Log;

import com.tangotab.core.xmlHandler.BaseSAXHandler;
import com.tangotab.nearMe.vo.DealsDetailVo;
/**
 * Parse the response XML Response and get all Deal detail information.
 * 
 * @author Dillip.Lenka
 *
 */
public class DealDetailHandler extends BaseSAXHandler
{
	private StringBuffer sBuffer = null;
	private int intParsingScope = PARSING_INACTIVE;
	private static final int PARSING_INACTIVE = 0;
	private static final int PARSE_FEEDS_LIST = 1;
	
	private List<DealsDetailVo> dealsDetailList;
	private DealsDetailVo dealsDetailVo; 

	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		if (localName.equals("deal_details"))
		{
			dealsDetailList = new ArrayList<DealsDetailVo>();
		}
		else if (localName.equals("deal"))
		{
			dealsDetailVo = new DealsDetailVo();
			dealsDetailVo.setId(attributes.getValue("id"));
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
					dealsDetailVo.setNoOfdeals(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("dealsfrom"))
				{
					dealsDetailVo.setDealsFrom(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("business_name"))
				{
					dealsDetailVo.setBusinessName(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("cuisine_type_id"))
				{
					dealsDetailVo.setCuisineTypeId(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("deal_name"))
				{
					dealsDetailVo.setDealName(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("rest_deal_restrictions"))
				{
					dealsDetailVo.setDealRestriction(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("driving_distance"))
				{
					dealsDetailVo.setDrivingDistance(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("rest_deal_availablestart_date"))
				{
					dealsDetailVo.setDealAvailableStartDate(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("rest_deal_start_date"))
				{
					dealsDetailVo.setRestDealStartDate(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("rest_deal_end_date"))
				{
					dealsDetailVo.setDealEndDate(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("available_start_time"))
				{
					dealsDetailVo.setStartTime(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("available_end_time"))
				{
					dealsDetailVo.setEndTime(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("available_week_days"))
				{
					dealsDetailVo.setAvailableWeekDays(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("deal_description"))
				{
					dealsDetailVo.setDealDescription(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("deal_credit_value"))
				{
					dealsDetailVo.setDealCreditValue(sbToString(sBuffer));
					
				}
				else if(localName.equalsIgnoreCase("location_rest_address"))
				{
					dealsDetailVo.setLocationRestAddress(sbToString(sBuffer));					
				}
				else if(localName.equalsIgnoreCase("address"))
				{
					dealsDetailVo.setAddress(sbToString(sBuffer));	
					dealsDetailVo.address = dealsDetailVo.address.replace("\n", "");
					
				}
				else if(localName.equalsIgnoreCase("phone"))
				{
					dealsDetailVo.setPhone(sbToString(sBuffer));
				}				
				else if(localName.equalsIgnoreCase("no_deals_available"))
				{
					dealsDetailVo.setNoDealsAvailable(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("image_url"))
				{
					dealsDetailVo.setImageUrl(sbToString(sBuffer));
				}
				else if(localName.equalsIgnoreCase("deal"))
				{
					dealsDetailList.add(dealsDetailVo);
				}
			}
			break;
		}
	}

	
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

	public List<DealsDetailVo> getDealsList() 
	{
		return dealsDetailList;
	}
	public String getLast_seen_timestamp()
	{
		return ""; 
	}

	public void setLast_seen_timestamp(String last_seen_timestamp)
	{
	}
	
}
