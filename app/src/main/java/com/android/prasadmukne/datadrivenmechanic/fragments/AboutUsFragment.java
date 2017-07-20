package com.android.prasadmukne.datadrivenmechanic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.prasadmukne.datadrivenmechanic.R;

/**
 * Created by prasad.mukne on 7/14/2017.
 */

public class AboutUsFragment extends Fragment
{

	public static AboutUsFragment newInstance(String argument)
	{
		AboutUsFragment fragment = new AboutUsFragment();
		Bundle args = new Bundle();
		//args.putString(PAYMENT_MODE, param1);
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View rootView = inflater.inflate(R.layout.fragment_aboout_us, container, false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.about_us);
		try
		{
			//initialiseAndSetUI(rootView);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return rootView;
	}
}
