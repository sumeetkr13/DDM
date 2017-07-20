package com.android.prasadmukne.datadrivenmechanic.login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.prasadmukne.datadrivenmechanic.R;
import com.android.prasadmukne.datadrivenmechanic.base.BaseActivity;
import com.android.prasadmukne.datadrivenmechanic.utils.AVLoadingIndicatorView;
import com.android.prasadmukne.datadrivenmechanic.utils.AppConstants;
import java.util.ArrayList;

public class LoginScreenActivity extends AppCompatActivity implements View.OnClickListener
{

	private boolean isPasswordVisible;
	private EditText passwordEditText;
	private ImageView showPasswordImageView;
	private AVLoadingIndicatorView progressBar;
	private EditText usernameEditText;
	private Button signInButton;
	private static final int PERMISSION_REQUEST_CODE = 200;
	private static final int REQUEST_PERMISSION_SETTING = 300;
	private ArrayList<String> usernamesArrayList=new ArrayList<String>();
	private ArrayList<String> passwordArrayList=new ArrayList<String>();


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);

		addUserCredentials();

		try
		{
			setupToolbar();

			initialiseUIElements();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			{
				checkAndAskPermissions();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void addUserCredentials()
	{
		usernamesArrayList.add("sumeet");
		usernamesArrayList.add("prasad");
		usernamesArrayList.add("josh");
		usernamesArrayList.add("rahul");
		usernamesArrayList.add("sumeet1");
		usernamesArrayList.add("prasad1");
		usernamesArrayList.add("josh1");
		usernamesArrayList.add("rahul1");
		usernamesArrayList.add("p");

		passwordArrayList.add("pass12");
		passwordArrayList.add("pass@123");
		passwordArrayList.add("p");
	}

	private void initialiseUIElements()
	{
		//TextView primaryActionBarTextView = (TextView) findViewById(R.id.primaryActionBarTextView);
		//primaryActionBarTextView.setVisibility(View.VISIBLE);
		//primaryActionBarTextView.setText(getResources().getString(R.string.app_name));

		//TextView versionNumberTextView = (TextView) findViewById(R.id.versionNumberTextView);
		// versionNumberTextView.setText(BuildConfig.VERSION_CODE);

		usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		//CheckBox rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);
		showPasswordImageView = (ImageView) findViewById(R.id.showPasswordImageView);
		progressBar = (AVLoadingIndicatorView) findViewById(R.id.loginProgressBar);
		signInButton = (Button) findViewById(R.id.signInButton);
		TextView forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);

		signInButton.setOnClickListener(this);

		forgotPasswordTextView.setOnClickListener(this);
		showPasswordImageView.setOnClickListener(this);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	private void setupToolbar()
	{
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(R.string.app_name);
	}

	@Override
	public void onClick(View v)
	{
		try
		{
			switch (v.getId())
			{
				case R.id.signInButton:

					if (usernameEditText.getText().toString().trim().equals(""))
					{
						usernameEditText.setError(getResources().getString(R.string.username_empty));
					}
					else if (passwordEditText.getText().toString().trim().equals(""))
					{
						passwordEditText.setError(getResources().getString(R.string.password_empty));
					}
					else
					{
						AppConstants.TEMP_USERNAME = usernameEditText.getText().toString();
						AppConstants.TEMP_PASSWORD = passwordEditText.getText().toString();
						validateUser(AppConstants.TEMP_USERNAME, AppConstants.TEMP_PASSWORD);
					}

					break;
				case R.id.forgotPasswordTextView:
					break;
				case R.id.showPasswordImageView:
					if (!isPasswordVisible)
					{
						isPasswordVisible = true;
						passwordEditText.setTransformationMethod(null);
						showPasswordImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_visibility_off));
					}
					else
					{
						isPasswordVisible = false;
						passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
						showPasswordImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_visibility_on));
					}
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void validateUser(final String username,final String password)
	{
		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				progressBar.setVisibility(View.VISIBLE);
				signInButton.setClickable(false);
				hideKeyBoard();

			}

			@Override
			protected Void doInBackground(Void... params)
			{
				try
				{
					Thread.sleep(3000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid)
			{
				super.onPostExecute(aVoid);

				try
				{
					progressBar.setVisibility(View.INVISIBLE);
					signInButton.setClickable(true);
					if(usernamesArrayList.contains(username) && passwordArrayList.contains(password))
					{
						startActivity(new Intent(LoginScreenActivity.this, BaseActivity.class));
						finish();
					}
					else
					{
						Toast.makeText(LoginScreenActivity.this,"Please Enter Correct Credentials",Toast.LENGTH_SHORT).show();
					}

					//startActivity(new Intent(LoginScreenActivity.this, BaseActivity.class));
					//finish();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}.execute();
	}

	public void hideKeyBoard()
	{
		View view = getCurrentFocus();
		if (null != view)
		{
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public void checkAndAskPermissions()
	{
		String[] totalPermissions = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.RECORD_AUDIO};

		ArrayList<String> requiredPermission = new ArrayList<>();
		ArrayList<String> requiredRationalePermission = new ArrayList<>();
		if (checkPermission(totalPermissions,requiredPermission).size()>0)
		{

			for (int i = 0; i < totalPermissions.length; i++)
			{
				if (ActivityCompat.shouldShowRequestPermissionRationale(LoginScreenActivity.this, totalPermissions[i]))
				{
					requiredRationalePermission.add(totalPermissions[i]);
				}
			}
			String[] requiredPermissionArray = requiredPermission.toArray(new String[requiredPermission.size()]);
			if (requiredPermissionArray.length != 0 && requiredRationalePermission.size()!=0)
			{
				ActivityCompat.requestPermissions(LoginScreenActivity.this, requiredPermissionArray, PERMISSION_REQUEST_CODE);
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreenActivity.this);
				builder.setTitle("Need Permissions");
				builder.setMessage("This app needs all required permissions.Please provide all required permissions manually from settings.");
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Intent intent = new Intent();
						intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						intent.setData(Uri.parse("package:" + getPackageName()));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
						startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
						dialog.cancel();
						finish();
					}
				});
				builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
						finish();
					}
				});
				builder.show();
			}

		}
	}

	public ArrayList<String> checkPermission(String[] totalPermissionsArrayList,ArrayList<String> requiredPermissionsArrayList)
	{
		for (int i = 0; i < totalPermissionsArrayList.length; i++)
		{
			if (ContextCompat.checkSelfPermission(LoginScreenActivity.this, totalPermissionsArrayList[i]) != PackageManager.PERMISSION_GRANTED)
			{
				requiredPermissionsArrayList.add(totalPermissionsArrayList[i]);
				//return false;
			}
		}
		return requiredPermissionsArrayList;
		//return true;
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE)
		{

			boolean allGranted = false;
			for (int i = 0; i < grantResults.length; i++)
			{
				if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
				{
					allGranted = true;
				}
				else
				{
					allGranted = false;
					break;
				}
			}

			if (!allGranted)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreenActivity.this);
				builder.setTitle("Need Permissions");
				builder.setMessage("This app needs all permissions to be provided.Please provide all required permissions.");
				builder.setPositiveButton("Grant", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
						checkAndAskPermissions();
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
						finish();
					}
				});
				builder.show();
			}
		}
	}

}
