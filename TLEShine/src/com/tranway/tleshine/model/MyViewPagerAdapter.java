package com.tranway.tleshine.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.R.anim;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tranway.telshine.database.DBInfo;
import com.tranway.telshine.database.DBManager;
import com.tranway.tleshine.R;
import com.tranway.tleshine.widget.CustomizedListView;
import com.tranway.tleshine.widget.RoundProgressBar;
import com.tranway.tleshine.widget.chartview.AbstractSeries;
import com.tranway.tleshine.widget.chartview.ChartView;
import com.tranway.tleshine.widget.chartview.LabelAdapter;
import com.tranway.tleshine.widget.chartview.LabelAdapter.LabelOrientation;
import com.tranway.tleshine.widget.chartview.LinearSeries;
import com.tranway.tleshine.widget.chartview.LinearSeries.LinearPoint;

public class MyViewPagerAdapter extends PagerAdapter {
	private static final String TAG = MyViewPagerAdapter.class.getSimpleName();

	private LayoutInflater mInflater;
	private Context context;
	private ActivityListAdapter mListAdapter;

	private List<ActivityInfo> mActivityInfos;
	private List<AbstractSeries> mChartSeries;
	private List<List<Map<String, Object>>> mEvery15MinPackets;
	private Animation mFadeinAnimation;
	private Animation mFadeoutAnimation;
	private long mTimeOffset = 0;

	private int position;

	public MyViewPagerAdapter(Context context, List<ActivityInfo> mActivityInfos,
			List<AbstractSeries> mChartSeries, List<List<Map<String, Object>>> mEvery15MinPackets) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		mListAdapter = new ActivityListAdapter(context);
		this.mActivityInfos = mActivityInfos;
		this.mChartSeries = mChartSeries;
		this.mEvery15MinPackets = mEvery15MinPackets;
		mFadeinAnimation = AnimationUtils.loadAnimation(context, anim.fade_in);
		mFadeinAnimation.setFillAfter(true);
		mFadeoutAnimation = AnimationUtils.loadAnimation(context, anim.fade_out);
		mFadeoutAnimation.setFillAfter(true);

		Calendar calendar = Calendar.getInstance();
		TimeZone zone = calendar.getTimeZone();
		mTimeOffset = zone.getOffset(calendar.getTimeInMillis()) / 1000;

		notifyDataSetChanged();
	}

	class ViewHolder {
		// top circle view
		RoundProgressBar progress;
		TextView timeTxt;
		TextView week;
		TextView syncTime;
		LinearLayout overviewLayout;
		TextView goalSteps;
		TextView finishedSteps;
		LinearLayout detailLayout;
		TextView distance;
		TextView calorie;
		TextView steps;
		Button showDetailBtn;
		// middle chart view
		ChartView chartView;
		// bottom list view
		CustomizedListView listView;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		this.position = position;
		Log.d(TAG, "---load view page item: " + position);
		final ViewHolder holder = new ViewHolder();
		View view = mInflater.inflate(R.layout.layout_viewpager, null);
		holder.progress = (RoundProgressBar) view.findViewById(R.id.progress);
		holder.timeTxt = (TextView) view.findViewById(R.id.tv_activity_date);
		holder.week = (TextView) view.findViewById(R.id.tv_activity_weekday);
		holder.syncTime = (TextView) view.findViewById(R.id.tv_sync_time);

		holder.overviewLayout = (LinearLayout) view.findViewById(R.id.ll_activity_overview);
		holder.goalSteps = (TextView) view.findViewById(R.id.tv_activity_steps_goal);
		holder.finishedSteps = (TextView) view.findViewById(R.id.tv_activity_steps_finished);
		holder.detailLayout = (LinearLayout) view.findViewById(R.id.ll_activity_detail);
		holder.distance = (TextView) view.findViewById(R.id.tv_activity_distance);
		holder.calorie = (TextView) view.findViewById(R.id.tv_activity_calorie);
		holder.steps = (TextView) view.findViewById(R.id.tv_activity_steps);
		holder.showDetailBtn = (Button) view.findViewById(R.id.btn_slow_detail);
		ActivityInfo info = mActivityInfos.get(position);

		long utcTime = info.getUtcTime();
		long userId = UserInfoKeeper.readUserInfo(context, UserInfoKeeper.KEY_ID, -1l);
		long utcTimeSeconds = utcTime * Util.SECONDS_OF_ONE_DAY;
		List<Map<String, Object>> every15MinPackets = DBManager.queryEvery15MinPackets(userId,
				utcTimeSeconds, utcTimeSeconds + Util.SECONDS_OF_ONE_DAY);
		holder.progress.setProgress(info.getSteps(), info.getGoal());

		long todayUtcTime = (System.currentTimeMillis() / 1000 + mTimeOffset)
				/ Util.SECONDS_OF_ONE_DAY;
		Util.logD("ViewPagerAdapter", info.toString() + ", todayUtcTime:" + todayUtcTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(utcTime * Util.SECONDS_OF_ONE_DAY * 1000);
		holder.week.setText(context.getResources().getStringArray(R.array.weekdays)[calendar
				.get(Calendar.DAY_OF_WEEK) - 1]);
		if (todayUtcTime == utcTime) {
			holder.timeTxt.setText(R.string.today);
		} else if (todayUtcTime - utcTime == 1) {
			holder.timeTxt.setText(R.string.yestoday);
		} else {
			SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.getDefault());
			Date date = new Date(utcTimeSeconds * 1000);
			holder.timeTxt.setText(df.format(date));
		}
		holder.syncTime.setText(makeSyncTimeString(UserInfoKeeper.readUserInfo(context,
				UserInfoKeeper.KEY_SYNC_BLUETOOTH_TIME, 0l)));
		holder.goalSteps.setText(String.valueOf(info.getGoal()));
		holder.finishedSteps.setText(String.valueOf(info.getSteps()));
		holder.showDetailBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (holder.detailLayout.isShown()) {
					// holder.detailLayout.startAnimation(mFadeinAnimation);
					// holder.overviewLayout.startAnimation(mFadeoutAnimation);
					holder.detailLayout.setVisibility(View.INVISIBLE);
					holder.overviewLayout.setVisibility(View.VISIBLE);
				} else {
					// holder.detailLayout.startAnimation(mFadeoutAnimation);
					// holder.overviewLayout.startAnimation(mFadeinAnimation);
					holder.detailLayout.setVisibility(View.VISIBLE);
					holder.overviewLayout.setVisibility(View.INVISIBLE);
				}
			}
		});
		holder.distance.setText(String.valueOf(formatKm(info.getDistance())));
		holder.calorie.setText(String.valueOf(info.getCalorie() / 10.0f));
		holder.steps.setText(String.valueOf(info.getSteps()));

		// load middle chart view
		holder.chartView = (ChartView) view.findViewById(R.id.chart_view);
		holder.chartView.setGridLinesHorizontal(3);
		holder.chartView.setGridLinesVertical(0);
		String[] labels = { "0h", "6h", "12h", "18h", "24h" };
		LabelAdapter mAdapter = new LabelAdapter(context, LabelOrientation.HORIZONTAL);
		mAdapter.setLabelValues(labels);
		holder.chartView.setBottomLabelAdapter(mAdapter);
		holder.chartView.clearSeries();
		holder.chartView.addSeries(makeSeries(every15MinPackets)); // .get(position)

		// load bottom list view
		holder.listView = (CustomizedListView) view
				.findViewById(R.id.act_solution_3_mylinearlayout);

		mListAdapter.setActivityData(every15MinPackets); // .get(position)
		holder.listView.setAdapter(mListAdapter);

		/**
		 * 注意：1、需要确定getCount()的返回值，即ViewPager的页面数量 2、感觉
		 * Every15MinPackets可以根据当前页面的时间，从数据库里读取
		 */

		container.addView(view);

		return view;
	}

	private String makeSyncTimeString(long lastSyncTime) {
		if (lastSyncTime == 0) {
			return context.getString(R.string.not_sync);
		}
		String syncTimeString = "";
		long nowTime = System.currentTimeMillis();
		long diff = (nowTime - lastSyncTime) / 1000;
		long minute = diff / 60;
		long hours = minute / 60;
		long days = hours / 24;
		long weeks = days / 7;
		long mooths = days / 30;
		if (mooths > 0) {
			syncTimeString = String.format(context.getString(R.string.mooth_ago), mooths);
		} else if (weeks > 0) {
			syncTimeString = String.format(context.getString(R.string.week_ago), weeks);
		} else if (days > 0) {
			syncTimeString = String.format(context.getString(R.string.day_ago), days);
		} else if (hours > 0) {
			syncTimeString = String.format(context.getString(R.string.hour_ago), hours);
		} else if (minute > 5) {
			syncTimeString = String.format(context.getString(R.string.minute_ago), minute);
		} else {
			return context.getString(R.string.last_sync_time_just_now);
		}
		return context.getString(R.string.last_sync_time_string) + syncTimeString;
	}

	private String formatKm(int miles) {
		float km = miles / 1000.0f;
		if (km <= 0.01) {
			return "0";
		}
		return String.format(Locale.getDefault(), "%.2f", km);
	}

	private AbstractSeries makeSeries(List<Map<String, Object>> packets) {
		LinearSeries series = new LinearSeries();
		series.setLineColor(context.getResources().getColor(R.color.yellow));
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
			long second = utcTime % Util.SECONDS_OF_ONE_DAY;
			float x = second / 3600f;
			// Util.logD(TAG, "utcTime:" + utcTime + ", second:" + second +
			// ", x:" + x + ", step:" + step);
			series.addPoint(new LinearPoint(x, step));
		}
		return series;
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
