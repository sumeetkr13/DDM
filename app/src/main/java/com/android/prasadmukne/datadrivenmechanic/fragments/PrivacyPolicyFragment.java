package com.android.prasadmukne.datadrivenmechanic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.prasadmukne.datadrivenmechanic.R;

/**
 * Created by prasad.mukne on 7/14/2017.
 */

public class PrivacyPolicyFragment extends Fragment
{

	public static PrivacyPolicyFragment newInstance(String argument)
	{
		PrivacyPolicyFragment fragment = new PrivacyPolicyFragment();
		Bundle args = new Bundle();
		//args.putString(PAYMENT_MODE, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View rootView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.privacy_policy);
		try
		{
			//initialiseAndSetUI(rootView);
			TextView privacyPolicyTextView1= (TextView) rootView.findViewById(R.id.privacyPolicyTextView1);
			privacyPolicyTextView1.setText(Html.fromHtml(getActivity().getResources().getString(R.string.privacy_policy_statement)));
			Button okButton= (Button) rootView.findViewById(R.id.okButton);
			okButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					getFragmentManager().popBackStack();
				}
			});
			//privacyPolicyTextView1.setText(getActivity().getResources().getString(R.string.privacy_policy_statement));
			/*TextView privacyPolicyTextView2= (TextView) rootView.findViewById(R.id.privacyPolicyTextView2);
			privacyPolicyTextView2.setText(Html.fromHtml(getActivity().getResources().getString(R.string.privacy_policy_statement2)));*/
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return rootView;
	}
}
