package com.tangotab.core.view;



import java.math.BigDecimal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tangotab.R;

public class RangeSeekBar<T extends Number> extends ImageView 
{
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Bitmap thumbImage = BitmapFactory.decodeResource(
			getResources(), R.drawable.seek_thumb_normal);
	private final Bitmap thumbPressedImage = BitmapFactory.decodeResource(
			getResources(), R.drawable.seek_thumb_pressed);
	private final float thumbWidth = thumbImage.getWidth();
	private final float thumbHalfWidth = 0.5f * thumbWidth;
	private final float thumbHalfHeight = 0.5f * thumbImage.getHeight();
	private final float lineHeight = 30.0f;
	private final float padding = thumbHalfWidth;
	private final T absoluteMinValue, absoluteMaxValue;
	private final NumberType numberType;
	private final double absoluteMinValuePrim, absoluteMaxValuePrim;
	private double normalizedMinValue = 0d;
	private double normalizedMaxValue = 1d;
	private Thumb pressedThumb = null;
	private boolean notifyWhileDragging = false;
	private long minVal,maxVal;
	private OnRangeSeekBarChangeListener<T> listener;
   private RangeBarValues rangeBarListener;
	/**
	 * Default color of a SeekBar
	 */
	public final int DEFAULT_COLOR = Color.argb(0xFF, 0xB4, 0xCD, 0x32);

	/**
	 * An invalid pointer id.
	 */
	public final int INVALID_POINTER_ID = 255;
	public final int ACTION_POINTER_UP = 0x6,
			ACTION_POINTER_INDEX_MASK = 0x0000ff00,
			ACTION_POINTER_INDEX_SHIFT = 8;

	private float mDownMotionX;
	private int mActivePointerId = INVALID_POINTER_ID;
	int textWidth;
	/**
	 * On touch, this offset plus the scaled value from the position of the
	 * touch will form the progress value. Usually 0.
	 */
	float mTouchProgressOffset;

	private int mScaledTouchSlop;
	/**
	 * Check whether the seek bar is being dragged
	 */
	private boolean mIsDragging;
	Context context;

	/**
	 * Creates a new RangeSeekBar.
	 * 
	 * @param absoluteMinValue
	 *            The minimum value of the selectable range.
	 * @param absoluteMaxValue
	 *            The maximum value of the selectable range.
	 * @param context
	 * @throws IllegalArgumentException
	 *             Will be thrown if min/max value type is not one of Long,
	 *             Double, Integer, Float, Short, Byte or BigDecimal.
	 */
	public RangeSeekBar(T absoluteMinValue, T absoluteMaxValue, Context context)
			throws IllegalArgumentException {
		super(context);
		this.context = context;
		this.absoluteMinValue = absoluteMinValue;
		this.absoluteMaxValue = absoluteMaxValue;
		absoluteMinValuePrim = absoluteMinValue.doubleValue();
		absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
		numberType = NumberType.fromNumber(absoluteMinValue);
		WindowManager mWinMgr = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int displayWidth = mWinMgr.getDefaultDisplay().getWidth();
		/**
		 * Subtract the  thumbimage width from the display width
		 */
		displayWidth = (int) (displayWidth - thumbWidth*2);
		/**
		 * calculate the x coordinate for the text to draw,divide the
		 * display width by total number of items to be drawn,so that  equal spacing 
		 * is maintained.
		 */
		textWidth = displayWidth / 5;

		// make RangeSeekBar focusable. This solves focus handling issues in
		// case EditText widgets are being used along with the RangeSeekBar
		// within ScollViews.
		setFocusable(true);
		setFocusableInTouchMode(true);
		init();

	}

	/**
	 * Used to calculate the touch slop value for the device
	 */
	private final void init() {
		mScaledTouchSlop = ViewConfiguration.get(getContext())
				.getScaledTouchSlop();
	}

	public boolean isNotifyWhileDragging() {
		return notifyWhileDragging;
	}

	/**
	 * Should the widget notify the listener callback while the user is still
	 * dragging a thumb? Default is false.
	 * 
	 * @param flag
	 */
	public void setNotifyWhileDragging(boolean flag) {
		this.notifyWhileDragging = flag;
	}

	/**
	 * Returns the absolute minimum value of the range that has been set at
	 * construction time.
	 * 
	 * @return The absolute minimum value of the range.
	 */
	public T getAbsoluteMinValue() {
		return absoluteMinValue;
	}

	/**
	 * Returns the absolute maximum value of the range that has been set at
	 * construction time.
	 * 
	 * @return The absolute maximum value of the range.
	 */
	public T getAbsoluteMaxValue() {
		return absoluteMaxValue;
	}

	/**
	 * Returns the currently selected min value.
	 * 
	 * @return The currently selected min value.
	 */
	public T getSelectedMinValue() {
		return normalizedToValue(normalizedMinValue);
	}

	/**
	 * Sets the currently selected minimum value. The widget will be invalidated
	 * and redrawn.
	 * 
	 * @param value
	 *            The Number value to set the minimum value to. Will be clamped
	 *            to given absolute minimum/maximum range.
	 */
	public void setSelectedMinValue(T value) {
		// in case absoluteMinValue == absoluteMaxValue, avoid division by zero
		// when normalizing.
		if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
			setNormalizedMinValue(0d);
		} else {
			setNormalizedMinValue(valueToNormalized(value));
		
		}
	}

	/**
	 * Returns the currently selected max value.
	 * 
	 * @return The currently selected max value.
	 */
	public T getSelectedMaxValue() {
		return normalizedToValue(normalizedMaxValue);
	}

	/**
	 * Sets the currently selected maximum value. The widget will be invalidated
	 * and redrawn.
	 * 
	 * @param value
	 *            The Number value to set the maximum value to. Will be clamped
	 *            to given absolute minimum/maximum range.
	 */
	public void setSelectedMaxValue(T value) {
		// in case absoluteMinValue == absoluteMaxValue, avoid division by zero
		// when normalizing.
		if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
			setNormalizedMaxValue(1d);
		} else {
			setNormalizedMaxValue(valueToNormalized(value));
		}
	}

	/**
	 * Registers given listener callback to notify about changed selected
	 * values.
	 * 
	 * @param listener
	 *            The listener to notify about changed selected values.
	 */
	public void setOnRangeSeekBarChangeListener(
			OnRangeSeekBarChangeListener<T> listener) {
		this.listener = listener;
	}
public void setRangeBarValuesListener(RangeBarValues list){
	this.rangeBarListener=list;
}
	/**
	 * Handles thumb selection and movement. Notifies listener callback on
	 * certain events.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!isEnabled())
			return false;

		int pointerIndex;

		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:
			// Remember where the motion event started
			mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
			pointerIndex = event.findPointerIndex(mActivePointerId);
			mDownMotionX = event.getX(pointerIndex);

			pressedThumb = evalPressedThumb(mDownMotionX);

			// Only handle thumb presses.
			if (pressedThumb == null)
				return super.onTouchEvent(event);

			setPressed(true);
			invalidate();
			onStartTrackingTouch();
			trackTouchEvent(event);
			attemptClaimDrag();

			break;
		case MotionEvent.ACTION_MOVE:
			if (pressedThumb != null) {

				if (mIsDragging) {
					trackTouchEvent(event);
				} else {
					// Scroll to follow the motion event
					pointerIndex = event.findPointerIndex(mActivePointerId);
					final float x = event.getX(pointerIndex);

					if (Math.abs(x - mDownMotionX) > mScaledTouchSlop) {
						setPressed(true);
						invalidate();
						onStartTrackingTouch();
						trackTouchEvent(event);
						attemptClaimDrag();
					}
				}

				if (notifyWhileDragging && listener != null) {
					listener.onRangeSeekBarValuesChanged(this,
							getSelectedMinValue(), getSelectedMaxValue());
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsDragging) {
				trackTouchEvent(event);
				onStopTrackingTouch();
				setPressed(false);
			} else {
				// Touch up when we never crossed the touch slop threshold
				// should be interpreted as a tap-seek to that location.
				onStartTrackingTouch();
				trackTouchEvent(event);
				onStopTrackingTouch();
			}

			pressedThumb = null;
			invalidate();
			if (listener != null) {
				listener.onRangeSeekBarValuesChanged(this,
						getSelectedMinValue(), getSelectedMaxValue());
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN: {
			final int index = event.getPointerCount() - 1;
			// final int index = ev.getActionIndex();
			mDownMotionX = event.getX(index);
			mActivePointerId = event.getPointerId(index);
			invalidate();
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(event);
			invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mIsDragging) {
				onStopTrackingTouch();
				setPressed(false);
			}
			invalidate(); // see above explanation
			break;
		}
		return true;
	}

	private final void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;

		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose
			// a new active pointer and adjust accordingly.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mDownMotionX = ev.getX(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
		}
	}

	/**
	 * Tracks the motion event , nothing but the touch and normalizes the value
	 * 
	 * @param event
	 *            ,The motion event that occurred
	 */
	private final void trackTouchEvent(MotionEvent event) {
		final int pointerIndex = event.findPointerIndex(mActivePointerId);
		final float x = event.getX(pointerIndex);
        // Log.e("x value is...",""+x);
		if (Thumb.MIN.equals(pressedThumb)) {
			setNormalizedMinValue(screenToNormalized(x));
		} else if (Thumb.MAX.equals(pressedThumb)) {
			setNormalizedMaxValue(screenToNormalized(x));
		}

	}

	/**
	 * Tries to claim the user's drag motion, and requests disallowing any
	 * ancestors from stealing events in the drag.
	 */
	private void attemptClaimDrag() {
		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
	}

	/**
	 * This is called when the user has started touching this widget.
	 */
	void onStartTrackingTouch() {
		mIsDragging = true;
	}

	/**
	 * This is called when the user either releases his touch or the touch is
	 * canceled.
	 */
	void onStopTrackingTouch() {
		mIsDragging = false;
	}

	/**
	 * Ensures correct size of the widget.
	 */
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		int width = 200;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}
		int height = thumbImage.getHeight();
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
			height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
		}
		setMeasuredDimension(width, height);
	}

	/**
	 * Draws the widget on the given canvas.
	 */
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// draw seek bar background line
		final RectF rect = new RectF(padding,
				0.5f * (getHeight() - lineHeight), getWidth() - padding,
				0.5f * (getHeight() + lineHeight));
		// fill the stroke
		paint.setStyle(Style.FILL);
		// Color for seek bar background line
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		canvas.drawRoundRect(rect, 5, 5, paint);

		// draw seek bar active range line
		rect.left = normalizedToScreen(normalizedMinValue);
		rect.right = normalizedToScreen(normalizedMaxValue);

		// green color
		paint.setColor(DEFAULT_COLOR);
		canvas.drawRoundRect(rect, 5, 5, paint);

		// draw minimum thumb
		drawThumb(normalizedToScreen(normalizedMinValue),
				Thumb.MIN.equals(pressedThumb), canvas);

		// draw maximum thumb
		drawThumb(normalizedToScreen(normalizedMaxValue),
				Thumb.MAX.equals(pressedThumb), canvas);
		paint.setColor(Color.BLACK);
		paint.setTextSize(16.0f);
		paint.setTypeface(Typeface.DEFAULT_BOLD);

		PointF pf = getTextCenterToDraw("6", rect, paint);
		// draw the text on the seek bar
		canvas.drawText("6",textWidth-15, pf.y, paint);
		canvas.drawText("9", (textWidth * 2)-15, pf.y, paint);
		canvas.drawText("12",(textWidth * 3)-15, pf.y, paint);
		canvas.drawText("3", (textWidth * 4)-15, pf.y, paint);
		canvas.drawText("6", (textWidth * 5)-15, pf.y, paint);
	}

	/**
	 * Calculates the x and y coordinates for the text to draw in the rectangle
	 * 
	 * @param text
	 *            Text that should be drawn
	 * @param region
	 *            Rectangle in which it should be drawn
	 * @param paint
	 *            Paint which is used for drawing the text
	 * @return,PointF Point in the coordinate system with x and y values
	 */
	private PointF getTextCenterToDraw(String text, RectF region, Paint paint) {
		Rect textBounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), textBounds);
		float x = region.centerX() - textBounds.width() * 0.4f;
		float y = region.centerY() + textBounds.height() * 0.4f;
		return new PointF(x, y);
	}

	/**
	 * Overridden to save instance state when device orientation changes. This
	 * method is called automatically if you assign an id to the RangeSeekBar
	 * widget using the {@link #setId(int)} method. Other members of this class
	 * than the normalized min and max values don't need to be saved.
	 */
	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable("SUPER", super.onSaveInstanceState());
		bundle.putDouble("MIN", normalizedMinValue);
		bundle.putDouble("MAX", normalizedMaxValue);
		return bundle;
	}

	/**
	 * Overridden to restore instance state when device orientation changes.
	 * This method is called automatically if you assign an id to the
	 * RangeSeekBar widget using the {@link #setId(int)} method.
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable parcel) {
		final Bundle bundle = (Bundle) parcel;
		super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
		normalizedMinValue = bundle.getDouble("MIN");
		normalizedMaxValue = bundle.getDouble("MAX");
	}

	/**
	 * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
	 * 
	 * @param screenCoord
	 *            The x-coordinate in screen space where to draw the image.
	 * @param pressed
	 *            Is the thumb currently in "pressed" state?
	 * @param canvas
	 *            The canvas to draw upon.
	 */
	private void drawThumb(float screenCoord, boolean pressed, Canvas canvas) {
		canvas.drawBitmap(pressed ? thumbPressedImage : thumbImage, screenCoord
				- thumbHalfWidth,
				(float) ((0.5f * getHeight()) - thumbHalfHeight), paint);
	}

	/**
	 * Decides which (if any) thumb is touched by the given x-coordinate.
	 * 
	 * @param touchX
	 *            The x-coordinate of a touch event in screen space.
	 * @return The pressed thumb or null if none has been touched.
	 */
	private Thumb evalPressedThumb(float touchX) {
		Thumb result = null;
		boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);
		boolean maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue);
		if (minThumbPressed && maxThumbPressed) {
			// if both thumbs are pressed (they lie on top of each other),
			// choose the one with more room to drag. this avoids "stalling" the
			// thumbs in a corner, not being able to drag them apart anymore.
			result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
		} else if (minThumbPressed) {
			result = Thumb.MIN;
		} else if (maxThumbPressed) {
			result = Thumb.MAX;
		}
		return result;
	}

	/**
	 * Decides if given x-coordinate in screen space needs to be interpreted as
	 * "within" the normalized thumb x-coordinate.
	 * 
	 * @param touchX
	 *            The x-coordinate in screen space to check.
	 * @param normalizedThumbValue
	 *            The normalized x-coordinate of the thumb to check.
	 * @return true if x-coordinate is in thumb range, false otherwise.
	 */
	private boolean isInThumbRange(float touchX, double normalizedThumbValue) {
		return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth;
	}

	/**
	 * Sets normalized min value to value so that 0 <= value <= normalized max
	 * value <= 1. The View will get invalidated when calling this method.
	 * 
	 * @param value
	 *            The new normalized min value to set.
	 */
	public void setNormalizedMinValue(double value) {
		normalizedMinValue = Math.max(0d,
				Math.min(1d, Math.min(value, normalizedMaxValue)));
		invalidate();
	}

	/**
	 * Sets normalized max value to value so that 0 <= normalized min value <=
	 * value <= 1. The View will get invalidated when calling this method.
	 * 
	 * @param value
	 *            The new normalized max value to set.
	 */
	public void setNormalizedMaxValue(double value) {
		normalizedMaxValue = Math.max(0d,
				Math.min(1d, Math.max(value, normalizedMinValue)));
		invalidate();
	}

	/**
	 * Converts a normalized value to a Number object in the value space between
	 * absolute minimum and maximum.
	 * 
	 * @param normalized
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private T normalizedToValue(double normalized) {
		return (T) numberType.toNumber(absoluteMinValuePrim + normalized
				* (absoluteMaxValuePrim - absoluteMinValuePrim));
	}

	/**
	 * Converts the given Number value to a normalized double.
	 * 
	 * @param value
	 *            The Number value to normalize.
	 * @return The normalized double.
	 */
	private double valueToNormalized(T value) {
		if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
			// prevent division by zero, simply return 0.
			return 0d;
		}
		return (value.doubleValue() - absoluteMinValuePrim)
				/ (absoluteMaxValuePrim - absoluteMinValuePrim);
	}

	/**
	 * Converts a normalized value into screen space.
	 * 
	 * @param normalizedCoord
	 *            The normalized value to convert.
	 * @return The converted value in screen space.
	 */
	private float normalizedToScreen(double normalizedCoord) {
		return (float) (padding + normalizedCoord * (getWidth() - 2 * padding));
	}

	/**
	 * Converts screen space x-coordinates into normalized values.
	 * 
	 * @param screenCoord
	 *            The x-coordinate in screen space to convert.
	 * @return The normalized value.
	 */
	private double screenToNormalized(float screenCoord) {
		int width = getWidth();
		if (width <= 2 * padding) {
			// prevent division by zero, simply return 0.
			return 0d;
		} else {
			//Log.e("padding..",""+padding);
			//Log.e("thumb width...",""+thumbWidth);
			Log.e("scrren coord",""+((screenCoord/textWidth)));
			float screenCor=((screenCoord/textWidth))+0.30f;
			
			if(Thumb.MIN.equals(pressedThumb)){
			if(screenCor<1.20f){
				
				if(screenCor>1.5f){
					//rangeBarListener.getMinVal(6);
					minVal=6;
					rangeBarListener.getMinVal(minVal, maxVal);
					//return 6;
				}
				else{
					//return Math.round(((screenCor+0.10)*100)/20);
					//rangeBarListener.getMinVal(Math.round(((screenCor+0.10)*100)/20));
					minVal=Math.round(((screenCor+0.10)*100)/20);
					rangeBarListener.getMinVal(minVal, maxVal);
				}
				
 			}
			else if(screenCor<2.20f){
				if(screenCor>2.5f){
					//rangeBarListener.getMinVal(9);
					minVal=9;
					rangeBarListener.getMinVal(minVal, maxVal);
					//return 9;
				}
				else{
					//Log.e("div",""+6+(Math.round((((screenCor+0.10)-1)*100)/40)));
				//	return Math.round(((screenCor+0.10)*100)/50);
					//rangeBarListener.getMinVal(6+(Math.round((((screenCor+0.10)-1)*100)/40)));
					minVal=6+(Math.round((((screenCor+0.10)-1)*100)/40));
					rangeBarListener.getMinVal(minVal, maxVal);
				
				}	
			}
			else if(screenCor<3.20f){
				if(screenCor>3.5f){
					//return 12;
					//rangeBarListener.getMinVal(12);
					minVal=12;
					rangeBarListener.getMinVal(minVal, maxVal);
				}
				else{
					//Log.e("div",""+9+(Math.round((((screenCor+0.10)-2)*100)/40)));
				//	return Math.round(((screenCor+0.10)*100)/50);
					//rangeBarListener.getMinVal(9+(Math.round((((screenCor+0.10)-2)*100)/40)));
					minVal=9+(Math.round((((screenCor+0.10)-2)*100)/40));
					rangeBarListener.getMinVal(minVal, maxVal);
				}	
				
			}
			else if(screenCor<4.20f){
				if(screenCor>4.5f){
					//return 15;
					//rangeBarListener.getMinVal(15);
					minVal=15;
					rangeBarListener.getMinVal(minVal, maxVal);
				}
				else{
					//Log.e("div",""+12+(Math.round((((screenCor+0.10)-3)*100)/45)));
				//	return Math.round(((screenCor+0.10)*100)/50);
					//rangeBarListener.getMinVal(12+(Math.round((((screenCor+0.10)-3)*100)/45)));
				minVal=12+(Math.round((((screenCor+0.10)-3)*100)/45));
				rangeBarListener.getMinVal(minVal, maxVal);
				}	
				
			}	
			else if(screenCor<5.20f){
				if(screenCor>5.5f){
					minVal=18;
					rangeBarListener.getMinVal(minVal, maxVal);
					//rangeBarListener.getMinVal(18);
					//return 18;
				}
				else{
					//Log.e("div",""+15+(Math.round((((screenCor+0.10)-4)*100)/45)));
				//	return Math.round(((screenCor+0.10)*100)/50);
					minVal=15+(Math.round((((screenCor+0.10)-4)*100)/45));
					rangeBarListener.getMinVal(minVal, maxVal);
					//rangeBarListener.getMinVal(15+(Math.round((((screenCor+0.10)-4)*100)/45)));
				}	
				
			}
			}
			else{

				if(screenCor<1.20f){
					
					if(screenCor>1.5f){
						//rangeBarListener.getMinVal(6);
						minVal=6;
						rangeBarListener.getMinVal(minVal, maxVal);
						//return 6;
					}
					else{
						//return Math.round(((screenCor+0.10)*100)/20);
						//rangeBarListener.getMinVal(Math.round(((screenCor+0.10)*100)/20));
						minVal=Math.round(((screenCor+0.10)*100)/20);
						rangeBarListener.getMinVal(minVal, maxVal);
					}
					
	 			}
				else if(screenCor<2.20f){
					if(screenCor>2.5f){
						//rangeBarListener.getMinVal(9);
						maxVal=9;
						rangeBarListener.getMinVal(minVal, maxVal);
						//return 9;
					}
					else{
						//Log.e("div",""+6+(Math.round((((screenCor+0.10)-1)*100)/40)));
					//	return Math.round(((screenCor+0.10)*100)/50);
						//rangeBarListener.getMinVal(6+(Math.round((((screenCor+0.10)-1)*100)/40)));
						maxVal=6+(Math.round((((screenCor+0.10)-1)*100)/40));
						rangeBarListener.getMinVal(minVal, maxVal);
					
					}	
				}
				else if(screenCor<3.20f){
					if(screenCor>3.5f){
						//return 12;
						//rangeBarListener.getMinVal(12);
						maxVal=12;
						rangeBarListener.getMinVal(minVal, maxVal);
					}
					else{
						//Log.e("div",""+9+(Math.round((((screenCor+0.10)-2)*100)/40)));
					//	return Math.round(((screenCor+0.10)*100)/50);
						//rangeBarListener.getMinVal(9+(Math.round((((screenCor+0.10)-2)*100)/40)));
						maxVal=9+(Math.round((((screenCor+0.10)-2)*100)/40));
						rangeBarListener.getMinVal(minVal, maxVal);
					}	
					
				}
				else if(screenCor<4.20f){
					if(screenCor>4.5f){
						//return 15;
						//rangeBarListener.getMinVal(15);
						maxVal=15;
						rangeBarListener.getMinVal(minVal, maxVal);
					}
					else{
						//Log.e("div",""+12+(Math.round((((screenCor+0.10)-3)*100)/45)));
					//	return Math.round(((screenCor+0.10)*100)/50);
						//rangeBarListener.getMinVal(12+(Math.round((((screenCor+0.10)-3)*100)/45)));
						maxVal=12+(Math.round((((screenCor+0.10)-3)*100)/45));
					rangeBarListener.getMinVal(minVal, maxVal);
					}	
					
				}	
				else if(screenCor<5.20f){
					if(screenCor>5.5f){
						maxVal=18;
						rangeBarListener.getMinVal(minVal, maxVal);
						//rangeBarListener.getMinVal(18);
						//return 18;
					}
					else{
						//Log.e("div",""+15+(Math.round((((screenCor+0.10)-4)*100)/45)));
					//	return Math.round(((screenCor+0.10)*100)/50);
						maxVal=15+(Math.round((((screenCor+0.10)-4)*100)/45));
						rangeBarListener.getMinVal(minVal, maxVal);
						//rangeBarListener.getMinVal(15+(Math.round((((screenCor+0.10)-4)*100)/45)));
					}	
					
				}
				
			}
			//Log.e("width..",""+width);
			double result = (screenCoord - padding) / (width - 2 * padding);
			//Log.e("result..",""+result);
			return Math.min(1d, Math.max(0d, result));
		}
	}
public interface RangeBarValues{
	public void getMinVal(long minVal,long maxVal);
		
	
}
	/**
	 * Callback listener interface to notify about changed range values.
	 * 
	 * 
	 * @param <T>
	 *            The Number type the RangeSeekBar has been declared with.
	 */
	public interface OnRangeSeekBarChangeListener<T> {
		public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
				T minValue, T maxValue);
	}

	/**
	 * Thumb constants (min and max).
	 */
	private static enum Thumb {
		MIN, MAX
	};

	/**
	 * Utility enumaration used to convert between Numbers and doubles.
	 * 
	 * 
	 */
	private static enum NumberType {
		LONG, DOUBLE, INTEGER, FLOAT, SHORT, BYTE, BIG_DECIMAL;

		public static <E extends Number> NumberType fromNumber(E value)
				throws IllegalArgumentException {
			if (value instanceof Long) {
				return LONG;
			}
			if (value instanceof Double) {
				return DOUBLE;
			}
			if (value instanceof Integer) {
				return INTEGER;
			}
			if (value instanceof Float) {
				return FLOAT;
			}
			if (value instanceof Short) {
				return SHORT;
			}
			if (value instanceof Byte) {
				return BYTE;
			}
			if (value instanceof BigDecimal) {
				return BIG_DECIMAL;
			}
			throw new IllegalArgumentException("Number class '"
					+ value.getClass().getName() + "' is not supported");
		}

		public Number toNumber(double value) {
			switch (this) {
			case LONG:
				return new Long((long) value);
			case DOUBLE:
				return value;
			case INTEGER:
				return new Integer((int) value);
			case FLOAT:
				return new Float(value);
			case SHORT:
				return new Short((short) value);
			case BYTE:
				return new Byte((byte) value);
			case BIG_DECIMAL:
				return new BigDecimal(value);
			}
			throw new InstantiationError("can't convert " + this
					+ " to a Number object");
		}
	}
}
