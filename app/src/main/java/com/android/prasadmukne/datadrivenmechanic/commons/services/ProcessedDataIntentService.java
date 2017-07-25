package com.android.prasadmukne.datadrivenmechanic.commons.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by prasad.mukne on 7/24/2017.
 */

public class ProcessedDataIntentService extends IntentService
{

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public ProcessedDataIntentService(String name)
	{
		super(name);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent)
	{

	}
}
