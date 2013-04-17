package com.tangotab.signUp.dao;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.dao.TangoTabBaseDao;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.signUp.vo.UserVo;
import com.tangotab.signUp.xmlHandler.MessageHandler;
/**
 * Dao class for new user to sign up and get Authentication to the user.
 * 
 * @author dillip.lenka
 *
 */
public class SignUpDao extends TangoTabBaseDao
{
	/**
	 * New user to sign up and get Authentication to login into TangoTab Application.
	 * @param userVo
	 * @return
	 * @throws TangoTabException
	 */
	public String signUp(UserVo userVo) throws ConnectTimeoutException,TangoTabException
	{
		Log.v("Method invocation to signUp method with  parameter as userVo", userVo.toString());
		String message =null;
		try {	
			String signUpUrl = AppConstant.baseUrl + '/' + "signup";
			StringEntity requestEntity = signupDetailsRequest(userVo);
			TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
			MessageHandler xHandler = new MessageHandler();
			instance.getXmlReader().setContentHandler(xHandler);				
			ConnectionManager cManager = instance.getConManger();
			cManager.initializePutURL(signUpUrl);
			cManager.goPutIt(signUpUrl,requestEntity);
					
			String response = cManager.getPutResponse();
			InputSource m_is = new InputSource();
			m_is.setCharacterStream(new StringReader(response));
			if (m_is != null)
			{
				instance.getXmlReader().parse(m_is);
				message = xHandler.getMessage();
				Log.v("Sign up response message ", message);
			}
		}
		catch (ConnectTimeoutException e)
		{	
			Log.e("ConnectTimeoutException :", "ConnectTimeoutException occured in Signup service respone ", e);
			throw new ConnectTimeoutException(e.getLocalizedMessage());
		}
		catch (IOException e)
		{	
			Log.e("Exception :", "IOException occured in Signup service respone ", e);
			throw new TangoTabException("ForgetPasswordService", "forgetPasswordService", e);
		}
		catch (SAXException e)
		{
			Log.e("Exception :", "SAXException occured in forget service respone ", e);
			throw new TangoTabException("ForgetPasswordService", "forgetPasswordService", e);
		}		
		return message;
	}
	
	
	
	/**
	 * This will generate a string entity for user request.
	 * 
	 * @param fName
	 * @param lName
	 * @param newMail
	 * @param newPassword
	 * @param mNumber
	 * @param newZip
	 * @param promoText
	 * @return
	 */
	private StringEntity signupDetailsRequest(UserVo userVo)
	{
		Log.v("Method invocation to signupDetailsRequest method with  parameter as userVo", userVo.toString());
		try {
			XmlSerializer serializer = Xml.newSerializer();
			StringWriter writer = new StringWriter();
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "signup");

			serializer.startTag(null, "firstname");
			serializer.text(userVo.getFirst_name());
			serializer.endTag(null, "firstname");

			serializer.startTag(null, "lastname");
			serializer.text(userVo.getLast_name());
			serializer.endTag(null, "lastname");

			serializer.startTag(null, "email");
			serializer.text(userVo.getEmail());
			serializer.endTag(null, "email");

			serializer.startTag(null, "password");
			serializer.text(userVo.getPassword());
			serializer.endTag(null, "password");

			serializer.startTag(null, "mobileno");
			serializer.text(userVo.getMobile_phone());
			serializer.endTag(null, "mobileno");

			serializer.startTag(null, "zipcode");
			serializer.text(userVo.getZip_code());
			serializer.endTag(null, "zipcode");

			serializer.startTag(null, "workzip");
			serializer.text(userVo.getWork_zip());
			serializer.endTag(null, "workzip");
			
			serializer.startTag(null, "promocode");
			serializer.text(userVo.getPromoText());
			serializer.endTag(null, "promocode");

			serializer.endTag(null, "signup");
			serializer.endDocument();
			return new StringEntity(writer.toString());
		} catch (Exception e) 
		{
			Log.e("Exception :", "Exception  occured in signupDetailsRequest method ", e);
			return null;
		}
	}
	
	
}
