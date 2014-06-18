package com.tranway.tleshine.model;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.widget.chartview.AbstractSeries;

public class VPagerAdapter extends PagerAdapter {
	private static final String TAG = VPagerAdapter.class.getSimpleName();

	private LayoutInflater mInflater;
	private ActivityAdapter mActivityAdapter;

	private List<ActivityInfo> mActivityInfos;
	private List<AbstractSeries> mChartSeries;
	private List<List<Map<String, Object>>> mEvery15MinPackets;
	private int position;

	public VPagerAdapter(Context context, List<ActivityInfo> mActivityInfos, List<AbstractSeries> mChartSeries,
			List<List<Map<String, Object>>> mEvery15MinPackets) {
		mInflater = LayoutInflater.from(context);
		mActivityAdapter = new ActivityAdapter(context);
		this.mActivityInfos = mActivityInfos;
		this.mChartSeries = mChartSeries;
		this.mEvery15MinPackets = mEvery15MinPackets;

		notifyDataSetChanged();
	}

	class ViewHolder {
		ListView mListView;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		this.position = position;
		Log.d(TAG, "---load view page item: " + position);

		ViewHolder holder = new ViewHolder();
		View view = mInflater.inflate(R.layout.layout_viewpager, null);
		holder.mListView = (ListView) view.findViewById(R.id.activity_list);

		/** fill ListView content */
		ActivityInfo mInfo = mActivityInfos.get(position);
		// TODO ... for test
		AbstractSeries mSeries = mChartSeries.get(0);
		// TODO ... for test
		List<Map<String, Object>> mPackets = mEvery15MinPackets.get(0);
		holder.mListView.setAdapter(mActivityAdapter);
		mActivityAdapter.setActivityData(mInfo, mSeries, mPackets);

		if (view.getParent() == null) {
			((ViewPager) container).addView(view, position);
		} else {
			((ViewGroup) view.getParent()).removeView(view);
			((ViewPager) container).addView(view, position);
		}

		return view;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mActivityInfos.size();
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
