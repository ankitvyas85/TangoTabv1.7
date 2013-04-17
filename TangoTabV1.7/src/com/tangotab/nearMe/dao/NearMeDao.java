package com.tangotab.nearMe.dao;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.dao.TangoTabBaseDao;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.utils.RestServiceUtil;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.nearMe.vo.NearMeVo;
import com.tangotab.nearMe.xmlHandler.DealDetailHandler;
/**
 * Dao class for get list of Deals from near me Object.
 * 
 * @author dillip.lenka
 *
 */
public class NearMeDao extends TangoTabBaseDao
{
	/**
	 * Get the List of deals list from nearme object
	 * @param nearMeVo
	 * @return
	 */
	public List<DealsDetailVo> getDealsList(NearMeVo nearMeVo)throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Invoking the method getDealsList() with parameter nearMeVo is", nearMeVo.toString());
		String nearMeUrl = null;
		nearMeUrl = getNearMeUrl(nearMeVo);
		Log.v("nearMeUrl url", nearMeUrl);
		TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
		DealDetailHandler nearHandler = new DealDetailHandler();
		ConnectionManager cManager = instance.getConManger();
		cManager.setupHttpGet(nearMeUrl);
		instance.getXmlReader().setContentHandler(nearHandler);
		InputSource m_is=null;
		
		List<DealsDetailVo> dealsList=null;
		try {
			if(AppConstant.dev_lat != 0.0 && AppConstant.dev_lang != 0.0)
			{
				m_is = cManager.makeGetRequestGetResponse();
			}
			if (m_is != null)
			{
				instance.getXmlReader().parse(m_is);			
				 dealsList = nearHandler.getDealsList();
				 Log.v("dealsList url", String.valueOf(dealsList.size()));
			}
		}
		catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException occured ", "ConnectTimeoutException occured in getDealsList() method ", e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		}
		
		catch (IOException e)
		{
			Log.e("Exception occured ", "IOException occured in getDealsList() method ", e);
			throw new TangoTabException("NearMeDao", "getDealsList", e);
		} catch (SAXException e)
		{
			Log.e("Exception occured ", "SAXException occured in getDealsList() method ", e);
			throw new TangoTabException("NearMeDao", "getDealsList", e);
		}
		return dealsList;
	}

	/**
	 * Get the near me url from nermeVo object.
	 * @param nearMeVo
	 * @return
	 */
	private String getNearMeUrl(NearMeVo nearMeVo)
	{
		Log.v("Invoking the method getNearMeUrl() with parameter nearMeVo is", nearMeVo.toString());
		String nearMeurl = AppConstant.baseUrl+"/deals/search?type="+TangoTabBaseDao.encodeURI(nearMeVo.getType())+"&city="+ TangoTabBaseDao.encodeURI(nearMeVo.getCityName())+ "&zipcode="
				+ TangoTabBaseDao.encodeURI(nearMeVo.getZipCode())+ "&coordinate="+AppConstant.dev_lat+","+AppConstant.dev_lang+ "&searchingradius="+nearMeVo.getSetDistance()
				+ "&pageIndex="+nearMeVo.getPageIndex()+ "&noOfdeals=0&restName=&address="+"&version="+nearMeVo.getAppVersion()+"&userId="+ nearMeVo.getUserId()
				+"&diningId="+nearMeVo.getDiningId()+"&timeRange="+nearMeVo.getMinRange()+","+nearMeVo.getMaxRange()+"&date="+TangoTabBaseDao.encodeURI(nearMeVo.getDate());
		return nearMeurl;
	}
	
}
