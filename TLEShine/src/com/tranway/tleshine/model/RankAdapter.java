package com.tranway.tleshine.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tranway.tleshine.R;

public class RankAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<FriendInfo> mFriendList = new ArrayList<FriendInfo>();

	public RankAdapter(Context context, List<FriendInfo> list) {
		this.mInflater = LayoutInflater.from(context);
		mFriendList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFriendList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class ViewHolder {
		ImageView mIconView;
		TextView mNameTxt;
		TextView mPointTxt;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list_rank, null);
			holder = new ViewHolder();
			holder.mIconView = (ImageView) convertView.findViewById(R.id.icon_user);
			holder.mNameTxt = (TextView) convertView.findViewById(R.id.txt_name);
			holder.mPointTxt = (TextView) convertView.findViewById(R.id.txt_point);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FriendInfo info = mFriendList.get(position);
		// holder.mIconView.setImageDrawable();
		holder.mNameTxt.setText(info.getName());
		holder.mPointTxt.setText(info.getPoint());

		return convertView;
	}
}
