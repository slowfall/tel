package com.tranway.Oband_Fitnessband.model;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tranway.Oband_Fitnessband.R;

public class RequestAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<FriendInfo> mFriendList = new ArrayList<FriendInfo>();
	private boolean isBtnVisible = false;
	private OnRequestListItemClickListener mListener;

	public RequestAdapter(Context context, List<FriendInfo> list) {
		this.mInflater = LayoutInflater.from(context);
		this.mFriendList = list;
		this.isBtnVisible = false;
	}

	public RequestAdapter(Context context, List<FriendInfo> list, boolean isVisiable) {
		this.mInflater = LayoutInflater.from(context);
		this.mFriendList = list;
		this.isBtnVisible = isVisiable;
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
	public void setOnListItemClickListener(OnRequestListItemClickListener mListener) {
		this.mListener = mListener;
	}

	class ViewHolder {
		ImageView mIconView;
		TextView mNameTxt;
		TextView mEmailTxt;
		Button mAcceptBtn;
		Button mRefuseBtn;
		LinearLayout mControlLayout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int index = position;
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list_request, null);
			holder = new ViewHolder();
			holder.mIconView = (ImageView) convertView.findViewById(R.id.icon_user);
			holder.mNameTxt = (TextView) convertView.findViewById(R.id.txt_name);
			holder.mEmailTxt = (TextView) convertView.findViewById(R.id.txt_email);
			holder.mAcceptBtn = (Button) convertView.findViewById(R.id.btn_accept);
			holder.mRefuseBtn = (Button) convertView.findViewById(R.id.btn_refuse);
			holder.mControlLayout = (LinearLayout) convertView.findViewById(R.id.layout_control);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FriendInfo info = mFriendList.get(index);
		// holder.mIconView.setImageDrawable();
		holder.mNameTxt.setText(info.getName());
		holder.mEmailTxt.setText(info.getEmail());
		if (isBtnVisible) {
			holder.mControlLayout.setVisibility(View.VISIBLE);
			if (mListener != null) {
				holder.mAcceptBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mListener.onItemButtonClick(arg0, index);
					}
				});
				holder.mRefuseBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mListener.onItemButtonClick(arg0, index);
					}
				});
			}
		} else {
			holder.mControlLayout.setVisibility(View.GONE);
		}

		return convertView;
	}

	public interface OnRequestListItemClickListener {
		void onItemButtonClick(View view, int index);
	}
}
