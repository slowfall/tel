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

public class ExerciseContentAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<ExerciseContent> mContentList = new ArrayList<ExerciseContent>();

	public ExerciseContentAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);
	}

	public void setContentList(List<ExerciseContent> contentList) {
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

		final ExerciseContent content = mContentList.get(position);
		// holder.exerciseIcon.setImageDrawable();
		holder.exerciseIntensit.setText("轻微运动");
		holder.exerciseTime.setText(content.getFromTime() + "~" + content.getToTime());
		holder.exercisePoint.setText(content.getPoint() + "点");

		return convertView;
	}
}
