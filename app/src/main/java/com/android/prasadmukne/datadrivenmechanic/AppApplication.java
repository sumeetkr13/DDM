package com.android.prasadmukne.datadrivenmechanic;

import android.app.Application;
import android.text.TextUtils;
import com.android.prasadmukne.datadrivenmechanic.commons.database.SQLiteDatabaseManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by prasad.mukne on 7/24/2017.
 */

public class AppApplication extends Application
{
	private static AppApplication mInstance;
	private RequestQueue          mRequestQueue;
	private ImageLoader mImageLoader;
	public static final String    TAG = AppApplication.class.getSimpleName();

	public static synchronized AppApplication getInstance()
	{
		return mInstance;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		String [] columnNames={SQLiteDatabaseManager.REQUEST,SQLiteDatabaseManager.FILE_PATH,SQLiteDatabaseManager.STATUS};
		String[] columnDataTypes={"Text","Text","Text"};
		SQLiteDatabaseManager.getInstance(this).createTable(SQLiteDatabaseManager.REQUEST_TABLE,columnNames,columnDataTypes);
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
		getRequestQueue().add(req);
	}

}
