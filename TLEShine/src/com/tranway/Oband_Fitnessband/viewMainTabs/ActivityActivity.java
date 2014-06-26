package com.tranway.Oband_Fitnessband.viewMainTabs;

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

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.ActivityInfo;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.model.ViewPagerAdapter;
import com.tranway.Oband_Fitnessband.widget.chartview.AbstractSeries;
import com.tranway.Oband_Fitnessband.widget.chartview.LinearSeries;
import com.tranway.Oband_Fitnessband.widget.chartview.LinearSeries.LinearPoint;
import com.tranway.telshine.database.DBInfo;
import com.tranway.telshine.database.DBManager;

@SuppressLint("NewApi")
// !!!!!!!!!!!!!!!!!!
public class ActivityActivity extends Activity {
	private static final String TAG = ActivityActivity.class.getSimpleName();

	private static final long SECONDS_OF_ONE_DAY = 2400 * 36;
	private static final float VIEWPAGE_HEIGHT_PERCENT = 0.5f;

	// private JazzyViewPager mPager;
	private ViewPager mViewPager;
	private TextView mTextNoActivityTips;
	private ViewPagerAdapter mAdapter;

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
		long continueCount = 0;
		long maxPoint = 0;
		long goalCount = 0;
		for (ActivityInfo info : mActivityInfos) {
			if (info.getGoal() <= info.getSteps()) {
				goalCount += 1;
				continueCount += 1;
			} else {
				continueCount = 0;
			}
			if (info.getSteps() > maxPoint) {
				maxPoint = info.getSteps();
			}
		}
		UserInfoKeeper.writeUserInfo(this, UserInfoKeeper.KEY_CONTINUE_COUNT, continueCount);
		UserInfoKeeper.writeUserInfo(this, UserInfoKeeper.KEY_MAX_POINT, maxPoint);
		UserInfoKeeper.writeUserInfo(this, UserInfoKeeper.KEY_GOAL_COUNT, goalCount);
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTextNoActivityTips = (TextView) findViewById(R.id.tv_no_activity_tips);
		mAdapter = new ViewPagerAdapter(this, mActivityInfos, mChartSeries, m15MinPackets);
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
