package com.tangotab.myOffers.Vo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.tangotab.core.vo.DealsVo;
/**
 * Offers information class.
 * 
 * @author Dillip.Lenka
 *
 */
public class OffersDetailsVo extends DealsVo implements Parcelable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String conResId;
	private String currentDate;
	private String dealId;
	private String dealManagerEmailId;
	private String hostManagerEmailId;
	private String isConsumerShownUp;
	private String latitude;
	private String longitude;
	private String reserveTimeStamp;
	private String noOfDeals;
	private String firstName;
	private String lastName;
	private String autoCheckIn;
	
	@Override
	public int describeContents()
	{
		return 0;
	}
	public OffersDetailsVo(){
		
	}
	
	public OffersDetailsVo(Parcel source)
    {
		businessName = source.readString();
		address = source.readString();
		dealName = source.readString();
		dealDescription = source.readString();
		dealRestriction = source.readString();
		imageUrl = source.readString();
		startTime = source.readString();
		endTime = source.readString();
		conResId = source.readString();
		currentDate = source.readString();
		
		dealId = source.readString();
		dealManagerEmailId = source.readString();
		hostManagerEmailId = source.readString();
		isConsumerShownUp = source.readString();
		latitude = source.readString();
		longitude = source.readString();
		reserveTimeStamp = source.readString();
		noOfDeals = source.readString();
		firstName = source.readString();
		lastName = source.readString();
		autoCheckIn= source.readString();
		
    }
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		  Log.v("writeToParcel...", String.valueOf(flags));
	      dest.writeString(businessName);
	      dest.writeString(address);
	      dest.writeString(dealName);
	      dest.writeString(dealDescription);
	      dest.writeString(dealRestriction);
	      dest.writeString(imageUrl);
	      dest.writeString(startTime);
	      dest.writeString(endTime);
	      dest.writeString(conResId);
	      dest.writeString(currentDate);	  	
	      dest.writeString(dealId);
	      dest.writeString(dealManagerEmailId);
	      dest.writeString(hostManagerEmailId);
	      dest.writeString(isConsumerShownUp);
	      dest.writeString(latitude);
	      dest.writeString(longitude);
	      dest.writeString(reserveTimeStamp);
	      dest.writeString(noOfDeals);
	      dest.writeString(firstName);
	      dest.writeString(lastName);
	      dest.writeString(autoCheckIn);		
	}
	public static final Parcelable.Creator<OffersDetailsVo> CREATOR = new Parcelable.Creator<OffersDetailsVo>()
		    {
		        public OffersDetailsVo createFromParcel(Parcel in)
		        {
		            return new OffersDetailsVo(in);
		        }

		        public OffersDetailsVo[] newArray(int size)
		        {
		            return new OffersDetailsVo[size];
		        }
		    };
		    
		    /**
			 * @return the conResId
			 */
			public String getConResId() {
				return conResId;
			}
			/**
			 * @param conResId the conResId to set
			 */
			public void setConResId(String conResId) {
				this.conResId = conResId;
			}
			/**
			 * @return the currentDate
			 */
			public String getCurrentDate() {
				return currentDate;
			}
			/**
			 * @param currentDate the currentDate to set
			 */
			public void setCurrentDate(String currentDate) {
				this.currentDate = currentDate;
			}
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
			 * @return the dealManagerEmailId
			 */
			public String getDealManagerEmailId() {
				return dealManagerEmailId;
			}
			/**
			 * @param dealManagerEmailId the dealManagerEmailId to set
			 */
			public void setDealManagerEmailId(String dealManagerEmailId) {
				this.dealManagerEmailId = dealManagerEmailId;
			}
			/**
			 * @return the hostManagerEmailId
			 */
			public String getHostManagerEmailId() {
				return hostManagerEmailId;
			}
			/**
			 * @param hostManagerEmailId the hostManagerEmailId to set
			 */
			public void setHostManagerEmailId(String hostManagerEmailId) {
				this.hostManagerEmailId = hostManagerEmailId;
			}
			/**
			 * @return the isConsumerShownUp
			 */
			public String getIsConsumerShownUp() {
				return isConsumerShownUp;
			}
			/**
			 * @param isConsumerShownUp the isConsumerShownUp to set
			 */
			public void setIsConsumerShownUp(String isConsumerShownUp) {
				this.isConsumerShownUp = isConsumerShownUp;
			}
			/**
			 * @return the latitude
			 */
			public String getLatitude() {
				return latitude;
			}
			/**
			 * @param latitude the latitude to set
			 */
			public void setLatitude(String latitude) {
				this.latitude = latitude;
			}
			/**
			 * @return the longitude
			 */
			public String getLongitude() {
				return longitude;
			}
			/**
			 * @param longitude the longitude to set
			 */
			public void setLongitude(String longitude) {
				this.longitude = longitude;
			}
			/**
			 * @return the reserveTimeStamp
			 */
			public String getReserveTimeStamp() {
				return reserveTimeStamp;
			}
			/**
			 * @param reserveTimeStamp the reserveTimeStamp to set
			 */
			public void setReserveTimeStamp(String reserveTimeStamp) {
				this.reserveTimeStamp = reserveTimeStamp;
			}
				
			/**
			 * @return the noOfDeals
			 */
			public String getNoOfDeals() {
				return noOfDeals;
			}
			/**
			 * @param noOfDeals the noOfDeals to set
			 */
			public void setNoOfDeals(String noOfDeals) {
				this.noOfDeals = noOfDeals;
			}
			
			/**
			 * @return the firstName
			 */
			public String getFirstName() {
				return firstName;
			}
			/**
			 * @param firstName the firstName to set
			 */
			public void setFirstName(String firstName) {
				this.firstName = firstName;
			}
			/**
			 * @return the lastName
			 */
			public String getLastName() {
				return lastName;
			}
			/**
			 * @param lastName the lastName to set
			 */
			public void setLastName(String lastName) {
				this.lastName = lastName;
			}
			
			/**
			 * @return the autoCheckIn
			 */
			public String getAutoCheckIn() {
				return autoCheckIn;
			}
			/**
			 * @param autoCheckIn the autoCheckIn to set
			 */
			public void setAutoCheckIn(String autoCheckIn) {
				this.autoCheckIn = autoCheckIn;
			}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OffersDetailsVo [conResId=" + conResId + ", currentDate="
				+ currentDate + ", dealId=" + dealId + ", dealManagerEmailId="
				+ dealManagerEmailId + ", hostManagerEmailId="
				+ hostManagerEmailId + ", isConsumerShownUp="
				+ isConsumerShownUp + ", latitude=" + latitude + ", longitude="
				+ longitude + ", reserveTimeStamp=" + reserveTimeStamp
				+ ", businessName=" + businessName + ", address=" + address
				+ ", dealName=" + dealName + ", dealDescription="
				+ dealDescription + ", dealRestriction=" + dealRestriction
				+ ", imageUrl=" + imageUrl + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", getConResId()=" + getConResId()
				+ ", getCurrentDate()=" + getCurrentDate() + ", getDealId()="
				+ getDealId() + ", getDealManagerEmailId()="
				+ getDealManagerEmailId() + ", getHostManagerEmailId()="
				+ getHostManagerEmailId() + ", getIsConsumerShownUp()="
				+ getIsConsumerShownUp() + ", getLatitude()=" + getLatitude()
				+ ", getLongitude()=" + getLongitude()
				+ ", getReserveTimeStamp()=" + getReserveTimeStamp()
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
