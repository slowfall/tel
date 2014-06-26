package com.tranway.Oband_Fitnessband.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.telshine.database.DBInfo;

public class ExerciseContentAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mContentList = new ArrayList<Map<String, Object>>();

	public ExerciseContentAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
	}

	public void setContentList(List<Map<String, Object>> contentList) {
		mContentList = contentList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mContentList.size();
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
		ImageView exerciseIcon;
		TextView exerciseIntensit;
		TextView exerciseTime;
		TextView exercisePoint;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list_exercise, null);
			holder = new ViewHolder();
			holder.exerciseIcon = (ImageView) convertView.findViewById(R.id.icon_exercise);
			holder.exerciseIntensit = (TextView) convertView.findViewById(R.id.txt_intensity);
			holder.exerciseTime = (TextView) convertView.findViewById(R.id.txt_time);
			holder.exercisePoint = (TextView) convertView.findViewById(R.id.txt_point);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Map<String, Object> content = mContentList.get(position);
		// holder.exerciseIcon.setImageDrawable();
		int steps = (Integer) content.get(DBInfo.KEY_STEPS);
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
		long utcTime = (Long) content.get(DBInfo.KEY_UTC_TIME);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
		String fromTime = format.format(new Date(utcTime * 1000));
		String toTime = format.format(new Date((utcTime + 15 * 60) * 1000));
		holder.exerciseTime.setText(fromTime + "~" + toTime);
		holder.exercisePoint.setText(content.get(DBInfo.KEY_STEPS) + "点");

		return convertView;
	}
}
