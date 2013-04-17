package com.tangotab.me.dao;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.utils.ValidationUtil;

/**
 * 
 * @author Lakshmipathi.P
 *
 */
public class MyPhilanthropyDao {

	/*
	 * Method to get myphilanthrophy information.
	 * @param userId
	 * @throws TangoTabException
	 */
	public Map<String, String> getMyPhilanthropy(String userId) throws TangoTabException {
		ConnectionManager cManager = null;
		String responseString = null;
		String uri = AppConstant.MYPHILANTHROPY_METRICS_URL + userId;
		try {
			cManager = new ConnectionManager();
			cManager.setupHttpGet(uri);
			responseString = cManager.getStringResponseForGetRequest();
			return parseResponse(responseString);
		} catch (Exception e) {
			Log.e("Exception ",
					"Exception occuered in getMyPhilanthropy method ", e);
			throw new TangoTabException("MyPhilanthropyDao",
					"getMyPhilanthropy", e);
		}
	}
	
	/*
	 * method for Parsing input response
	 * @param response
	 * @return
	 */
	private Map<String,String> parseResponse(String response){
		JSONObject jsonObject,metricsJsonObj = null;
		Map<String, String> mapResponse = new HashMap<String, String>();
		if(ValidationUtil.isNullOrEmpty(response)){
			mapResponse.put("me", "0");
			mapResponse.put("tangotab", "0");
			mapResponse.put("friends", "0");
			mapResponse.put("potential", "0");
			return mapResponse;
		}
		
		try {
			jsonObject = new JSONObject(response);
			if(!ValidationUtil.isNull(jsonObject)){
				metricsJsonObj = jsonObject.getJSONObject("metrics");
				if(!ValidationUtil.isNull(metricsJsonObj)){
					if(!ValidationUtil.isNullOrEmpty(metricsJsonObj.getString("me"))){
						mapResponse.put("me", metricsJsonObj.getString("me"));
					}else{
						mapResponse.put("me", metricsJsonObj.getString("0"));
					}
					if(!ValidationUtil.isNullOrEmpty(metricsJsonObj.getString("tangotab"))){
						mapResponse.put("tangotab", metricsJsonObj.getString("tangotab"));
					}else{
						mapResponse.put("tangotab", metricsJsonObj.getString("0"));
					}
					if(!ValidationUtil.isNullOrEmpty(metricsJsonObj.getString("friends"))){
						mapResponse.put("friends", metricsJsonObj.getString("friends"));
					}else{
						mapResponse.put("friends", metricsJsonObj.getString("0"));
					}
					if(!ValidationUtil.isNullOrEmpty(metricsJsonObj.getString("potential"))){
						mapResponse.put("potential", metricsJsonObj.getString("potential"));
					}else{
						mapResponse.put("potential", metricsJsonObj.getString("0"));
					}
				}
				
			}
		} catch (JSONException e) {
			Log.e("JSONException ",
					"Exception occuered in getMyPhilanthropy method ", e);
		}
		return mapResponse;
	}

}
