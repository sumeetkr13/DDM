package com.android.prasadmukne.datadrivenmechanic.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.android.prasadmukne.datadrivenmechanic.R;
import com.android.prasadmukne.datadrivenmechanic.login.LoginScreenActivity;

public class SplashScreenActivity extends AppCompatActivity
{

	private static int SPLASH_TIME_OUT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		try
		{
			
			new Handler().postDelayed(new Runnable()
			{

				/*
				 * Showing splash screen with a timer. This will be useful when you
				 * want to show case your app logo / company
				 */

				@Override
				public void run()
				{
					// This method will be executed once the timer is over
					// Start your app main activity

					Intent i = new Intent(SplashScreenActivity.this, LoginScreenActivity.class);
					startActivity(i);

					// close this activity
					finish();
				}
			}, SPLASH_TIME_OUT);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
