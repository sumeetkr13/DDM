package com.android.prasadmukne.datadrivenmechanic.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by prasad.mukne on 7/20/2017.
 */

public class Utility
{

	public boolean isNetworkAvailable(Context context)
	{
		ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
		return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
	}

	public boolean isInternetAvailable()
	{
		try
		{
			InetAddress address = InetAddress.getByName("www.google.com");
			return !address.equals("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
