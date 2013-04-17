package com.tangotab.search.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tangotab.R;
import com.tangotab.claimOffer.activity.ClaimOfferActivity;
import com.tangotab.core.utils.ImageLoader;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.nearMe.vo.DealsDetailVo;

/**
 * Adapter class to be used for displaying list of deals in layout.
 * 
 * <br> Class :SearchListAdapter
 * <br> Layout :dealslistitems.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class SearchListAdapter extends BaseAdapter 
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
	private TextView date;
	TextView newDeals,newDeals1;
	private TextView nodealsavailable;	
	private int selectedPosition;
	private String country;
	private RotateAnimation rotate;
	/**
	 * Constructor for near me list adapter.
	 * @param context
	 * @param dealsDetailList
	 * @param locationSearch
	 */
	public SearchListAdapter(Context context,List<DealsDetailVo> dealsDetailList,boolean locationSearch,int selectedPosition,String country) 
	{
		this.context=context;
		this.dealsDetailList = dealsDetailList;
		this.selectedPosition =selectedPosition;
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
						
			/*String restDeal_Available_startDate = dealsDetailVo.getDealAvailableStartDate();
			String rest[] = restDeal_Available_startDate.split("-");
			String rest1;
			if (rest.length > 2)
				rest1 = rest[1] + "-" + rest[2] + "-" + rest[0];
			else
				rest1 = rest[1] + "-" + rest[0];*/
	
			// Refer to TextView from Layout
			businessname = (TextView) convertView.findViewById(R.id.businessname);
			businessname.setText(dealsDetailVo.getBusinessName());
			
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
			/* 
			 AnimationSet snowMov1 = new AnimationSet(true);
			 RotateAnimation rotate1 = new RotateAnimation(0,-45, Animation.RELATIVE_TO_SELF,0.5f , Animation.RELATIVE_TO_SELF,0);
			 rotate1.setDuration(0);
			  snowMov1.addAnimation(rotate1);
		        TranslateAnimation trans1 =  new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.1f, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
		        trans1.setDuration(0);
		        snowMov1.addAnimation(trans1);
		        newDeals.startAnimation(snowMov1);*/
			 /*newDeals1=(TextView) convertView.findViewById(R.id.myImageViewText1);
			newDeals1.setTextColor(Color.RED);
			newDeals1.startAnimation(rotate);*/
			ImageView image = (ImageView) convertView.findViewById(R.id.image);
			imageLoader.DisplayImage(dealsDetailVo.getImageUrl(), image);	
			dealname = (TextView) convertView.findViewById(R.id.dealname);
			dealname.setText(dealsDetailVo.getDealName());
	
			/*date = (TextView) convertView.findViewById(R.id.date);
			date.setText(rest1 + ", " + dealsDetailVo.getStartTime() + " to "+ dealsDetailVo.getEndTime());*/
			nodealsavailable = (TextView) convertView.findViewById(R.id.nodealsavailable);
			String drivingDistance=dealsDetailVo.getDrivingDistance();
			String milesOrKm = " miles";
			if(!ValidationUtil.isNullOrEmpty(country) && country.equalsIgnoreCase("canada")){
				drivingDistance = String.valueOf(new DecimalFormat("##.##").format(Float.parseFloat(drivingDistance)*1.61));
				milesOrKm = " Km";
			}
			//nodealsavailable.setText(dealsDetailVo.getNoDealsAvailable()+ " offers available" + "    " + drivingDistance + " miles");
			nodealsavailable.setText(drivingDistance + milesOrKm+", "+dealsDetailVo.getStartTime() + " - "+ dealsDetailVo.getEndTime());
			/* 
			 * Click Handler for ContentView
			 */
			convertView.setOnClickListener(
					new OnClickListener() 
					{		
						@Override
						public void onClick(View v) 
						{
							try {
								int textPosition = position;
								DealsDetailVo dealsDetailVo = dealsDetailList.get(textPosition);
								ArrayList<DealsDetailVo> businessList=new ArrayList<DealsDetailVo>();
								for(DealsDetailVo dealsVo:dealsDetailList)
						        {
						        	if(dealsDetailVo.businessName.equals(dealsVo.businessName))
						        	{
						        		businessList.add(dealsVo);	
									}
						        	
								}
								//businessList.addAll(dealsDetailList);
	                         	Intent calimedIntent = new Intent(context,ClaimOfferActivity.class);	                         	
	                         	calimedIntent.putExtra("isFromSearch",  true);
								calimedIntent.putExtra("from",  "search");
								calimedIntent.putExtra("selectDeal",  dealsDetailVo);
								calimedIntent.putExtra("businessList", businessList);
								calimedIntent.putExtra("country", country);
								calimedIntent.putExtra("fromsearch","fromsearch");
								calimedIntent.putExtra("selectedPosition",selectedPosition);								
								calimedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								context.startActivity(calimedIntent);

						} catch (Exception e)
						{
							Log.e("Exception", e.getLocalizedMessage(), e);
						}
							
			             }
					});
			convertView.setTag(dealsDetailVo);
			return convertView;		
	}
	
	
}
