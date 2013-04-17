package com.tangotab.core.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * This class will be used for create local data base and store information into it.
 * 
 * @author dillip.lenka
 *
 */
public class CreateSqliteHelper extends SQLiteOpenHelper
{

	private static final String DATABASE_NAME = "TangoTab.sqlite";
	private static final int SCHEMA_VERSION = 1;
	

	public CreateSqliteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);		
	}
	/**
	 * Delete the Data base
	 * @param db
	 */
	public void deleteDatabase(SQLiteDatabase db)
	{
		db.execSQL("delete from LOGIN");
	}
	/**
	 * Create all the table informations.
	 * 
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		try {

			db.execSQL("CREATE TABLE IF NOT EXISTS LOGIN(ID INTEGER,OPEN TEXT);");
			db.execSQL("CREATE TABLE IF NOT EXISTS MyDeals(ID INTEGER,reserve_time TEXT,image_url TEXT,business_Name TEXT,con_restId TEXT,deal_name TEXT,sttime TEXT,edtime TEXT);");
			db.execSQL("CREATE TABLE IF NOT EXISTS CURRENTDATE(currentDate TEXT)");
		} catch (SQLiteException e)
		{
			Log.e("Exception", e.getLocalizedMessage());
		}

	}
	/**
	 * Drop the table if already exists.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		android.util.Log.w("LunchList","Upgrading database, which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS LOGIN");
		db.execSQL("DROP TABLE IF EXISTS MyDeals");
		db.execSQL("DROP TABLE IF EXISTS CURRENTDATE");
		
		onCreate(db);
		onCreate(db);
		onCreate(db);
	}
	
}
