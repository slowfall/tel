package com.tranway.tleshine.viewMainTabs;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tranway.telshine.database.DBInfo;
import com.tranway.telshine.database.DBManager;
import com.tranway.tleshine.R;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.model.Util;
import com.tranway.tleshine.widget.MultiRoundProgressBar;
import com.tranway.tleshine.widget.RoundProgressBar;

public class SleepActivity extends Activity {
	private ViewPager mViewPager;
	private ViewPagerAdapter mAdapter;

	private List<Map<String, Object>> mSleepData = new ArrayList<Map<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep);

		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateViewPager();
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mAdapter = new ViewPagerAdapter(this, mViewPager, mSleepData);
		mViewPager.setAdapter(mAdapter);
		MyOnPageChangeListener pageChangeListener = new MyOnPageChangeListener();
		mViewPager.setOnPageChangeListener(pageChangeListener);
	}

	private void updateViewPager() {
		long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1l);

		mSleepData.clear();
		mSleepData.addAll(DBManager.queryAllSleepInfos(userId));
		mAdapter.notifyDataSetChanged();
		if (mSleepData.size() > 0) {
			mViewPager.setCurrentItem(mSleepData.size() - 1);
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public class ViewPagerAdapter extends PagerAdapter {

		private LayoutInflater mInflater;
		// private ArrayList<DailyExercise> mList;
		private List<Map<String, Object>> mSleepDatas;
		private int position;

		public ViewPagerAdapter(Context context, ViewPager mPager,
		// ArrayList<DailyExercise> mList) {
				List<Map<String, Object>> mList) {
			mInflater = LayoutInflater.from(context);
			this.mSleepDatas = mList;
		}

		class ViewHolder {
			RoundProgressBar  mProgress;
			TextView mTimeTxt;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			this.position = position;
			ViewHolder holder = new ViewHolder();
			View view = mInflater.inflate(R.layout.layout_point, null);
			holder.mProgress = (RoundProgressBar) view.findViewById(R.id.progress);
			holder.mTimeTxt = (TextView) view.findViewById(R.id.txt_date);

			Map<String, Object> info = mSleepDatas.get(position);
			long deepSleepTime = (Long) info.get(DBInfo.KEY_SLEEP_DEEP_TIME);
			long shallowSleepTime = (Long) info.get(DBInfo.KEY_SLEEP_SHALLOW_TIME);
			long sleepGoal = (Long) info.get(DBInfo.KEY_SLEEP_GOAL);
			holder.mProgress.setProgress(deepSleepTime + shallowSleepTime, sleepGoal);

			long todayUtcTime = System.currentTimeMillis() / 1000 / Util.SECONDS_OF_ONE_DAY;
			long sleepUtcTime = (Long) info.get(DBInfo.KEY_UTC_TIME);
			long utcTime = sleepUtcTime / Util.SECONDS_OF_ONE_DAY;
			if (todayUtcTime == utcTime) {
				holder.mTimeTxt.setText(R.string.today);
			} else if (todayUtcTime - utcTime == 1) {
				holder.mTimeTxt.setText(R.string.yestoday);
			} else {
				SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.getDefault());
				Date date = new Date(utcTime * 1000 * 3600 * 24);
				holder.mTimeTxt.setText(df.format(date));
			}
			container.addView(view);
			return view;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mSleepDatas.size();
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
}
