package com.tranway.tleshine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tranway.tleshine.R;

public class CustomizedTimeWheelView extends LinearLayout {

	private WheelView mHourWheel;
	private WheelView mMinuteWheel;
	private WheelView mUnuseWheel;

	private int default_hour = 8;
	private int default_min = 0;

	public CustomizedTimeWheelView(Context context) {
		super(context);
	}

	public CustomizedTimeWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.customized_wheel, this, true);
		initialize();
	}

	private void initialize() {
		mHourWheel = (WheelView) findViewById(R.id.wheel_left);
		mHourWheel.setAdapter(new NumericWheelAdapter(0, 23, null));
		mHourWheel.setCyclic(false);
		mHourWheel.setCurrentItem(default_hour);
		mHourWheel.setLabel("小时");

		mMinuteWheel = (WheelView) findViewById(R.id.wheel_middle);
		mMinuteWheel.setAdapter(new NumericWheelAdapter(0, 59, null));
		mMinuteWheel.setCyclic(false);
		mMinuteWheel.setCurrentItem(default_min);
		mMinuteWheel.setLabel("分钟");

		mUnuseWheel = (WheelView) findViewById(R.id.wheel_right);
		mUnuseWheel.setVisibility(View.GONE);
	}

	/**
	 * Get the user setting goal sleep time
	 * 
	 * @return return time, unit is minute
	 */
	public int getTime() {
		return mHourWheel.getCurrentItem() * 60 + mMinuteWheel.getCurrentItem();
	}

	public void setOnScrollLisenter(OnWheelScrollListener mListener) {
		if (mListener != null) {
			mHourWheel.addScrollingListener(mListener);
			mMinuteWheel.addScrollingListener(mListener);
		}
	}

	public void setCurrentGoal(int time) {
		if (time <= 0) {
			return;
		}
		int left = time / 60;
		int right = time % 60;
		mHourWheel.setCurrentItem(left);
		mMinuteWheel.setCurrentItem(right);
	}

}
