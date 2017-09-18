package com.android.prasadmukne.datadrivenmechanic.commons.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.prasadmukne.datadrivenmechanic.commons.services.FeatureDataIntentService;

/**
 * Created by prasad.mukne on 7/24/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		/*NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if (info != null && info.isConnected())
		{
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			String ssid = wifiInfo.getSSID();
			Intent intent1=new Intent(context, FeatureDataIntentService.class);
			intent1.putExtra("","")
			context.startService(intent1);
		}*/
		/*ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		//NetworkInfo[] infoAvailableNetworks = cm.getAllNetworkInfo();
		boolean isMobile = false, isWifi = false;

				if (null != netInfo)
				{
					if (netInfo.getType() == ConnectivityManager.TYPE_WIFI)
					{
						if (netInfo.isConnected() && netInfo.isAvailable())
						{
							isWifi = true;
						}
					}
					if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE)
					{
						if (netInfo.isConnected() && netInfo.isAvailable())
						{
							isMobile = true;
						}
					}


			Intent intent1 = new Intent(context, FeatureDataIntentService.class);
			if (isWifi)
			{
				intent1.putExtra(AppConstants.NETWORK_TYPE, AppConstants.WIFI_NETWORK);
			}
			else if (isMobile)
			{
				intent1.putExtra(AppConstants.NETWORK_TYPE, AppConstants.MOBILE_NETWORK);
			}
			context.startService(intent1);*/
		Intent intent1 = new Intent(context, FeatureDataIntentService.class);
		context.startService(intent1);

	}
}
