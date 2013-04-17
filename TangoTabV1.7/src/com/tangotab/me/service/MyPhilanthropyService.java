package com.tangotab.me.service;

import java.util.Map;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.me.dao.MyPhilanthropyDao;

/**
 * 
 * @author Lakshmipathi.P
 *
 */
public class MyPhilanthropyService {

	/*
	 * Method to get myphilanthrophy information.
	 * @param userId
	 * @throws TangoTabException
	 */
	public Map<String, String> getMyPhilanthropy(String userId) throws TangoTabException {
		MyPhilanthropyDao myPhilanthropyDao = new MyPhilanthropyDao();
		try {
			return myPhilanthropyDao.getMyPhilanthropy(userId);
		} catch (TangoTabException e) {
			Log.e("Exception ",
					"Exception occured at getMyPhilanthropy() method", e);
			throw new TangoTabException("MyPhilanthropyService",
					"getMyPhilanthropy", e);
		}
	}

}
