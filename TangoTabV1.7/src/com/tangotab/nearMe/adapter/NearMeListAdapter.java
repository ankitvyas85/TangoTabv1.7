package com.tangotab.nearMe.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangotab.R;
import com.tangotab.claimOffer.activity.ClaimOfferActivity;
import com.tangotab.core.utils.ImageLoader;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.nearMe.vo.DealsDetailVo;

/**
 * User defined adapter class will be used for  display the list view for list of deal in near me Tab
 * 
 * <bR> Class : NearMeListAdapter
 * <br>Layout : nearme.xml
 * 
 *  @author Dillip.Lenka
 *
 */
public class NearMeListAdapter extends BaseAdapter 
{
	/*
	 * Meta Definitions
	 */
	private ImageLoader imageLoader;
	private Context context;
	private final List<DealsDetailVo> dealsDetailList;
	/*
	 * UI Widgets
	 */
	private LayoutInflater layoutInflater;
	private TextView businessname;
	private TextView dealname;
	private TextView date,newDeals;
	private TextView nodealsavailable;
	private LinearLayout llShowMore;
	private String country;
	private RotateAnimation rotate;
	/**
	 * Constructor for nearmelist adapter.
	 * @param context
	 * @param dealsDetailList
	 * @param locationSearch
	 */
	public NearMeListAdapter(Context context,List<DealsDetailVo> dealsDetailList,LinearLayout llShowMore,String country) 
	{
		this.context=context;
		this.dealsDetailList = dealsDetailList;
		this.llShowMore =llShowMore;
		this.country = country;
		imageLoader = new ImageLoader(context.getApplicationContext());		
		layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		 rotate= (RotateAnimation)AnimationUtils.loadAnimation(context,R.anim.rot);
	}
	
	
	@Override
	public int getCount()
	{
		return dealsDetailList.size();
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
			if(convertView==null)
			{
				convertView=layoutInflater.inflate(R.layout.dealslistitems, null);
			}
			DealsDetailVo  dealsDetailVo= dealsDetailList.get(position);
			String noDeals = dealsDetailVo.getNoOfdeals();
			if (noDeals.contains("'"))
				noDeals = noDeals.replace("'", "");
			int no_deals =0;
			try
			{
			 no_deals = Integer.parseInt(noDeals);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			if(getCount()<no_deals && no_deals!=0)
				llShowMore.setVisibility(View.VISIBLE);
			if(getCount()==no_deals || getCount()<10)
				llShowMore.setVisibility(View.GONE);
			String sdate = dealsDetailVo.getDealAvailableStartDate();			
			String rest[] = sdate.split("-");
			String rest1;
			if (rest.length > 2)
				rest1 = rest[1] + "-" + rest[2] + "-" + rest[0];
			else
				rest1 = rest[1] + "-" + rest[0];
	
			// Refer to TextView from Layout
			businessname = (TextView) convertView.findViewById(R.id.businessname);
			businessname.setText(dealsDetailVo.getBusinessName());
			ImageView image = (ImageView) convertView.findViewById(R.id.image);
			imageLoader.DisplayImage(dealsDetailVo.getImageUrl(), image);	
			dealname = (TextView) convertView.findViewById(R.id.dealname);
			dealname.setText(dealsDetailVo.getDealName());
	
			/*date = (TextView) convertView.findViewById(R.id.date);
			date.setText(rest1 + ", " + dealsDetailVo.getStartTime() + " to "+ dealsDetailVo.getEndTime());
			nodealsavailable = (TextView) convertView.findViewById(R.id.nodealsavailable);*/
			nodealsavailable = (TextView) convertView.findViewById(R.id.nodealsavailable);
			String drivingDistance=dealsDetailVo.getDrivingDistance();
			String milesOrKm = " miles";
			StringBuilder dealsAvailable = new StringBuilder();
					
			if(!ValidationUtil.isNullOrEmpty(country) && country.equalsIgnoreCase("canada")){
				drivingDistance = String.valueOf(new DecimalFormat("##.##").format(Float.parseFloat(drivingDistance)*1.61));
				milesOrKm = " Km";
			}

			nodealsavailable.setText(dealsAvailable.append(drivingDistance).append(milesOrKm).append(", ").append(dealsDetailVo.getStartTime()).append(" - ").append(dealsDetailVo.getEndTime()));
			newDeals=(TextView) convertView.findViewById(R.id.myImageViewText);
		    String availableDeals=dealsDetailVo.getNoDealsAvailable();
		    if(availableDeals.length()>1){
			newDeals.setText(" "+dealsDetailVo.getNoDealsAvailable()+" "+"\n"+"LEFT");
		    }
		    else{
		    	newDeals.setText("  "+dealsDetailVo.getNoDealsAvailable()+"\n"+"LEFT");	
		    }
			//  newDeals.setTextColor(Color.RED);
		    //v.setText(" 4 "+"\n"+"LEFT");
			
			 newDeals.startAnimation(rotate);
			convertView.setTag(dealsDetailVo);
			return convertView;		
	}
	
}
