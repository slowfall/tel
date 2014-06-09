package com.tranway.tleshine.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tranway.tleshine.R;

public class FriendsAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<FriendInfo> mFriendList = new ArrayList<FriendInfo>();
	private boolean isAddFriend = false;
	private OnListItemClickListener mListener;

	public FriendsAdapter(Context context, List<FriendInfo> list) {
		this.mInflater = LayoutInflater.from(context);
		this.mFriendList = list;
		this.isAddFriend = false;
	}

	public FriendsAdapter(Context context, List<FriendInfo> list, boolean isAdd) {
		this.mInflater = LayoutInflater.from(context);
		this.mFriendList = list;
		this.isAddFriend = isAdd;
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

	/**
	 * set on list item button (add friend button) click listener
	 * 
	 * @param mListener
	 */
	public void setOnListItemClickListener(OnListItemClickListener mListener) {
		this.mListener = mListener;
	}

	class ViewHolder {
		ImageView mIconView;
		TextView mNameTxt;
		TextView mEmailTxt;
		Button mAddBtn;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int index = position;
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list_friend, null);
			holder = new ViewHolder();
			holder.mIconView = (ImageView) convertView.findViewById(R.id.icon_user);
			holder.mNameTxt = (TextView) convertView.findViewById(R.id.txt_name);
			holder.mEmailTxt = (TextView) convertView.findViewById(R.id.txt_email);
			holder.mAddBtn = (Button) convertView.findViewById(R.id.btn_add_friend);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FriendInfo info = mFriendList.get(index);
		// holder.mIconView.setImageDrawable();
		holder.mNameTxt.setText(info.getName());
		holder.mEmailTxt.setText(info.getEmail());
		if (isAddFriend) {
			holder.mAddBtn.setVisibility(View.VISIBLE);
			if (mListener != null) {
				holder.mAddBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mListener.onItemButtonClick(index);
					}
				});
			}
		} else {
			holder.mAddBtn.setVisibility(View.GONE);
		}

		return convertView;
	}

	public interface OnListItemClickListener {
		void onItemButtonClick(int index);
	}
}
