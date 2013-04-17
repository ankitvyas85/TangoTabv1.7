package com.tangotab.core.session;

import java.util.ArrayList;
import java.util.List;

import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.vo.SearchVo;

import android.app.Application;
/**
 * Application class will used for string information into session.
 * 
 * @author dillip.lenka
 *
 */
public class TangoTabBaseApplication extends Application
{
	
	static TangoTabBaseApplication tangoTabBaseApplication;
	
	
	public static boolean isdialogpopsup;
	public static boolean isIsdialogpopsup() {
		return isdialogpopsup;
	}
	public static void setIsdialogpopsup(boolean isdialogpopsup) {
		TangoTabBaseApplication.isdialogpopsup = isdialogpopsup;
	}
	/**
	 * Variable will be used for store install date of TangoTab application..
	 */
	private String installDate;
	
	/**
	 * Variable will be used for store list of deals in search page.
	 */
	private List<DealsDetailVo> searchList = new ArrayList<DealsDetailVo>();
	
	/**
	 * Variable will be used for store list of offers in my offers page.
	 */
	private List<OffersDetailsVo> offersList = new ArrayList<OffersDetailsVo>();
	
	/**
	 * Variable will be used for store list of deals in near me page.
	 */
	private List<DealsDetailVo> dealsList = new ArrayList<DealsDetailVo>();
	/**
	 * Variable will be used to Store the search vo object
	 */
	private SearchVo searchVo;
	/**
	 * Variable will be used for store page count.
	 */
	private int nearMePageCount;	
	
	/**
	 * Variable will be used for store page count for my offer page.
	 */
	private int myOfferPageCount;	
	/**
	 * Custom url handler URL spMailingID
	 */
	private String spMailingID;
	/**
	 * Custom url handler URL spUserId
	 */
	private String spUserId;
	
	/**
	 * Custom url handler URL spJobId
	 */
	private String spJobId;
	/**
	 * appVerison to store the current version of the application.
	 */
	private String appVerison;
	
	
	ArrayList<Integer> shareflow;
	
	
	public ArrayList<Integer> getShareflow() {
		return shareflow;
	}
	public void setShareflow(ArrayList<Integer> shareflow) {
		this.shareflow = shareflow;
	}
	/**
	 * @return the installDate
	 */
	public String getInstallDate()
	{
		return installDate;
	}
	/**
	 * @param installDate the installDate to set
	 */
	public void setInstallDate(String installDate) {
		this.installDate = installDate;
	}
	/**
	 * @return the searchList
	 */
	public List<DealsDetailVo> getSearchList() {
		return searchList;
	}
	/**
	 * @param searchList the searchList to set
	 */
	public void setSearchList(List<DealsDetailVo> searchList) {
		this.searchList = searchList;
	}
	/**
	 * @return the offersList
	 */
	public List<OffersDetailsVo> getOffersList() {
		return offersList;
	}
	/**
	 * @param offersList the offersList to set
	 */
	public void setOffersList(List<OffersDetailsVo> offersList) {
		this.offersList = offersList;
	}
	/**
	 * @return the dealsList
	 */
	public List<DealsDetailVo> getDealsList() {
		return dealsList;
	}
	/**
	 * @param dealsList the dealsList to set
	 */
	public void setDealsList(List<DealsDetailVo> dealsList) {
		this.dealsList = dealsList;
	}
	/**
	 * @return the searchVo
	 */
	public SearchVo getSearchVo() {
		return searchVo;
	}
	/**
	 * @param searchVo the searchVo to set
	 */
	public void setSearchVo(SearchVo searchVo) {
		this.searchVo = searchVo;
	}

	/**
	 * @return the nearMePageCount
	 */
	public int getNearMePageCount() {
		return nearMePageCount;
	}

	/**
	 * @param nearMePageCount the nearMePageCount to set
	 */
	public void setNearMePageCount(int nearMePageCount) {
		this.nearMePageCount = nearMePageCount;
	}
	/**
	 * @return the myOfferPageCount
	 */
	public int getMyOfferPageCount() {
		return myOfferPageCount;
	}
	/**
	 * @param myOfferPageCount the myOfferPageCount to set
	 */
	public void setMyOfferPageCount(int myOfferPageCount) {
		this.myOfferPageCount = myOfferPageCount;
	}
	/**
	 * @return the spMailingID
	 */
	public String getSpMailingID() {
		return spMailingID;
	}
	/**
	 * @param spMailingID the spMailingID to set
	 */
	public void setSpMailingID(String spMailingID) {
		this.spMailingID = spMailingID;
	}
	/**
	 * @return the spUserId
	 */
	public String getSpUserId() {
		return spUserId;
	}
	/**
	 * @param spUserId the spUserId to set
	 */
	public void setSpUserId(String spUserId) {
		this.spUserId = spUserId;
	}
	/**
	 * @return the spJobId
	 */
	public String getSpJobId() {
		return spJobId;
	}
	/**
	 * @param spJobId the spJobId to set
	 */
	public void setSpJobId(String spJobId) {
		this.spJobId = spJobId;
	}
	/**
	 * @return the appVerison
	 */
	public String getAppVerison() {
		return appVerison;
	}
	/**
	 * @param appVerison the appVerison to set
	 */
	public void setAppVerison(String appVerison) {
		this.appVerison = appVerison;
	}
	public static TangoTabBaseApplication getInstance()
	{
		if(tangoTabBaseApplication==null)
			tangoTabBaseApplication=new TangoTabBaseApplication();
		
		return tangoTabBaseApplication;
	}
		
}