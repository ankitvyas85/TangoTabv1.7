package com.tangotab.nearMe.vo;

import com.tangotab.core.vo.DealsVo;
/**
 * Class for all deal information to be store.
 * 
 * @author dillip.lenka
 *
 */
public class DealsDetailVo extends DealsVo
{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String availableWeekDays;
	private String cuisineTypeId;
	private String dealCreditValue;
	private String dealsFrom;
	private String drivingDistance;
	private String locationRestAddress;
	private String noOfdeals;
	private String noDealsAvailable;
	private String dealAvailableStartDate;
	private String dealEndDate;
	private String restDealStartDate;
	private String phone;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the availableWeekDays
	 */
	public String getAvailableWeekDays() {
		return availableWeekDays;
	}
	/**
	 * @param availableWeekDays the availableWeekDays to set
	 */
	public void setAvailableWeekDays(String availableWeekDays) {
		this.availableWeekDays = availableWeekDays;
	}
	/**
	 * @return the cuisineTypeId
	 */
	public String getCuisineTypeId() {
		return cuisineTypeId;
	}
	/**
	 * @param cuisineTypeId the cuisineTypeId to set
	 */
	public void setCuisineTypeId(String cuisineTypeId) {
		this.cuisineTypeId = cuisineTypeId;
	}
	/**
	 * @return the dealCreditValue
	 */
	public String getDealCreditValue() {
		return dealCreditValue;
	}
	/**
	 * @param dealCreditValue the dealCreditValue to set
	 */
	public void setDealCreditValue(String dealCreditValue) {
		this.dealCreditValue = dealCreditValue;
	}
	/**
	 * @return the dealsFrom
	 */
	public String getDealsFrom() {
		return dealsFrom;
	}
	/**
	 * @param dealsFrom the dealsFrom to set
	 */
	public void setDealsFrom(String dealsFrom) {
		this.dealsFrom = dealsFrom;
	}
	/**
	 * @return the drivingDistance
	 */
	public String getDrivingDistance() {
		return drivingDistance;
	}
	/**
	 * @param drivingDistance the drivingDistance to set
	 */
	public void setDrivingDistance(String drivingDistance) {
		this.drivingDistance = drivingDistance;
	}
	/**
	 * @return the locationRestAddress
	 */
	public String getLocationRestAddress() {
		return locationRestAddress;
	}
	/**
	 * @param locationRestAddress the locationRestAddress to set
	 */
	public void setLocationRestAddress(String locationRestAddress) {
		this.locationRestAddress = locationRestAddress;
	}
	/**
	 * @return the noOfdeals
	 */
	public String getNoOfdeals() {
		return noOfdeals;
	}
	/**
	 * @param noOfdeals the noOfdeals to set
	 */
	public void setNoOfdeals(String noOfdeals) {
		this.noOfdeals = noOfdeals;
	}
	/**
	 * @return the noDealsAvailable
	 */
	public String getNoDealsAvailable() {
		return noDealsAvailable;
	}
	/**
	 * @param noDealsAvailable the noDealsAvailable to set
	 */
	public void setNoDealsAvailable(String noDealsAvailable) {
		this.noDealsAvailable = noDealsAvailable;
	}
	/**
	 * @return the dealAvailableStartDate
	 */
	public String getDealAvailableStartDate() {
		return dealAvailableStartDate;
	}
	/**
	 * @param dealAvailableStartDate the dealAvailableStartDate to set
	 */
	public void setDealAvailableStartDate(String dealAvailableStartDate) {
		this.dealAvailableStartDate = dealAvailableStartDate;
	}
	/**
	 * @return the dealEndDate
	 */
	public String getDealEndDate() {
		return dealEndDate;
	}
	/**
	 * @param dealEndDate the dealEndDate to set
	 */
	public void setDealEndDate(String dealEndDate) {
		this.dealEndDate = dealEndDate;
	}
	/**
	 * @return the restDealStartDate
	 */
	public String getRestDealStartDate() {
		return restDealStartDate;
	}
	/**
	 * @param restDealStartDate the restDealStartDate to set
	 */
	public void setRestDealStartDate(String restDealStartDate) {
		this.restDealStartDate = restDealStartDate;
	}
		
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DealsDetailVo [id=" + id + ", availableWeekDays="
				+ availableWeekDays + ", cuisineTypeId=" + cuisineTypeId
				+ ", dealCreditValue=" + dealCreditValue + ", dealsFrom="
				+ dealsFrom + ", drivingDistance=" + drivingDistance
				+ ", locationRestAddress=" + locationRestAddress
				+ ", noOfdeals=" + noOfdeals + ", noDealsAvailable="
				+ noDealsAvailable + ", dealAvailableStartDate="
				+ dealAvailableStartDate + ", dealEndDate=" + dealEndDate
				+ ", restDealStartDate=" + restDealStartDate + ", phone="
				+ phone + ", businessName=" + businessName + ", address="
				+ address + ", dealName=" + dealName + ", dealDescription="
				+ dealDescription + ", dealRestriction=" + dealRestriction
				+ ", imageUrl=" + imageUrl + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", getId()=" + getId()
				+ ", getAvailableWeekDays()=" + getAvailableWeekDays()
				+ ", getCuisineTypeId()=" + getCuisineTypeId()
				+ ", getDealCreditValue()=" + getDealCreditValue()
				+ ", getDealsFrom()=" + getDealsFrom()
				+ ", getDrivingDistance()=" + getDrivingDistance()
				+ ", getLocationRestAddress()=" + getLocationRestAddress()
				+ ", getNoOfdeals()=" + getNoOfdeals()
				+ ", getNoDealsAvailable()=" + getNoDealsAvailable()
				+ ", getDealAvailableStartDate()="
				+ getDealAvailableStartDate() + ", getDealEndDate()="
				+ getDealEndDate() + ", getRestDealStartDate()="
				+ getRestDealStartDate() + ", getPhone()=" + getPhone()
				+ ", getBusinessName()=" + getBusinessName()
				+ ", getAddress()=" + getAddress() + ", getDealName()="
				+ getDealName() + ", getDealDescription()="
				+ getDealDescription() + ", getDealRestriction()="
				+ getDealRestriction() + ", getImageUrl()=" + getImageUrl()
				+ ", getStartTime()=" + getStartTime() + ", getEndTime()="
				+ getEndTime() + ", getUserId()=" + getUserId()
				+ ", getPassword()=" + getPassword() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + "]";
	}
		
	
	
}
