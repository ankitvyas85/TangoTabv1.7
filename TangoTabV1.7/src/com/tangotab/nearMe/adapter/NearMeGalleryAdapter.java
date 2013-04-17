package com.tangotab.nearMe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class NearMeGalleryAdapter extends BaseAdapter{
	private Context ctx;
	int[] pics;
	public NearMeGalleryAdapter(Context c,int[] pics) {
		ctx = c;
		this.pics=pics;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pics.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2)
	{
		ImageView iv = new ImageView(ctx);
		iv.setImageResource(pics[arg0]);
		iv.setScaleType(ImageView.ScaleType.FIT_XY);
		iv.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		iv.setBackgroundResource(pics[arg0]);
		return iv;
	}

}
