package com.android.prasadmukne.datadrivenmechanic.privacy_policy;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.prasadmukne.datadrivenmechanic.R;
import com.android.prasadmukne.datadrivenmechanic.fragments.PrivacyPolicyFragment;
import com.android.prasadmukne.datadrivenmechanic.login.LoginScreenActivity;
import com.android.prasadmukne.datadrivenmechanic.utils.AppConstants;
import com.android.prasadmukne.datadrivenmechanic.utils.SharedPreferencesUtility;

public class PrivacyPolicyActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_policy);

		try {
			AlertDialog.Builder builder=new AlertDialog.Builder(this);

			LayoutInflater inflater=this.getLayoutInflater();
			View view=inflater.inflate(R.layout.dialog_privacy_policy,null);
			builder.setView(view);

			final AlertDialog alertDialog=builder.create();
			TextView privacyPolicyTextView1= (TextView) view.findViewById(R.id.privacyPolicyTextView1);
			privacyPolicyTextView1.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_statement)));

			Button acceptButton = (Button) view.findViewById(R.id.acceptButton);
			acceptButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					alertDialog.dismiss();
					SharedPreferencesUtility sharedPreferencesUtility=SharedPreferencesUtility.getSharedPreferencesUtility(PrivacyPolicyActivity.this);
					sharedPreferencesUtility.putBoolean(AppConstants.IS_LICENSE_AGREED, true);
					Intent intent = new Intent(PrivacyPolicyActivity.this, LoginScreenActivity.class);
					startActivity(intent);
					finish();
				}
			});
			Button rejectButton = (Button) view.findViewById(R.id.rejectButton);
			rejectButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					alertDialog.dismiss();
					finish();
				}
			});

			builder.setOnKeyListener(new DialogInterface.OnKeyListener()
			{
				@Override
				public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
					return true;
				}
			});

			builder.setCancelable(false);
			alertDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
