package com.android.prasadmukne.datadrivenmechanic;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import com.android.prasadmukne.datadrivenmechanic.commons.database.SQLiteDatabaseManager;
import com.android.prasadmukne.datadrivenmechanic.commons.services.FeatureDataIntentService;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by prasad.mukne on 7/24/2017.
 */

public class AppApplication extends Application
{

	private static AppApplication mInstance;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	public static final String TAG = AppApplication.class.getSimpleName();

	public static synchronized AppApplication getInstance()
	{
		return mInstance;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		mInstance = this;
		String[] requestTableColumnNames = {SQLiteDatabaseManager.REQUEST, SQLiteDatabaseManager.USERNAME, SQLiteDatabaseManager.FILE_PATH, SQLiteDatabaseManager.FILE_NAME, SQLiteDatabaseManager.STATUS, SQLiteDatabaseManager.IS_EXPERT_USER, SQLiteDatabaseManager.IS_SYSTEM_FAULTY, SQLiteDatabaseManager.TIMESTAMP};
		String[] requestTableColumnDataTypes = {"Text", "Text", "Text", "Text", "Text", "Text", "Text", "Text"};
		SQLiteDatabaseManager.getInstance(this).createTable(SQLiteDatabaseManager.REQUEST_TABLE, requestTableColumnNames, requestTableColumnDataTypes);

		String[] rawTableColumnNames = {SQLiteDatabaseManager.USERNAME, SQLiteDatabaseManager.TIMESTAMP, SQLiteDatabaseManager.FILE_PATH, SQLiteDatabaseManager.FILE_NAME, SQLiteDatabaseManager.STATUS};
		String[] rawTablColumnDataTypes = {"Text", "Text", "Text", "Text", "Text"};
		SQLiteDatabaseManager.getInstance(this).createTable(SQLiteDatabaseManager.RAW_TABLE, rawTableColumnNames, rawTablColumnDataTypes);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		mInstance.registerReceiver(receiver, intentFilter);
	}

	public RequestQueue getRequestQueue()
	{
		if (mRequestQueue == null)
		{
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}


	public <T> void addToRequestQueue(Request<T> req, String tag)
	{
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		//RequestFuture<JSONObject> future = RequestFuture.newFuture();
		RetryPolicy policy = new DefaultRetryPolicy(300000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		req.setRetryPolicy(policy);
		getRequestQueue().add(req);
		/*try {
			//JSONObject response = future.get(); // this will block
		} catch (InterruptedException e) {
			// exception handling
		} catch (ExecutionException e) {
			// exception handling
		}*/
	}

	public BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent arg1)
		{
			Intent intent1 = new Intent(context, FeatureDataIntentService.class);
			context.startService(intent1);
		}
	};

	@Override
	public void onTerminate()
	{
		mInstance.unregisterReceiver(receiver);
		super.onTerminate();
	}
}
