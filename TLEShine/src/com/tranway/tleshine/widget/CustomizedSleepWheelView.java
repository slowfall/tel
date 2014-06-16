package com.tranway.tleshine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tranway.tleshine.R;
import com.tranway.tleshine.util.UserInfoUtils;

public class CustomizedSleepWheelView extends LinearLayout {

	private WheelView mFromHourWheel, mFromMinuteWheel;
	private WheelView mToHourWheel, mToMinuteWheel;
	private WheelView mStartWheel, mOverWheel;

	private int default_hour = 0;
	private int default_min = 0;

	public CustomizedSleepWheelView(Context context) {
		super(context);
	}

	public CustomizedSleepWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.customized_wheel_sleep, this, true);
		initialize();
	}

	private void initialize() {
		mFromHourWheel = (WheelView) findViewById(R.id.wheel_left);
		mFromHourWheel.setAdapter(new NumericWheelAdapter(0, 23, null));
		mFromHourWheel.setCyclic(false);
		mFromHourWheel.setCurrentItem(default_hour);
		mFromHourWheel.setLabel("时");

		mFromMinuteWheel = (WheelView) findViewById(R.id.wheel_middle);
		mFromMinuteWheel.setAdapter(new NumericWheelAdapter(0, 59, null));
		mFromMinuteWheel.setCyclic(false);
		mFromMinuteWheel.setCurrentItem(default_min);
		mFromMinuteWheel.setLabel("分");

		mToHourWheel = (WheelView) findViewById(R.id.wheel_right);
		mToHourWheel.setAdapter(new NumericWheelAdapter(0, 23, null));
		mToHourWheel.setCyclic(false);
		mToHourWheel.setCurrentItem(default_hour);
		mToHourWheel.setLabel("时");

		mToMinuteWheel = (WheelView) findViewById(R.id.wheel_last);
		mToMinuteWheel.setAdapter(new NumericWheelAdapter(0, 59, null));
		mToMinuteWheel.setCyclic(false);
		mToMinuteWheel.setCurrentItem(default_min);
		mToMinuteWheel.setLabel("分");

		mStartWheel = (WheelView) findViewById(R.id.wheel_start);
		mStartWheel.setCyclic(false);
		mStartWheel.setLabel("开始");

		mOverWheel = (WheelView) findViewById(R.id.wheel_over);
		mOverWheel.setCyclic(false);
		mOverWheel.setLabel("结束");
	}

	/**
	 * Get the user setting goal sleep time
	 * 
	 * @return return time, such as 21:00-03:00
	 */
	public String getTime() {
		return mFromHourWheel.getCurrentItem() + ":" + mFromMinuteWheel.getCurrentItem() + "-"
				+ mToHourWheel.getCurrentItem() + ":" + mToMinuteWheel.getCurrentItem();
	}

	public void setOnScrollLisenter(OnWheelScrollListener mListener) {
		if (mListener != null) {
			mFromHourWheel.addScrollingListener(mListener);
			mFromMinuteWheel.addScrollingListener(mListener);
			mToHourWheel.addScrollingListener(mListener);
			mToMinuteWheel.addScrollingListener(mListener);
		}
	}

	public void setCurrentGoalRange(String range) {
		if (range == null) {
			return;
		}
		int[] time = UserInfoUtils.convertTimeRangeToTimeArray(range);
		mFromHourWheel.setCurrentItem(time[0]);
		mFromMinuteWheel.setCurrentItem(time[1]);
		mToHourWheel.setCurrentItem(time[2]);
		mToMinuteWheel.setCurrentItem(time[3]);
	}

}
