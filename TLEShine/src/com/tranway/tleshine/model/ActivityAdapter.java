package com.tranway.tleshine.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tranway.telshine.database.DBInfo;
import com.tranway.tleshine.R;
import com.tranway.tleshine.R.string;
import com.tranway.tleshine.widget.RoundProgressBar;
import com.tranway.tleshine.widget.chartview.AbstractSeries;
import com.tranway.tleshine.widget.chartview.ChartView;
import com.tranway.tleshine.widget.chartview.LabelAdapter;
import com.tranway.tleshine.widget.chartview.LabelAdapter.LabelOrientation;

public class ActivityAdapter extends BaseAdapter {
	private static final String TAG = "ActivityAdapter";

	private LayoutInflater mInflater;
	private Context context;

	/** current ViewPager activity information */
	private ActivityInfo mActivityInfo;
	/** current ViewPager chart view series */
	private AbstractSeries mChartSeries;
	/** current ViewPager activity every 15Min packets */
	private List<Map<String, Object>> mEvery15MinPackets = new ArrayList<Map<String, Object>>();

	private String[] testStr = { "one", "two", "three", "four" };

	public ActivityAdapter(Context context) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setActivityData(ActivityInfo mActivityInfo, AbstractSeries mChartSeries,
			List<Map<String, Object>> mEvery15MinPackets) {
		this.mActivityInfo = mActivityInfo;
		this.mChartSeries = mChartSeries;
		this.mEvery15MinPackets = mEvery15MinPackets;

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return mEvery15MinPackets.size() + 2;
		return testStr.length + 2;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * Activity information view holder
	 * 
	 * @author SHZ
	 */
	class ViewActivityHolder {
		RoundProgressBar mProgress;
		TextView mTimeTxt;
	}

	/**
	 * Activity Chart view holder
	 * 
	 * @author SHZ
	 */
	class ViewChartHolder {
		ChartView chartView;
	}

	/**
	 * Every 15Mins data view holder
	 * 
	 * @author SHZ
	 */
	class ViewContentHolder {
		ImageView exerciseIcon;
		TextView exerciseIntensit;
		TextView exerciseTime;
		TextView exercisePoint;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			Log.d(TAG, "---loading first item---" + position);
			ViewActivityHolder holder;
			// if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_point, null);
			holder = new ViewActivityHolder();
			holder.mProgress = (RoundProgressBar) convertView.findViewById(R.id.progress);
			holder.mTimeTxt = (TextView) convertView.findViewById(R.id.txt_date);
			convertView.setTag(holder);
			// } else {
			// holder = (ViewActivityHolder) convertView.getTag();
			// }

			holder.mProgress.setProgress(mActivityInfo.getSteps(),
					UserGoalKeeper.readExerciseGoalPoint(MyApplication.getAppContext()));
			Log.d(TAG,
					"----step=" + mActivityInfo.getSteps() + "---goal="
							+ UserGoalKeeper.readExerciseGoalPoint(MyApplication.getAppContext()));
			long todayUtcTime = System.currentTimeMillis() / 1000 / 3600 / 24;
			long utcTime = mActivityInfo.getUtcTime();
			Util.logD("ViewPagerAdapter", mActivityInfo.toString() + ", todayUtcTime:" + todayUtcTime);
			if (todayUtcTime == utcTime) {
				holder.mTimeTxt.setText(R.string.today);
			} else if (todayUtcTime - utcTime == 1) {
				holder.mTimeTxt.setText(R.string.yestoday);
			} else {
				SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.getDefault());
				Date date = new Date(utcTime * 1000 * 3600 * 24);
				holder.mTimeTxt.setText(df.format(date));
			}

			return convertView;
		} else if (position == 1) {
			Log.d(TAG, "---loading second item---" + position);
			ViewChartHolder holder;
			// if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_chartview, null);
			holder = new ViewChartHolder();
			holder.chartView = (ChartView) convertView.findViewById(R.id.chart_view);
			convertView.setTag(holder);
			// } else {
			// holder = (ViewChartHolder) convertView.getTag();
			// }

			holder.chartView.setGridLinesHorizontal(3);
			holder.chartView.setGridLinesVertical(0);
			String[] labels = { "0h", "6h", "12h", "18h", "24h" };
			LabelAdapter mAdapter = new LabelAdapter(context, LabelOrientation.HORIZONTAL);
			mAdapter.setLabelValues(labels);
			holder.chartView.setBottomLabelAdapter(mAdapter);

			holder.chartView.clearSeries();
			holder.chartView.addSeries(mChartSeries);
			return convertView;
		} else {
			Log.d(TAG, "---loading bottom item---" + position);

			ViewContentHolder holder;
			// if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list_exercise, null);
			holder = new ViewContentHolder();
			holder.exerciseIcon = (ImageView) convertView.findViewById(R.id.icon_exercise);
			holder.exerciseIntensit = (TextView) convertView.findViewById(R.id.txt_intensity);
			holder.exerciseTime = (TextView) convertView.findViewById(R.id.txt_time);
			holder.exercisePoint = (TextView) convertView.findViewById(R.id.txt_point);
			convertView.setTag(holder);
			// } else {
			// holder = (ViewContentHolder) convertView.getTag();
			// }

			// final Map<String, Object> mPacket =
			// mEvery15MinPackets.get(position - 2);
			// // holder.exerciseIcon.setImageDrawable();
			// int steps = (Integer) mPacket.get(DBInfo.KEY_STEPS);
			// int intensitId = R.string.light_sport;
			// int iconId = R.drawable.walk;
			// if (steps > 1000) {
			// intensitId = R.string.overdose_sport;
			// iconId = R.drawable.run;
			// } else if (steps > 500) {
			// intensitId = R.string.right_sport;
			// iconId = R.drawable.little_run;
			// } else {
			// intensitId = R.string.light_sport;
			// iconId = R.drawable.walk;
			// }
			// holder.exerciseIntensit.setText(intensitId);
			// holder.exerciseIcon.setImageResource(iconId);
			// long utcTime = (Long) mPacket.get(DBInfo.KEY_UTC_TIME);
			// SimpleDateFormat format = new SimpleDateFormat("HH:mm",
			// Locale.getDefault());
			// String fromTime = format.format(new Date(utcTime * 1000));
			// String toTime = format.format(new Date((utcTime + 15 * 60) *
			// 1000));
			// holder.exerciseTime.setText(fromTime + "~" + toTime);
			// holder.exercisePoint.setText(mPacket.get(DBInfo.KEY_STEPS) +
			// "点");

			// for test
			holder.exercisePoint.setText(testStr[position - 2]);
			return convertView;
		}
	}

}
