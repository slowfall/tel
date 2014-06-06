package com.tranway.tleshine.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.widget.RoundProgressBar;

public class ViewPagerAdapter extends PagerAdapter {

	private LayoutInflater mInflater;
	private ArrayList<DailyExercise> mList;
	private int position;

	public ViewPagerAdapter(Context context, ViewPager mPager,
			ArrayList<DailyExercise> mList) {
		mInflater = LayoutInflater.from(context);
		this.mList = mList;
	}

	class ViewHolder {
		RoundProgressBar mProgress;
		TextView mTimeTxt;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		this.position = position;
		ViewHolder holder = new ViewHolder();
		View view = mInflater.inflate(R.layout.layout_point, null);
		holder.mProgress = (RoundProgressBar) view.findViewById(R.id.progress);
		holder.mTimeTxt = (TextView) view.findViewById(R.id.txt_date);

		DailyExercise ex = mList.get(position);
		holder.mProgress.setProgress(ex.getAchieve(), ex.getGoal());

		SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.CHINA);
		Date date = new Date(ex.getDate() * 1000);
		holder.mTimeTxt.setText(df.format(date));

		container.addView(view);
		return view;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object obj) {
		// container.removeView(mPager.findViewFromObject(position));
		((ViewPager) container).removeView((View) (obj));
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	public int getCurrentPosition() {
		return this.position;
	}
}
