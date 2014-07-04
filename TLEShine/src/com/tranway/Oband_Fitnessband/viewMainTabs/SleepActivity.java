package com.tranway.Oband_Fitnessband.viewMainTabs;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.BLEPacket;
import com.tranway.Oband_Fitnessband.model.UserGoalKeeper;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.model.Util;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsNightGoalActivity;
import com.tranway.Oband_Fitnessband.widget.MultiRoundProgressBar;
import com.tranway.Oband_Fitnessband.widget.chartview.AbstractSeries;
import com.tranway.Oband_Fitnessband.widget.chartview.ChartView;
import com.tranway.Oband_Fitnessband.widget.chartview.LabelAdapter;
import com.tranway.Oband_Fitnessband.widget.chartview.LabelAdapter.LabelOrientation;
import com.tranway.Oband_Fitnessband.widget.chartview.LinearSeries;
import com.tranway.Oband_Fitnessband.widget.chartview.LinearSeries.LinearPoint;
import com.tranway.tleshine.database.DBInfo;
import com.tranway.tleshine.database.DBManager;

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
		if (mSleepData.size() <= 0) {
			Map<String, Object> data = new TreeMap<String, Object>();
			data.put(DBInfo.KEY_UTC_TIME, System.currentTimeMillis() / 1000);
			data.put(DBInfo.KEY_SLEEP_DEEP_TIME, 0l);
			data.put(DBInfo.KEY_SLEEP_GOAL,
					60l * UserGoalKeeper.readSleepGoalTime(getApplicationContext()));
			data.put(DBInfo.KEY_SLEEP_SHALLOW_TIME, 0l);
			data.put(DBInfo.KEY_SLEEP_PACKET, new byte[0]);
			mSleepData.add(data);
		}
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
		private Context mContext;
		private long todayUtcTime;

		public ViewPagerAdapter(Context context, ViewPager mPager,
		// ArrayList<DailyExercise> mList) {
				List<Map<String, Object>> mList) {
			mInflater = LayoutInflater.from(context);
			this.mSleepDatas = mList;
			mContext = context;

			Calendar calendar = Calendar.getInstance();
			TimeZone zone = calendar.getTimeZone();
			long timeOffset = zone.getOffset(calendar.getTimeInMillis()) / 1000;
			todayUtcTime = (System.currentTimeMillis() / 1000 + timeOffset)
					/ Util.SECONDS_OF_ONE_DAY;
		}

		class ViewHolder {
			MultiRoundProgressBar progress;
			TextView timeTxt;
			TextView sleepGoal;
			TextView sleepCondition;
			Button showDetail;
			ImageButton editGoal;
			ChartView chartView;
			LinearLayout chartLayout;
			LinearLayout detailLayout;
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
			final ViewHolder holder = new ViewHolder();
			View view = mInflater.inflate(R.layout.layout_sleep, null);
			holder.progress = (MultiRoundProgressBar) view.findViewById(R.id.progress);
			holder.timeTxt = (TextView) view.findViewById(R.id.tv_sleep_date);
			holder.sleepGoal = (TextView) view.findViewById(R.id.tv_sleep_goal);
			holder.sleepCondition = (TextView) view.findViewById(R.id.tv_sleep_condition);
			holder.showDetail = (Button) view.findViewById(R.id.btn_show_detail);
			holder.editGoal = (ImageButton) view.findViewById(R.id.ib_sleep_edit_goal);
			holder.chartView = (ChartView) view.findViewById(R.id.chart_view);
			holder.chartLayout = (LinearLayout) view.findViewById(R.id.ll_sleep_chart_layout);
			holder.detailLayout = (LinearLayout) view.findViewById(R.id.ll_sleep_detail_layout);
			holder.sleepTimeTotal = (TextView) view.findViewById(R.id.tv_sleep_time_total);
			holder.sleepTimeShallow = (TextView) view.findViewById(R.id.tv_sleep_time_shallow);
			holder.sleepTimeDeep = (TextView) view.findViewById(R.id.tv_sleep_time_deep);
			holder.sleepWakeTimes = (TextView) view.findViewById(R.id.tv_sleep_wake_times);

			Map<String, Object> info = mSleepDatas.get(position);
			long deepSleepTime = (Long) info.get(DBInfo.KEY_SLEEP_DEEP_TIME);
			long shallowSleepTime = (Long) info.get(DBInfo.KEY_SLEEP_SHALLOW_TIME);
			long sleepGoal = (Long) info.get(DBInfo.KEY_SLEEP_GOAL);
			if (sleepGoal < 0) {
				sleepGoal = 0;
			}
			float rate = 1.0f;
			long sleepTotalTime = deepSleepTime + shallowSleepTime;
			if (sleepGoal < sleepTotalTime) {
				rate = sleepTotalTime / (float) sleepGoal;
			}
			int max = (int) (sleepGoal / 60);
			holder.progress.setMax(max);
			holder.progress.setProgress((int) (sleepTotalTime / 60 * rate));
			holder.progress.setProgressNext((int) (deepSleepTime / 60 * rate));

			long sleepUtcTime = (Long) info.get(DBInfo.KEY_UTC_TIME);
			long utcTime = sleepUtcTime / Util.SECONDS_OF_ONE_DAY;
			if (todayUtcTime == utcTime) {
				holder.timeTxt.setText(R.string.today);
			} else if (todayUtcTime - utcTime == 1) {
				holder.timeTxt.setText(R.string.yesterday);
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
			} else if (quality > 0.25) {
				holder.sleepCondition.setText(R.string.sleep_good);
			} else {
				holder.sleepCondition.setText("");
			}

			holder.showDetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (holder.detailLayout.isShown()) {
						holder.detailLayout.setVisibility(View.INVISIBLE);
						holder.chartLayout.setVisibility(View.VISIBLE);
					} else {
						holder.detailLayout.setVisibility(View.VISIBLE);
						holder.chartLayout.setVisibility(View.INVISIBLE);
					}
				}
			});

			holder.editGoal.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, SettingsNightGoalActivity.class);
					startActivity(intent);
				}
			});

			holder.sleepTimeTotal.setText(formatTime(sleepTotalTime));
			holder.sleepTimeShallow.setText(formatTime(shallowSleepTime));
			holder.sleepTimeDeep.setText(formatTime(deepSleepTime));
			holder.sleepWakeTimes.setText("0");

			holder.chartView.setGridLinesHorizontal(3);
			holder.chartView.setGridLinesVertical(0);
			String[] labels = { "0h", "6h", "12h", "18h", "24h" };
			LabelAdapter mAdapter = new LabelAdapter(mContext, LabelOrientation.HORIZONTAL);
			mAdapter.setLabelValues(labels);
			holder.chartView.setBottomLabelAdapter(mAdapter);
			holder.chartView.clearSeries();
			holder.chartView.addSeries(makeSeries(info));

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

		private AbstractSeries makeSeries(Map<String, Object> packets) {
			LinearSeries series = new LinearSeries();
			series.setLineColor(mContext.getResources().getColor(R.color.yellow));
			series.setLineWidth(5);
			series.addPoint(new LinearPoint(0, 0));
			series.addPoint(new LinearPoint(6, 0));
			series.addPoint(new LinearPoint(12, 0));
			series.addPoint(new LinearPoint(18, 0));
			series.addPoint(new LinearPoint(24, 0));
			BLEPacket tool = new BLEPacket();
			long startTime = (Long) packets.get(DBInfo.KEY_UTC_TIME);
			byte[] sleepBytes = new byte[0];
			if (packets.containsKey(DBInfo.KEY_SLEEP_PACKET)) {
				sleepBytes = (byte[]) packets.get(DBInfo.KEY_SLEEP_PACKET);
			}
			for (int i = 0; i < sleepBytes.length; i++) {
				long utcTime = startTime + i * 5 * 60;
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(utcTime * 1000);
				long second = calendar.get(Calendar.HOUR_OF_DAY) * 3600
						+ calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
				float x = second / 3600.0f;
				series.addPoint(new LinearPoint(x, tool.bytesToInt(new byte[] { sleepBytes[i] })));
			}

			return series;
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
