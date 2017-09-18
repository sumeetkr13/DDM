package com.android.prasadmukne.datadrivenmechanic.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import com.android.prasadmukne.datadrivenmechanic.R;
import com.android.prasadmukne.datadrivenmechanic.utils.AppConstants;
import com.android.prasadmukne.datadrivenmechanic.utils.SharedPreferencesUtility;

/**
 * Created by prasad.mukne on 7/14/2017.
 */

public class SettingsFragment extends Fragment
{

	public static SettingsFragment newInstance(String argument)
	{
		SettingsFragment fragment = new SettingsFragment();
		Bundle args = new Bundle();
		//args.putString(PAYMENT_MODE, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.settings);

		try
		{
			TextView changeUserTypeTextView= (TextView) rootView.findViewById(R.id.changeUserTypeTextView);
			changeUserTypeTextView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					showUserTypeDialog();
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return rootView;
	}

	public void showUserTypeDialog()
	{
		final Dialog fingerPrintDialog = new Dialog(new ContextThemeWrapper(getActivity(), R.style.BasicAppBaseTheme));
		fingerPrintDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View dialogView=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_user_type,null);
		fingerPrintDialog.setContentView(dialogView);
		fingerPrintDialog.setCancelable(false);
		final RadioButton normalUserRadioButton= (RadioButton) dialogView.findViewById(R.id.normalUserRadioButton);
		final RadioButton expertUserRadioButton= (RadioButton) dialogView.findViewById(R.id.expertUserRadioButton);
		SharedPreferencesUtility sharedPreferencesUtility=SharedPreferencesUtility.getSharedPreferencesUtility(getActivity());
		if(sharedPreferencesUtility.getString(AppConstants.USER_TYPE).equals(AppConstants.NORMAL_USER))
		{
			normalUserRadioButton.setChecked(true);
		}
		else if(sharedPreferencesUtility.getString(AppConstants.USER_TYPE).equals(AppConstants.EXPERT_USER))
		{
			expertUserRadioButton.setChecked(true);
		}
		Button dialogButton = (Button) fingerPrintDialog.findViewById(R.id.doneButton);
		dialogButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				fingerPrintDialog.dismiss();
				storeAndRefreshUIWRTUserType(normalUserRadioButton.isChecked(),expertUserRadioButton.isChecked());
			}
		});
		;
		fingerPrintDialog.show();
	}

	public void storeAndRefreshUIWRTUserType(boolean isNormalUserRadioButtonChecked,boolean isExpertUserRadioButtonChecked)
	{
		SharedPreferencesUtility sharedPreferencesUtility=SharedPreferencesUtility.getSharedPreferencesUtility(getActivity());
		if(isNormalUserRadioButtonChecked)
		{
			sharedPreferencesUtility.putString(AppConstants.USER_TYPE,AppConstants.NORMAL_USER);
		}
		else if(isExpertUserRadioButtonChecked)
		{
			sharedPreferencesUtility.putString(AppConstants.USER_TYPE,AppConstants.EXPERT_USER);
		}

	}
}