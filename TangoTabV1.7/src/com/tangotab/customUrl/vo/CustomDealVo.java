package com.tangotab.customUrl.vo;
/**
 * This class will be used to get deal from deal Id and deal Date.
 * 
 * @author Dillip.Lenka
 *
 */
public class CustomDealVo
{
	private String dealId;
	private String dealDate;
	private String locLat;
	private String locLong;
	/**
	 * @return the dealId
	 */
	public String getDealId() {
		return dealId;
	}
	/**
	 * @param dealId the dealId to set
	 */
	public void setDealId(String dealId) {
		this.dealId = dealId;
	}
	/**
	 * @return the dealDate
	 */
	public String getDealDate() {
		return dealDate;
	}
	/**
	 * @param dealDate the dealDate to set
	 */
	public void setDealDate(String dealDate) {
		this.dealDate = dealDate;
	}
	/**
	 * @return the locLat
	 */
	public String getLocLat() {
		return locLat;
	}
	/**
	 * @param locLat the locLat to set
	 */
	public void setLocLat(String locLat) {
		this.locLat = locLat;
	}
	/**
	 * @return the locLong
	 */
	public String getLocLong() {
		return locLong;
	}
	/**
	 * @param locLong the locLong to set
	 */
	public void setLocLong(String locLong) {
		this.locLong = locLong;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomDealVo [dealId=" + dealId + ", dealDate=" + dealDate
				+ ", locLat=" + locLat + ", locLong=" + locLong
				+ ", getDealId()=" + getDealId() + ", getDealDate()="
				+ getDealDate() + ", getLocLat()=" + getLocLat()
				+ ", getLocLong()=" + getLocLong() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
}
