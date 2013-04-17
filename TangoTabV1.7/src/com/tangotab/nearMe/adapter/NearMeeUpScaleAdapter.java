package com.tangotab.nearMe.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangotab.R;
import com.tangotab.core.utils.ImageLoader;
import com.tangotab.nearMe.vo.DealsDetailVo;

public class NearMeeUpScaleAdapter extends BaseAdapter{
	private ImageLoader imageLoader;
	private Context context;
	private final List<DealsDetailVo> dealsDetailList;
	private LayoutInflater layoutInflater;
	public NearMeeUpScaleAdapter(Context context,List<DealsDetailVo> dealsDetailList,LinearLayout llShowMore){
		this.context=context;
		this.dealsDetailList = dealsDetailList;
		imageLoader = new ImageLoader(context.getApplicationContext(),true);		
		layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);			
	}
	@Override
	public int getCount() {
		
		return dealsDetailList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		DealsDetailVo dealsDetailVo=dealsDetailList.get(arg0);
		ViewHolder holder;
		if(arg1!=null){
		holder=(ViewHolder) arg1.getTag();	
		}
		else{
			holder=new ViewHolder();
			arg1=layoutInflater.inflate(R.layout.upscale_list_item, null);
			arg1.setTag(holder);
		holder.restaurantImg=(ImageView) arg1.findViewById(R.id.ivUpscaleImg);
		holder.name=(TextView)arg1.findViewById(R.id.tvName);
		}
		
		imageLoader.DisplayImage(dealsDetailVo.getImageUrl(), holder.restaurantImg);
		holder.name.setText(dealsDetailVo.getBusinessName());
		return arg1;
	}
public static class ViewHolder{
	ImageView restaurantImg;
	TextView name;
}
}
