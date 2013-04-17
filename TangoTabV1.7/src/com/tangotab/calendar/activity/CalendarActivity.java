package com.tangotab.calendar.activity;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tangotab.R;
import com.tangotab.calendar.utils.CalendarView;
import com.tangotab.mainmenu.activity.MainMenuActivity;
import com.tangotab.search.activity.SearchActivity;

/**
 *  calendar activity class will be used for Select Date from calendar.
 * 
 * <br> Class :CalendarActivity <br>
 * <br> Layout :calenderlayout.xml
 * 
 * @author Satyabrata Sarangi
 * 
 */
public class CalendarActivity extends Activity implements OnClickListener
{
	/**
	 * Meta definitions
	 */
	private Button sendDate;
	private CalendarView calendarView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.calendarlayout);
		
		calendarView = (CalendarView) findViewById(R.id.calendar_view);
		
		sendDate = (Button) findViewById(R.id.btnSendDate);
		sendDate.setOnClickListener(this);
	}

	/*
	 * onClick listner for fetch the date from calendar
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v == sendDate)
		{
			Intent sendDateIntent = new Intent();
			String Day;
			String Month;
			if(calendarView.isDateChanged() || CalendarView.prevYear ==0){
			int day = calendarView.getDay();
			int month = calendarView.getMonth();
			Log.v("Selected Month from calender", String.valueOf(month));
			Log.v("Selected day from calender", String.valueOf(day));
			boolean isRightTime = false;
			int year = calendarView.getYear();
			String Year = String.valueOf(year);
			/**
			 * Add selected date into calendar.
			 */
			Calendar selectCal = Calendar.getInstance();
			selectCal.set(Calendar.YEAR, year);
			selectCal.set(Calendar.MONTH, month);
			selectCal.set(Calendar.DAY_OF_MONTH, day);
			selectCal.set(Calendar.HOUR_OF_DAY, 0);
			selectCal.set(Calendar.MINUTE, 0);
			selectCal.set(Calendar.SECOND, 0);
			selectCal.set(Calendar.MILLISECOND, 0);
			Date selectedDate = selectCal.getTime();
			Log.v("Selected date from calender ", selectCal.getTime()
					.toString());
			/*
			 * Add 0 before date and month if value less than 10
			 */

			Day = (day < 10) ? ("0" + day) : (String.valueOf(day));
			Month = (month < 10) ? ("0" + (month + 1)): (String.valueOf(month));

			/**
			 * Get the date 30 days from current date.
			 */
			Calendar cal1 = Calendar.getInstance();
			cal1.add(Calendar.DAY_OF_MONTH, 30);
			cal1.set(Calendar.HOUR_OF_DAY, 0);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);
			cal1.set(Calendar.MILLISECOND, 0);
			Log.v("Selected date afterThirtyDays ", cal1.getTime().toString());
			/**
			 * Get the current date from calendar with out time.
			 */
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Log.v("currentDate ", cal.getTime().toString());
			/**
			 * If selected date in between current date and 30 daye after the
			 * current date.
			 */
			if (selectedDate.compareTo(cal.getTime()) == 0)
				isRightTime = true;
			
			if ((selectedDate.compareTo(cal1.getTime())<=0)	&& (selectedDate.compareTo(cal.getTime()) >= 0))
			{
				isRightTime = true;
			}
			else
			{
				if(selectedDate.after(cal1.getTime()))
					showDialog(1);
				if(selectedDate.before(cal.getTime()))
					showDialog(0);	
			}			
			if (isRightTime)
			{
				sendDateIntent.putExtra("day", Day);
				sendDateIntent.putExtra("month", Month);
				sendDateIntent.putExtra("year", Year);
				setResult(RESULT_OK, sendDateIntent);
				CalendarView.prevYear=0;
				CalendarView.prevDay=0;
				CalendarView.prevMonth=0;
				finish();
			}
		}
			else{
				Day = (CalendarView.prevDay < 10) ? ("0" + CalendarView.prevDay) : (String.valueOf(CalendarView.prevDay));
				Month = (CalendarView.prevMonth < 10) ? ("0" + (CalendarView.prevMonth + 1)): (String.valueOf(CalendarView.prevMonth));
				Log.e("prev date is...",""+CalendarView.prevDay+""+CalendarView.prevMonth);
				sendDateIntent.putExtra("day",Day);
				sendDateIntent.putExtra("month",Month);
				sendDateIntent.putExtra("year", String.valueOf(CalendarView.prevYear));
				setResult(RESULT_OK, sendDateIntent);
				CalendarView.prevYear=0;
				CalendarView.prevDay=0;
				CalendarView.prevMonth=0;
				finish();	
			}	
		}
		
	}
	/**
	 * Dialog message for different cases.
	 * 
	 */
	protected Dialog onCreateDialog(int id)
		{
		switch (id)
		{
		case 0:
			AlertDialog.Builder ab = new AlertDialog.Builder(CalendarActivity.this);
			ab.setTitle("Invalid Date");
			ab.setMessage("You can't select previous date,Please select the future date within next 30days");
			ab.setPositiveButton("Dismiss",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					});
			return ab.create();
			
		case 1:
			AlertDialog.Builder ab1 = new AlertDialog.Builder(CalendarActivity.this);
			ab1.setTitle("Invalid Date");
			ab1.setMessage("Please select the future date within next 30days");
			ab1.setPositiveButton("Dismiss",new DialogInterface.OnClickListener()
			{
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			return ab1.create();
		}
		
		return null;
	}
	
	 @Override
		public boolean onKeyDown(int keycode, KeyEvent e) {
		    switch(keycode) 
		    {
		        case KeyEvent.KEYCODE_MENU:
		        	Intent mainMenuIntent = new Intent(CalendarActivity.this,MainMenuActivity.class);
					mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainMenuIntent);
		            return true;
		        case KeyEvent.KEYCODE_SEARCH:
		        	Intent searchIntent=new Intent(CalendarActivity.this, SearchActivity.class);
					searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(searchIntent);
		            return true; 
		    }

		    return super.onKeyDown(keycode, e);
		}
}
