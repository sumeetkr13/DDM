package com.android.prasadmukne.datadrivenmechanic.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by prasad.mukne on 6/22/2017.
 */

public class SharedPreferencesUtility
{
	private static SharedPreferencesUtility sharedPreferencesUtility;
	private SharedPreferences sharedPreferences;
	private static final String SHARED_PREFERENCE_NAME="DataDrivenApp";

	private SharedPreferencesUtility(Context context)
	{
		sharedPreferences=context.getSharedPreferences(SHARED_PREFERENCE_NAME,Context.MODE_PRIVATE);
	}

	public static SharedPreferencesUtility getSharedPreferencesUtility(Context context)
	{
		if(null==sharedPreferencesUtility)
		{
			sharedPreferencesUtility=new SharedPreferencesUtility(context);
		}
		return sharedPreferencesUtility;
	}

	public void putString(String key,String value)
	{
		sharedPreferences.edit().putString(key,value).commit();
	}

	public String getString(String key)
	{
		return sharedPreferences.getString(key,"");
	}

	public void putBoolean(String key,boolean value)
	{
		sharedPreferences.edit().putBoolean(key,value).commit();
	}

	public boolean getBoolean(String key)
	{
		return sharedPreferences.getBoolean(key,false);
	}

	public void putInt(String key,int value)
	{
		sharedPreferences.edit().putInt(key,value).commit();
	}

	public int getInt(String key)
	{
		return sharedPreferences.getInt(key,0);
	}



}
