package com.android.prasadmukne.datadrivenmechanic.commons.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.prasadmukne.datadrivenmechanic.commons.database.SQLiteDatabaseManager;
import com.android.prasadmukne.datadrivenmechanic.commons.model.RequestBO;
import com.android.prasadmukne.datadrivenmechanic.utils.AppConstants;
import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prasad.mukne on 7/24/2017.
 */

public class ProcessedDataIntentService extends IntentService
{



	public ProcessedDataIntentService()
	{
		super("ProcessedDataIntentService");
	}

	public ProcessedDataIntentService(String name)
	{
		super(name);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, startId, startId);
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent)
	{
		try
		{
			Log.i("onHandleIntent", "onHandleIntent");
			final ArrayList<RequestBO> requestArrayList = new ArrayList<RequestBO>();
			SQLiteDatabaseManager sqLiteDatabaseManager = SQLiteDatabaseManager.getInstance(getApplicationContext());
			Cursor cursor = sqLiteDatabaseManager.search("Select * from " + SQLiteDatabaseManager.REQUEST_TABLE + ";");
			while (cursor.moveToNext())
			{
				RequestBO requestBO = new RequestBO();
				requestBO.set_id(cursor.getInt(cursor.getColumnIndex(SQLiteDatabaseManager.ROW_ID)));
				requestBO.setRequestString(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.REQUEST)));
				requestBO.setFilePath(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.FILE_PATH)));
				requestBO.setStatus(cursor.getString(cursor.getColumnIndex(SQLiteDatabaseManager.STATUS)));
				requestArrayList.add(requestBO);
			}

			if (VolleyConnectionRequest.isNetworkAvailable(getApplicationContext()))
			{
				for (int i = 0; i < requestArrayList.size(); i++)
				{
					final VolleyConnectionRequest volleyConnectionRequest1 = new VolleyConnectionRequest(getApplicationContext(), AppConstants.PROCESSED_DATA_SENDING_URL, Method.POST, requestArrayList.get(i).get_id(), new JSONObject(requestArrayList.get(i).getRequestString()), false, new HashMap<String, String>(), new HashMap<String, String>(), Priority.HIGH, new ResponseHandler()
					{
						@Override
						public void onPreExecute()
						{

						}

						@Override
						public void onSuccessfulResponse(JSONObject response, int rowid)
						{
							SQLiteDatabaseManager.getInstance(getApplicationContext()).deleteOnRowId(SQLiteDatabaseManager.REQUEST_TABLE, rowid);
						}

						@Override
						public void onFailureResponse(Exception e)
						{

						}
					});
					volleyConnectionRequest1.sendRequest();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


}
