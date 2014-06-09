package com.tranway.tleshine.model;

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

import com.tranway.telshine.database.DBEvery15MinPacketHelper;
import com.tranway.tleshine.R;

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
		holder.exerciseIntensit.setText("轻微运动");
		long utcTime = (Long) content.get(DBEvery15MinPacketHelper.KEY_UTC_TIME);
		SimpleDateFormat format = new SimpleDateFormat("HH:MM:SS", Locale.getDefault());
		String fromTime = format.format(new Date(utcTime * 1000));
		String toTime = format.format(new Date((utcTime + 15 * 60) * 1000));
		holder.exerciseTime.setText(fromTime + "~" + toTime);
		holder.exercisePoint.setText(content.get(DBEvery15MinPacketHelper.KEY_STEPS) + "点");

		return convertView;
	}
}
