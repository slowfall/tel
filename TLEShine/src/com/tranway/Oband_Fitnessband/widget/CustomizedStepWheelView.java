package com.tranway.Oband_Fitnessband.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tranway.Oband_Fitnessband.R;

public class CustomizedStepWheelView extends LinearLayout {

	private static final int STRIDE_LENGTH_BASE = 20;

	private WheelView mStrideWheel;
	private WheelView mMinuteWheel;
	private WheelView mUnuseWheel;

	private int defaultStep = 80 - STRIDE_LENGTH_BASE;

	public CustomizedStepWheelView(Context context) {
		super(context);
	}

	public CustomizedStepWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.customized_wheel, this, true);
		initialize();
	}

	private void initialize() {
		mStrideWheel = (WheelView) findViewById(R.id.wheel_left);
		mStrideWheel.setAdapter(new NumericWheelAdapter(20, 200, null));
		mStrideWheel.setCyclic(false);
		mStrideWheel.setCurrentItem(defaultStep);
		mStrideWheel.setLabel("  CM");

		mMinuteWheel = (WheelView) findViewById(R.id.wheel_middle);
		mMinuteWheel.setVisibility(View.GONE);
		// mMinuteWheel.setAdapter(new NumericWheelAdapter(0, 59, null));
		// mMinuteWheel.setCyclic(false);
		// mMinuteWheel.setCurrentItem(default_min);
		// mMinuteWheel.setLabel("分钟");

		mUnuseWheel = (WheelView) findViewById(R.id.wheel_right);
		mUnuseWheel.setVisibility(View.GONE);
	}

	/**
	 * Get the user setting stride
	 * 
	 * @return return length, unit is cm
	 */
	public int getStride() {
		return mStrideWheel.getCurrentItem() + STRIDE_LENGTH_BASE;
	}

	public void setOnScrollLisenter(OnWheelScrollListener mListener) {
		if (mListener != null) {
			mStrideWheel.addScrollingListener(mListener);
		}
	}

	public void setCurrentStride(int stride) {
		if (stride < 0) {
			return;
		}
		mStrideWheel.setCurrentItem(stride - STRIDE_LENGTH_BASE);
	}

}
