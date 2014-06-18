package com.tranway.tleshine.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.widget.CustomizedListView;
import com.tranway.tleshine.widget.RoundProgressBar;
import com.tranway.tleshine.widget.chartview.AbstractSeries;
import com.tranway.tleshine.widget.chartview.ChartView;
import com.tranway.tleshine.widget.chartview.LabelAdapter;
import com.tranway.tleshine.widget.chartview.LabelAdapter.LabelOrientation;

public class MyViewPagerAdapter extends PagerAdapter {
	private static final String TAG = MyViewPagerAdapter.class.getSimpleName();

	private LayoutInflater mInflater;
	private Context context;
	private ActivityListAdapter mListAdapter;

	private List<ActivityInfo> mActivityInfos;
	private List<AbstractSeries> mChartSeries;
	private List<List<Map<String, Object>>> mEvery15MinPackets;

	private int position;

	public MyViewPagerAdapter(Context context, List<ActivityInfo> mActivityInfos, List<AbstractSeries> mChartSeries,
			List<List<Map<String, Object>>> mEvery15MinPackets) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		mListAdapter = new ActivityListAdapter(context);
		this.mActivityInfos = mActivityInfos;
		this.mChartSeries = mChartSeries;
		this.mEvery15MinPackets = mEvery15MinPackets;

		notifyDataSetChanged();
	}

	class ViewHolder {
		// top circle view
		RoundProgressBar mProgress;
		TextView mTimeTxt;
		// middle chart view
		ChartView chartView;
		// bottom list view
		CustomizedListView mListView;
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
		holder.mProgress = (RoundProgressBar) view.findViewById(R.id.progress);
		holder.mTimeTxt = (TextView) view.findViewById(R.id.txt_date);

		ActivityInfo info = mActivityInfos.get(position);
		holder.mProgress.setProgress(info.getSteps(),
				UserGoalKeeper.readExerciseGoalPoint(MyApplication.getAppContext()));
		long todayUtcTime = System.currentTimeMillis() / 1000 / 3600 / 24;
		long utcTime = info.getUtcTime();
		Util.logD("ViewPagerAdapter", info.toString() + ", todayUtcTime:" + todayUtcTime);
		if (todayUtcTime == utcTime) {
			holder.mTimeTxt.setText(R.string.today);
		} else if (todayUtcTime - utcTime == 1) {
			holder.mTimeTxt.setText(R.string.yestoday);
		} else {
			SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.getDefault());
			Date date = new Date(utcTime * 1000 * 3600 * 24);
			holder.mTimeTxt.setText(df.format(date));
		}

		// load middle chart view
		holder.chartView = (ChartView) view.findViewById(R.id.chart_view);
		holder.chartView.setGridLinesHorizontal(3);
		holder.chartView.setGridLinesVertical(0);
		String[] labels = { "0h", "6h", "12h", "18h", "24h" };
		LabelAdapter mAdapter = new LabelAdapter(context, LabelOrientation.HORIZONTAL);
		mAdapter.setLabelValues(labels);
		holder.chartView.setBottomLabelAdapter(mAdapter);
		holder.chartView.clearSeries();
		holder.chartView.addSeries(mChartSeries.get(0)); // .get(position)

		// load bottom list view
		holder.mListView = (CustomizedListView) view.findViewById(R.id.act_solution_3_mylinearlayout);
		mListAdapter.setActivityData(mEvery15MinPackets.get(0)); // .get(position)
		holder.mListView.setAdapter(mListAdapter);

		/**
		 * 注意：1、需要确定getCount()的返回值，即ViewPager的页面数量
		 *		2、感觉 Every15MinPackets可以根据当前页面的时间，从数据库里读取
		 */
		
		container.addView(view);

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
