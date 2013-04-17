package com.tangotab.nearMe.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

public class NearMeGallery extends Gallery{

	public NearMeGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
@Override
public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		float velocityY) {
	 return super.onFling(e1, e2, 0, velocityY); 
}



}
