package com.tranway.Oband_Fitnessband.viewMainTabs;

import java.util.ArrayList;
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
import com.tranway.tleshine.database.DBManager;

@SuppressLint("NewApi")
// !!!!!!!!!!!!!!!!!!
public class ActivityActivity extends Activity {
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
}
