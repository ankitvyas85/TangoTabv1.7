package com.tangotab.facebook.dao;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.dao.TangoTabBaseDao;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.login.xmlHandler.UserValidationXmlHandler;
import com.tangotab.signUp.xmlHandler.MessageHandler;

/**
 * 
 * @author Lakshmipathi.P
 * 
 */
public class FacebookLoginDao extends TangoTabBaseDao 
{
	private TangoTabBaseDao instance;
	private ConnectionManager cManager;
	private MessageHandler msgHandler;
	private UserValidationXmlHandler userValidationXmlHandler;

	public FacebookLoginDao() {
		instance = TangoTabBaseDao.getInstance();
		cManager = new ConnectionManager();
		msgHandler = new MessageHandler();
		userValidationXmlHandler = new UserValidationXmlHandler();
	}

	/**
	 * 
	 * @param userName
	 * @param facebookId
	 * @return
	 * @throws ConnectTimeoutException
	 * @throws TangoTabException
	 */
	public Map<String, String> checkForUser(String userName, String facebookId)throws ConnectTimeoutException,TangoTabException 
	{

		Log.v("Invoking method checkForUser() with parameter username and password :",
				userName + "," + facebookId);
		String message = null;
		Map<String, String> response = new HashMap<String, String>();
		String isActiveUrl = AppConstant.baseUrl + "/isactiveaccount?emailId="
				+ userName + "&facebookId=" + facebookId;
		Log.v("isActiveUrl is", isActiveUrl);
		instance.getXmlReader().setContentHandler(userValidationXmlHandler);
		cManager.setupHttpGet(isActiveUrl);
		
		try {
			InputSource m_is = cManager.makeGetRequestGetResponse();
			if (m_is != null) {
				instance.getXmlReader().parse(m_is);
				message = userValidationXmlHandler.getMessage();
				response.put("message", message);
				response.put("zipCode", userValidationXmlHandler.zip_code);
				response.put("userId", userValidationXmlHandler.user_Id);
				Log.v("Response Message is", message);
			}
		} catch (ConnectTimeoutException exe) 
		{
			Log.e("ConnectTimeoutException ", "ConnectTimeoutException occuered in checkForUser method ",exe);
			throw new ConnectTimeoutException(exe.getLocalizedMessage());
		} catch (IOException e) {
			Log.e("Exception ", "IOException occuered in checkForUser method ",
					e);
			throw new TangoTabException("FacebookLoginDao", "checkForUser", e);
		} catch (SAXException e) {
			Log.e("Exception ",
					"SAXException occuered in checkForUser method ", e);
			throw new TangoTabException("FacebookLoginDao", "checkForUser", e);
		}
		return response;
	}

	/**
	 * 
	 * @param signupDetailsRequest
	 * @return
	 * @throws TangoTabException
	 */
	public String signUpToTangoTab(StringEntity signupDetailsRequest)throws ConnectTimeoutException,TangoTabException 
	{
		String message = null;		
		
		try {
			instance.getXmlReader().setContentHandler(msgHandler);
			cManager.initializePutURL(AppConstant.baseUrl + '/' + "signup");
			cManager.goPutIt(AppConstant.baseUrl + '/' + "signup",signupDetailsRequest);
			String response = cManager.getPutResponse();
			InputSource m_is = new InputSource();
			m_is.setCharacterStream(new StringReader(response));
			if (m_is != null) {
				instance.getXmlReader().parse(m_is);
				message = msgHandler.getMessage();
				Log.v("Response Message is", message);
			}
		}
		catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException ","ConnectTimeoutException occuered in signUpToTangoTab method ", e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			Log.e("Exception ","IOException occuered in signUpToTangoTab method ", e);
			throw new TangoTabException("FacebookLoginDao", "signUpToTangoTab",e);
		} 
		catch (SAXException e)
		{
			Log.e("Exception ",	"SAXException occuered in signUpToTangoTab method ", e);
			throw new TangoTabException("FacebookLoginDao", "signUpToTangoTab",	e);
		}

		return message;
	}

	/**
	 * 
	 * @param userName
	 * @param postalZip
	 * @return
	 * @throws ConnectTimeoutException
	 * @throws TangoTabException
	 */
	public String updateZipForUser(String userName, String postalZip,String workZip)throws ConnectTimeoutException,TangoTabException 
	{
		String message = null;
		String updateZipUrl =null;
		if(!ValidationUtil.isNullOrEmpty(workZip))
			updateZipUrl = AppConstant.baseUrl + "/updateZipCode?emailId="+ userName + "&zipCode=" + postalZip+"&workZip=" +workZip;
		else
			updateZipUrl = AppConstant.baseUrl + "/updateZipCode?emailId="+ userName + "&zipCode=" + postalZip;
		Log.v("updateZipUrl is", updateZipUrl);
		instance.getXmlReader().setContentHandler(msgHandler);
		cManager.setupHttpGet(updateZipUrl);		
		try {
			InputSource m_is = cManager.makeGetRequestGetResponse();
			if (m_is != null) {
				instance.getXmlReader().parse(m_is);
				message = msgHandler.getMessage();
				Log.v("Response Message is", message);
			}
		}
		catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException ","ConnectTimeoutException occuered in updateZipForUser method ", e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		} 
		catch (IOException e)
		{
			Log.e("Exception ","IOException occuered in updateZipForUser method ", e);
			throw new TangoTabException("FacebookLoginDao", "updateZipForUser",e);
		} 
		catch (SAXException e)
		{
			Log.e("Exception ","SAXException occuered in updateZipForUser method ", e);
			throw new TangoTabException("FacebookLoginDao", "updateZipForUser",e);
		}
		return message;
	}
	/**
	 * Update the social network url for both face book and twitter sharing 
	 * @param userName
	 * @param facebook
	 * @param twitter
	 * @return
	 * @throws ConnectTimeoutException
	 * @throws TangoTabException
	 */
	public String updateSocialPreferences(String userName, String facebook,String twitter)throws ConnectTimeoutException,TangoTabException 
	{
		String message = null;
		String socialUrl = AppConstant.baseUrl + "/updateSocialPref?emailId="+ userName + "&twitter=" + twitter+"&facebook=" +facebook;
		Log.v("Facebook and twitter web service URL is", socialUrl);
		instance.getXmlReader().setContentHandler(msgHandler);
		cManager.setupHttpGet(socialUrl);		
		try {
			InputSource m_is = cManager.makeGetRequestGetResponse();
			if (m_is != null) {
				instance.getXmlReader().parse(m_is);
				message = msgHandler.getMessage();
				Log.v("Response Message is", message);
			}
		}
		catch (ConnectTimeoutException e)
		{
			Log.e("ConnectTimeoutException ","ConnectTimeoutException occuered in updateSocialPreferences method ", e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		} 
		catch (IOException e)
		{
			Log.e("Exception ","IOException occuered in updateSocialPreferences method ", e);
			throw new TangoTabException("FacebookLoginDao", "updateSocialPreferences",e);
		} 
		catch (SAXException e)
		{
			Log.e("Exception ","SAXException occuered in updateSocialPreferences method ", e);
			throw new TangoTabException("FacebookLoginDao", "updateSocialPreferences",e);
		}
		return message;
	}
}