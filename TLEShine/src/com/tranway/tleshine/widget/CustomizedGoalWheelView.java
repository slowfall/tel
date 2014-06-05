package com.tranway.tleshine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tranway.tleshine.R;

public class CustomizedGoalWheelView extends LinearLayout {

	private WheelView mLeftWheel;
	private WheelView mRightWheel;
	private WheelView mUnitWheel;

	private String[] rightStr = { "000", "100", "200", "300", "400", "500", "600", "700", "800", "900" };
	private String[] unit = { "点" };
	private int default_m = 0;
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
		mLeftWheel.setAdapter(new NumericWheelAdapter(0, 5, null));
		mLeftWheel.setCyclic(false);
		mLeftWheel.setCurrentItem(default_m);

		mRightWheel = (WheelView) findViewById(R.id.wheel_middle);
		mRightWheel.setAdapter(new ArrayWheelAdapter<String>(rightStr, rightStr.length));
		mRightWheel.setCyclic(false);
		mRightWheel.setCurrentItem(default_cm);

		mUnitWheel = (WheelView) findViewById(R.id.wheel_right);
		mUnitWheel.setAdapter(new ArrayWheelAdapter<String>(unit, unit.length));
		mUnitWheel.setCyclic(false);
	}

	/**
	 * Get the user setting goal point value
	 * 
	 * @return return high, unit is centimeter
	 */
	public int getGoalPoint() {
		return mLeftWheel.getCurrentItem() * 1000 + mRightWheel.getCurrentItem() * 100;
	}

	public void setOnScrollLisenter(OnWheelScrollListener mListener) {
		if (mListener != null) {
			mLeftWheel.addScrollingListener(mListener);
			mRightWheel.addScrollingListener(mListener);
		}
	}

	public void setCurrentGoal(int point) {
		if (point <= 0) {
			return;
		}
		int left = point / 1000;
		int right = point % 1000 / 100;
		mLeftWheel.setCurrentItem(left);
		mRightWheel.setCurrentItem(right);
	}

}