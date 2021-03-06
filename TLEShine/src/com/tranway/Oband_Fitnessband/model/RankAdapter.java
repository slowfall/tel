package com.tranway.Oband_Fitnessband.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.Tools;

public class RankAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<FriendInfo> mFriendList = new ArrayList<FriendInfo>();
	private Context mContext;
	private static final int[] mRankIconIDs = new int[] { R.drawable.rank_gold,
			R.drawable.rank_silver, R.drawable.rank_copper };

	public RankAdapter(Context context, List<FriendInfo> list) {
		this.mInflater = LayoutInflater.from(context);
		mFriendList = list;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mFriendList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		ImageView mIconView;
		TextView mNameTxt;
		ImageView mSexIcon;
		TextView mPointTxt;
		ImageView mRankIcon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list_rank, null);
			holder = new ViewHolder();
			holder.mIconView = (ImageView) convertView.findViewById(R.id.icon_user);
			holder.mSexIcon = (ImageView) convertView.findViewById(R.id.iv_sex_icon);
			holder.mNameTxt = (TextView) convertView.findViewById(R.id.txt_name);
			holder.mPointTxt = (TextView) convertView.findViewById(R.id.txt_point);
			holder.mRankIcon = (ImageView) convertView.findViewById(R.id.iv_rank_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FriendInfo info = mFriendList.get(position);
		if (info.getAvatar() != null) {
			holder.mIconView.setImageBitmap(Tools.base64ToBitmap(info.getAvatar()));
		} else {
			holder.mIconView.setImageResource(R.drawable.user);
		}
		// holder.mIconView.setImageDrawable();
		holder.mNameTxt.setText(info.getName());
		if (info.getSex() == UserInfo.SEX_MALE) {
			holder.mSexIcon.setImageResource(R.drawable.icon_man);
		} else {
			holder.mSexIcon.setImageResource(R.drawable.icon_woman);
		}
		holder.mPointTxt.setText(info.getPoint() + mContext.getString(R.string.activity_step));
		if (position < mRankIconIDs.length) {
			holder.mRankIcon.setImageResource(mRankIconIDs[position]);
		} else {
			holder.mRankIcon.setImageBitmap(null);
		}

		return convertView;
	}
}
