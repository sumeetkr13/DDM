package com.android.prasadmukne.datadrivenmechanic.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.prasadmukne.datadrivenmechanic.R;
import com.android.prasadmukne.datadrivenmechanic.fragments.AboutUsFragment;
import com.android.prasadmukne.datadrivenmechanic.fragments.HelpFragment;
import com.android.prasadmukne.datadrivenmechanic.fragments.HomeScreenFragment;
import com.android.prasadmukne.datadrivenmechanic.fragments.PrivacyPolicyFragment;
import com.android.prasadmukne.datadrivenmechanic.fragments.SettingsFragment;
import com.android.prasadmukne.datadrivenmechanic.login.LoginScreenActivity;
import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity
{

	private ArrayList<String> drawerItemArrayList;

	private DrawerLayout drawerLayout;

	private DrawerListAdapter drawerListAdapter;

	private ListView draweristView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);

		try
		{
			Toolbar toolbar = setupToolbar();

			initialiseUIElements(toolbar);

			prepareDrawerListData();

			setupNavigationDrawer();

			loadDefaultFragment();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void loadDefaultFragment()
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeScreenFragment(), "HomeScreenFragment").commit();
	}

	private void setupNavigationDrawer()
	{
		drawerListAdapter = new DrawerListAdapter(this, drawerItemArrayList);
		draweristView.setAdapter(drawerListAdapter);
		draweristView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				try
				{
					Fragment fragment = null;
					String TAG = "";
					String itemName = drawerItemArrayList.get(position);
					if (itemName.equals(getResources().getString(R.string.home)))
					{
						fragment = getSupportFragmentManager().findFragmentByTag("HomeScreenFragment");
						if (null == fragment)
						{
							fragment = new HomeScreenFragment();
						}
						TAG = "HomeScreenFragment";
					}
					else if (itemName.equals(getResources().getString(R.string.settings)))
					{
						fragment = getSupportFragmentManager().findFragmentByTag("SettingsFragment");
						if (null == fragment)
						{
							fragment = SettingsFragment.newInstance("");
						}
						TAG = "SettingsFragment";
					}
					else if (itemName.equals(getResources().getString(R.string.about_us)))
					{
						fragment = getSupportFragmentManager().findFragmentByTag("AboutUsFragment");
						if (null == fragment)
						{
							fragment = AboutUsFragment.newInstance("");
						}
						TAG = "AboutUsFragment";
					}
					else if (itemName.equals(getResources().getString(R.string.privacy_policy)))
					{
						fragment = getSupportFragmentManager().findFragmentByTag("PrivacyPolicyFragment");
						if (null == fragment)
						{
							fragment = PrivacyPolicyFragment.newInstance("");
						}
						TAG = "PrivacyPolicyFragment";
					}
					else if (itemName.equals(getResources().getString(R.string.help)))
					{
						fragment = getSupportFragmentManager().findFragmentByTag("HelpFragment");
						if (null == fragment)
						{
							fragment = HelpFragment.newInstance("");
						}
						TAG = "HelpFragment";
					}
					else if (itemName.equals(getResources().getString(R.string.logout)))
					{
						drawerLayout.closeDrawer(Gravity.START);
						showLogoutDialog();
					}

					if (fragment != null)
					{
						FragmentManager fragmentManager = getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
						if (!fragment.isAdded())
						{
							// fragmentTransaction.replace(R.id.content_frame, fragment,TAG).commit();
							fragmentTransaction.replace(R.id.content_frame, fragment, TAG).addToBackStack(null).commit();
						}
					}
					draweristView.setItemChecked(position, true);
					draweristView.setSelection(position);
					drawerLayout.closeDrawer(Gravity.START);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	private void initialiseUIElements(Toolbar toolbar)
	{
		//TextView primaryActionBarTextView = (TextView) findViewById(R.id.primaryActionBarTextView);
		//primaryActionBarTextView.setVisibility(View.VISIBLE);
		//primaryActionBarTextView.setText(getResources().getString(R.string.app_name));
		draweristView = (ListView) findViewById(R.id.drawerListView);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawerLayout.setDrawerListener(toggle);

		toggle.syncState();
	}

	private Toolbar setupToolbar()
	{
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(R.string.app_name);
		return toolbar;
	}

	private void prepareDrawerListData()
	{
		drawerItemArrayList = new ArrayList<String>();
		drawerItemArrayList.add(getResources().getString(R.string.home));
		drawerItemArrayList.add(getResources().getString(R.string.settings));
		drawerItemArrayList.add(getResources().getString(R.string.about_us));
		drawerItemArrayList.add(getResources().getString(R.string.privacy_policy));
		drawerItemArrayList.add(getResources().getString(R.string.help));
		drawerItemArrayList.add(getResources().getString(R.string.logout));
	}

	@Override
	public void onBackPressed()
	{
		try
		{
			if (getSupportFragmentManager().getBackStackEntryCount() == 0)
			{
				showLogoutDialog();

			}
			else
			{
				getSupportFragmentManager().popBackStack();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		// super.onBackPressed();
	}

	private void showLogoutDialog()
	{
		new AlertDialog.Builder(this).setTitle("Logout").setMessage("Do you really want to logout this app?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(BaseActivity.this, LoginScreenActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				// System.exit(0);
			}
		}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		}).setCancelable(false).setIcon(R.drawable.ic_logout_icon).show();
	}

}
