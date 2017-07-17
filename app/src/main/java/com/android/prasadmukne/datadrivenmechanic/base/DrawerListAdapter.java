package com.android.prasadmukne.datadrivenmechanic.base;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.prasadmukne.datadrivenmechanic.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by prasad.mukne on 7/14/2017.
 */

public class DrawerListAdapter extends BaseAdapter
{
		private Context context;
		private ArrayList<String> drawerItemList;

		public DrawerListAdapter(Context context, ArrayList<String> drawerItemList)
		{
			this.context = context;
			this.drawerItemList = drawerItemList;
		}

	@Override
	public int getCount()
	{
		return drawerItemList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return drawerItemList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			TextView itemNameTextView = null;
			if (convertView == null)
			{
				LayoutInflater inflater = ((Activity) context).getLayoutInflater();
				convertView = inflater.inflate(R.layout.adapter_drawer_list_item, parent, false);
				itemNameTextView= (TextView) convertView.findViewById(R.id.itemNameTextView);
			}
			itemNameTextView.setText(drawerItemList.get(position));

			return convertView;
		}

}
