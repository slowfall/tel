package com.tranway.tleshine.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tranway.telshine.database.DBInfo;
import com.tranway.tleshine.R;

public class ActivityListAdapter extends BaseAdapter {
	private static final String TAG = "ActivityAdapter";

	private LayoutInflater mInflater;
//	private String[] testStr = { "one", "two", "three", "four" };

	/** current ViewPager activity every 15Min packets */
	private List<Map<String, Object>> mEvery15MinPackets = new ArrayList<Map<String, Object>>();

	public ActivityListAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
	}

	public void setActivityData(List<Map<String, Object>> mEvery15MinPackets) {
		this.mEvery15MinPackets = mEvery15MinPackets;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		 return mEvery15MinPackets.size();
//		return testStr.length;
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
	 * Every 15Mins data view holder
	 * 
	 * @author SHZ
	 */
	class ViewContentHolder {
		ImageView exerciseIcon;
		TextView exerciseIntensit;
		TextView exerciseTime;
		TextView exercisePoint;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "---loading bottom item---" + position);

		ViewContentHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list_exercise, null);
			holder = new ViewContentHolder();
			holder.exerciseIcon = (ImageView) convertView.findViewById(R.id.icon_exercise);
			holder.exerciseIntensit = (TextView) convertView.findViewById(R.id.txt_intensity);
			holder.exerciseTime = (TextView) convertView.findViewById(R.id.txt_time);
			holder.exercisePoint = (TextView) convertView.findViewById(R.id.txt_point);
			convertView.setTag(holder);
		} else {
			holder = (ViewContentHolder) convertView.getTag();
		}

		final Map<String, Object> mPacket = mEvery15MinPackets.get(position);
		// holder.exerciseIcon.setImageDrawable();
		int steps = (Integer) mPacket.get(DBInfo.KEY_STEPS);
		int intensitId = R.string.light_sport;
		int iconId = R.drawable.walk;
		if (steps > 1000) {
			intensitId = R.string.overdose_sport;
			iconId = R.drawable.run;
		} else if (steps > 500) {
			intensitId = R.string.right_sport;
			iconId = R.drawable.little_run;
		} else {
			intensitId = R.string.light_sport;
			iconId = R.drawable.walk;
		}
		holder.exerciseIntensit.setText(intensitId);
		holder.exerciseIcon.setImageResource(iconId);
		long utcTime = (Long) mPacket.get(DBInfo.KEY_UTC_TIME);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
		String fromTime = format.format(new Date(utcTime * 1000));
		String toTime = format.format(new Date((utcTime + 15 * 60) * 1000));
		holder.exerciseTime.setText(fromTime + "~" + toTime);
		holder.exercisePoint.setText(mPacket.get(DBInfo.KEY_STEPS) + "步");

		// for test
		// holder.exerciseTime.setText(testStr[position]);
		return convertView;
	}

}
