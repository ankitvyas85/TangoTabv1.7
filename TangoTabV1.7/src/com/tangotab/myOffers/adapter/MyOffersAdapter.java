package com.tangotab.myOffers.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangotab.R;
import com.tangotab.core.utils.ImageLoader;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.myOfferDetails.activity.MyoffersDetailActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
/**
 * MY offers adapter for displaying list of offers in the list.
 * 
 * Class Name:MyOffersAdapter
 * XML Use :myofferslistitems.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class MyOffersAdapter extends BaseAdapter
{
	/*
	 * Meta Definations
	 */
	private ImageLoader imageLoader;
	private Context context;
	private final ArrayList<OffersDetailsVo> offersList;
	private RotateAnimation rotate;
	
	/*
	 * UI Widgets
	 */
	private LayoutInflater layoutInflater;
	private TextView businessname;
	private TextView dealname;
	private TextView DealName;
	private TextView con_code;
	private TextView textViewtimeStamp;	
	private LinearLayout llShowMore;
	private TextView address;
	
	
	/**
	 * Constructor for nearmelist adapter.
	 * @param context
	 * @param dealsDetailList
	 * @param locationSearch
	 */
	public MyOffersAdapter(Context context,List<OffersDetailsVo> offersDetailsList,LinearLayout llShowMore) 
	{
		this.context=context;
		this.offersList = (ArrayList<OffersDetailsVo>) offersDetailsList;
		this.llShowMore = llShowMore;
		imageLoader = new ImageLoader(context.getApplicationContext());	
		rotate= (RotateAnimation)AnimationUtils.loadAnimation(this.context,R.anim.rot);
				
	}
	
	
	@Override
	public int getCount()
	{
		return offersList.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return null;
	}

	@Override
	public long getItemId(int arg0) 
	{
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) 
		{
			layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=layoutInflater.inflate(R.layout.myofferslistitems, null);
			

			
			LinearLayout cornerImage=(LinearLayout)convertView.findViewById(R.id.cornerImage);
			OffersDetailsVo  offersDetailsVo= offersList.get(position);
			ImageView image = (ImageView) convertView.findViewById(R.id.mydealimage);
			businessname = (TextView) convertView.findViewById(R.id.businessname);
		//	con_code = (TextView) convertView.findViewById(R.id.confirmationcode);
			address=(TextView)convertView.findViewById(R.id.Address);
			dealname = (TextView) convertView.findViewById(R.id.dealName);
			textViewtimeStamp = (TextView) convertView.findViewById(R.id.timeStamp);
			//Check the offer status
			if(Integer.parseInt(offersDetailsVo.getIsConsumerShownUp())<0)
			{
				/*TextView newDeals=(TextView)convertView.findViewById(R.id.myImageViewText);
				newDeals.setText("Expired");
				newDeals.startAnimation(rotate);*/
				cornerImage.setBackgroundResource(R.drawable.expired);
			    
			}
			if(Integer.parseInt(offersDetailsVo.getIsConsumerShownUp())>0)
			{
				/*TextView newDeals=(TextView)convertView.findViewById(R.id.myImageViewText);
				newDeals.setText("Redemmed");
				newDeals.startAnimation(rotate);*/
				cornerImage.setBackgroundResource(R.drawable.redemeed);
				
				//Redeemed offer put the redemmed offer image.
			}
			if (!ValidationUtil.isNull(offersDetailsVo))
			{
				String currentDate = offersDetailsVo.getReserveTimeStamp();
				if(!ValidationUtil.isNullOrEmpty(currentDate))
				{
				String reserve[] = currentDate.split(" ");
				String rese[] = reserve[0].split("-");
				String totalTime;
				if (rese.length > 2) {
					totalTime = rese[1] + "/" + rese[2] + "/" + rese[0];
				} else
					totalTime = rese[1] + "/" + rese[0];
				textViewtimeStamp.setText(totalTime + ", " + offersDetailsVo. getStartTime() + " to "	+ offersDetailsVo.getEndTime());
				//textViewtimeStamp.setText(totalTime);
				}
				try {
					if (getCount()< Integer.parseInt(offersDetailsVo.getNoOfDeals())){
						llShowMore.setVisibility(View.VISIBLE);
					}

					if (getCount() == Integer.parseInt(offersDetailsVo.getNoOfDeals()) || getCount()<10) {
						llShowMore.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageLoader.DisplayImage(offersDetailsVo.getImageUrl(), image);
				businessname.setText(offersDetailsVo.getBusinessName());
			//	con_code.setText("Confirmation Code: " + offersDetailsVo.getConResId()
			String[] split=offersDetailsVo.getAddress().split(",");
			
			address.setText(split[2].trim());
			dealname.setText(offersDetailsVo.dealName);
			}
			else {
				Log.v("Offer detail object is null", "");
			}
				
			/* 
			 * Click Handler for ContentView
			 */
			convertView.setOnClickListener(
					new OnClickListener() 
					{		
						@Override
						public void onClick(View v) 
						{
							int textPosition = position;
							OffersDetailsVo offersDetailsVo = offersList.get(textPosition);
							if(Integer.parseInt(offersDetailsVo.getIsConsumerShownUp())<0)
							{
								AlertDialog.Builder ab1 = new AlertDialog.Builder(context);
								ab1.setTitle("TangoTab");
								ab1.setMessage("Oops, offer is expired");
								ab1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which)
									{
										dialog.dismiss();
									}
								});
								ab1.create().show();		
							}
						
								
							
							else
							{
								ArrayList<OffersDetailsVo>  newOffersList = new ArrayList<OffersDetailsVo>();
								for(OffersDetailsVo OffersVo:offersList)
								{
									if(offersDetailsVo.getBusinessName().equals(OffersVo.getBusinessName()))
										newOffersList.add(OffersVo);
								}
								Bundle bundle = new Bundle();							
								Intent calimedIntent = new Intent(context,MyoffersDetailActivity.class);
								bundle.putParcelable("selectOffers", offersDetailsVo);
								bundle.putParcelableArrayList("OffersList",newOffersList);
								//calimedIntent.putExtra("index", position);
								calimedIntent.putExtras(bundle);								
								context.startActivity(calimedIntent);
							}
			             }
					});
			convertView.setTag(offersDetailsVo);
			return convertView;		
	}
	}

