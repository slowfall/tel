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
			MultiRoundProgressBar progress;
			TextView timeTxt;
			TextView sleepGoal;
			TextView sleepCondition;
			TextView sleepTimeTotal;
			TextView sleepTimeShallow;
			TextView sleepTimeDeep;
			TextView sleepWakeTimes;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			this.position = position;
			ViewHolder holder = new ViewHolder();
			View view = mInflater.inflate(R.layout.layout_sleep, null);
			holder.progress = (MultiRoundProgressBar) view.findViewById(R.id.progress);
			holder.timeTxt = (TextView) view.findViewById(R.id.tv_sleep_date);
			holder.sleepGoal = (TextView) view.findViewById(R.id.tv_sleep_goal);
			holder.sleepCondition = (TextView) view.findViewById(R.id.tv_sleep_condition);
			holder.sleepTimeTotal = (TextView) view.findViewById(R.id.tv_sleep_time_total);
			holder.sleepTimeShallow = (TextView) view.findViewById(R.id.tv_sleep_time_shallow);
			holder.sleepTimeDeep = (TextView) view.findViewById(R.id.tv_sleep_time_deep);
			holder.sleepWakeTimes = (TextView) view.findViewById(R.id.tv_sleep_wake_times);

			Map<String, Object> info = mSleepDatas.get(position);
			long deepSleepTime = (Long) info.get(DBInfo.KEY_SLEEP_DEEP_TIME);
			long shallowSleepTime = (Long) info.get(DBInfo.KEY_SLEEP_SHALLOW_TIME);
			long sleepGoal = (Long) info.get(DBInfo.KEY_SLEEP_GOAL);
			float rate = 1.0f;
			long sleepTotalTime = deepSleepTime + shallowSleepTime;
			if (sleepGoal < sleepTotalTime) {
				rate = sleepTotalTime / (float) sleepGoal;
			}
			int max = (int) (sleepGoal / 60);
			holder.progress.setMax(max);
			holder.progress.setProgress((int) (sleepTotalTime / 60 * rate));
			holder.progress.setProgressNext((int) (deepSleepTime / 60 * rate));

			long todayUtcTime = System.currentTimeMillis() / 1000 / Util.SECONDS_OF_ONE_DAY;
			long sleepUtcTime = (Long) info.get(DBInfo.KEY_UTC_TIME);
			long utcTime = sleepUtcTime / Util.SECONDS_OF_ONE_DAY;
			if (todayUtcTime == utcTime) {
				holder.timeTxt.setText(R.string.today);
			} else if (todayUtcTime - utcTime == 1) {
				holder.timeTxt.setText(R.string.yestoday);
			} else {
				SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.getDefault());
				Date date = new Date(utcTime * 1000 * 3600 * 24);
				holder.timeTxt.setText(df.format(date));
			}
			holder.sleepGoal.setText(String.format(getString(R.string.n_hour_sleep_goal),
					formatTime(sleepGoal)));
			float quality = deepSleepTime / (float) sleepTotalTime;
			if (quality < 0.2) {
				holder.sleepCondition.setText(R.string.sleep_bad);
			} else if (quality <= 0.25) {
				holder.sleepCondition.setText(R.string.sleep_normal);
			} else {
				holder.sleepCondition.setText(R.string.sleep_good);
			}

			holder.sleepTimeTotal.setText(formatTime(sleepTotalTime));
			holder.sleepTimeShallow.setText(formatTime(shallowSleepTime));
			holder.sleepTimeDeep.setText(formatTime(deepSleepTime));
			holder.sleepWakeTimes.setText("0");

			container.addView(view);
			return view;
		}

		private String formatTime(long seconds) {
			float hour = seconds / 3600.0f;
			if (hour <= 0.01) {
				return "0";
			}
			return String.format(Locale.getDefault(), "%.2f", hour);
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
