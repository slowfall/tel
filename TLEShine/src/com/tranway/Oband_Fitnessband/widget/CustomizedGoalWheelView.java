package com.tranway.Oband_Fitnessband.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tranway.Oband_Fitnessband.R;

public class CustomizedGoalWheelView extends LinearLayout {

	private WheelView mLeftWheel;
	private WheelView mRightWheel;
	private WheelView mUnitWheel;

	private String[] rightStr = { "000", "100", "200", "300", "400", "500", "600", "700", "800", "900" };
	private int default_m = 5;
	private int default_cm = 6;

	public CustomizedGoalWheelView(Context context) {
		super(context);
	}

	public CustomizedGoalWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.customized_wheel, this, true);
		initialize();
	}

	private void initialize() {
		mLeftWheel = (WheelView) findViewById(R.id.wheel_left);
		mLeftWheel.setAdapter(new NumericWheelAdapter(0, 99, null));
		mLeftWheel.setCyclic(false);
		mLeftWheel.setCurrentItem(default_m);

		mRightWheel = (WheelView) findViewById(R.id.wheel_middle);
		mRightWheel.setAdapter(new ArrayWheelAdapter<String>(rightStr, rightStr.length));
		mRightWheel.setCyclic(false);
		mRightWheel.setCurrentItem(default_cm);

		mUnitWheel = (WheelView) findViewById(R.id.wheel_right);
		mUnitWheel.setLabel(getContext().getString(R.string.activity_step));
		mUnitWheel.setCyclic(false);
	}

	/**
	 * Get the user setting goal point value
	 * 
	 * @return return high, unit is centimeter
	 */
	public int getGoalStep() {
		return mLeftWheel.getCurrentItem() * 1000 + mRightWheel.getCurrentItem() * 100;
	}

	public void setOnScrollLisenter(OnWheelScrollListener mListener) {
		if (mListener != null) {
			mLeftWheel.addScrollingListener(mListener);
			mRightWheel.addScrollingListener(mListener);
		}
	}

	public void setCurrentStep(int step) {
		if (step <= 0) {
			return;
		}
		int left = step / 1000;
		int right = step % 1000 / 100;
		mLeftWheel.setCurrentItem(left);
		mRightWheel.setCurrentItem(right);
	}

}
