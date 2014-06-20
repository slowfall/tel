package com.tranway.tleshine.viewMainTabs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.tranway.telshine.database.DBInfo;
import com.tranway.telshine.database.DBManager;
import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ActivityInfo;
import com.tranway.tleshine.model.MyViewPagerAdapter;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.widget.chartview.AbstractSeries;
import com.tranway.tleshine.widget.chartview.LinearSeries;
import com.tranway.tleshine.widget.chartview.LinearSeries.LinearPoint;

@SuppressLint("NewApi")
// !!!!!!!!!!!!!!!!!!
public class TestActivity extends Activity {
	private static final String TAG = TestActivity.class.getSimpleName();

	private static final long SECONDS_OF_ONE_DAY = 2400 * 36;
	private static final float VIEWPAGE_HEIGHT_PERCENT = 0.5f;

	// private JazzyViewPager mPager;
	private ViewPager mViewPager;
	private TextView mTextNoActivityTips;
	private MyViewPagerAdapter mAdapter;

	private List<List<Map<String, Object>>> m15MinPackets = new ArrayList<List<Map<String, Object>>>();
	private List<AbstractSeries> mChartSeries = new ArrayList<AbstractSeries>();
	private List<ActivityInfo> mActivityInfos = new ArrayList<ActivityInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_test);

		// Test code
		// ActivityInfo activityInfo = new ActivityInfo();
		// activityInfo.setCalorie(100);
		// activityInfo.setDistance(1000);
		// activityInfo.setSteps(100);
		// activityInfo.setUtcTime(System.currentTimeMillis() / 1000 / (3600 *
		// 24));
		// DBManager.addActivityInfo(12, activityInfo);
		//
		// List<Map<String, Object>> mEvery15MinPackets = new
		// ArrayList<Map<String, Object>>();
		// long userId = UserInfoKeeper.readUserInfo(this,
		// UserInfoKeeper.KEY_ID, -1l);
		// mActivityInfos.clear();
		// mActivityInfos.addAll(DBManager.queryActivityInfo(userId));
		//
		// // ----- test for add more view pager -----
		// mActivityInfos.add(activityInfo);
		// mActivityInfos.add(activityInfo);
		// Log.d(TAG, "----Activity info : " + mActivityInfos.size());
		// // ----- test for add more view pager -----
		//
		// long utcTime = System.currentTimeMillis() / 1000;
		// long dayUtc = utcTime / SECONDS_OF_ONE_DAY;
		// mEvery15MinPackets.clear();
		// mEvery15MinPackets.addAll(DBManager.queryEvery15MinPackets(userId,
		// dayUtc * SECONDS_OF_ONE_DAY, (dayUtc + 1)
		// * SECONDS_OF_ONE_DAY));
		//
		// AbstractSeries series = makeSeries(mEvery15MinPackets);
		// mChartSeries.add(series);
		// m15MinPackets.add(mEvery15MinPackets);
		//
		// // ----- for test chart view
		// mChartSeries.clear();
		// LinearSeries mSseries = new LinearSeries();
		// mSseries.setLineColor(getResources().getColor(R.color.yellow));
		// mSseries.setLineWidth(5);
		// mSseries.addPoint(new LinearPoint(0, 100));
		// mSseries.addPoint(new LinearPoint(6, 30));
		// mSseries.addPoint(new LinearPoint(12, 400));
		// mSseries.addPoint(new LinearPoint(18, 70));
		// mSseries.addPoint(new LinearPoint(24, 200));
		// mChartSeries.add(mSseries);
		// ----- for test chart view

		initView();
	}

	@Override
	public void onResume() {
		super.onResume();
		long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1l);
		mActivityInfos.clear();
		mActivityInfos.addAll(DBManager.queryActivityInfo(userId));
		mAdapter.notifyDataSetChanged();
		if (mActivityInfos.size() > 0) {
			mViewPager.setVisibility(View.VISIBLE);
			mViewPager.setCurrentItem(mActivityInfos.size() - 1);
			mTextNoActivityTips.setVisibility(View.GONE);
		} else {
			mViewPager.setVisibility(View.INVISIBLE);
			mTextNoActivityTips.setVisibility(View.VISIBLE);
		}
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTextNoActivityTips = (TextView) findViewById(R.id.tv_no_activity_tips);
		mAdapter = new MyViewPagerAdapter(this, mActivityInfos, mChartSeries, m15MinPackets);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setCurrentItem(0);
		mAdapter.notifyDataSetChanged();
	}

	private AbstractSeries makeSeries(List<Map<String, Object>> packets) {
		LinearSeries series = new LinearSeries();
		series.setLineColor(getResources().getColor(R.color.yellow));
		series.setLineWidth(5);
		series.addPoint(new LinearPoint(0, 0));
		series.addPoint(new LinearPoint(6, 0));
		series.addPoint(new LinearPoint(12, 0));
		series.addPoint(new LinearPoint(18, 0));
		// series.addPoint(new LinearPoint(24, 0));
		for (Map<String, Object> every15MinPacket : packets) {
			long utcTime = (Long) every15MinPacket.get(DBInfo.KEY_UTC_TIME);
			int step = (Integer) every15MinPacket.get(DBInfo.KEY_STEPS);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(utcTime * 1000);
			long second = utcTime % SECONDS_OF_ONE_DAY;
			float x = second / 3600f;
			// Util.logD(TAG, "utcTime:" + utcTime + ", second:" + second +
			// ", x:" + x + ", step:" + step);
			series.addPoint(new LinearPoint(x, step));
		}
		return series;
	}

}
