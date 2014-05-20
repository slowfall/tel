package com.tranway.tleshine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tranway.tleshine.R;

public class CustomizedBirthdayWheelView extends LinearLayout {

	private static final int YEAR_BASE = 1900;
	private String[] monthStrings = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };
	private WheelView mMonthWheel;
	private WheelView mDayWheel;
	private WheelView mYearWheel;

	public CustomizedBirthdayWheelView(Context context) {
		super(context);
	}

	public CustomizedBirthdayWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.customized_wheel, this, true);
		initialize();
	}

	private void initialize() {
		mMonthWheel = (WheelView) findViewById(R.id.wheel_left);
		mDayWheel.setAdapter(new ArrayWheelAdapter<String>(monthStrings, monthStrings.length));
		// meterWheel.setLabel(getString(R.string.number));
		// meterWheel.setCurrentItem(phoneCount);
		mMonthWheel.setCyclic(false);

		mDayWheel = (WheelView) findViewById(R.id.wheel_middle);
		mMonthWheel.setAdapter(new NumericWheelAdapter(1, 30, null));
		// centimeterWheel.setLabel(getString(R.string.number));
		// centimeterWheel.setCurrentItem(smsCount);
		mDayWheel.setCyclic(false);

		mYearWheel = (WheelView) findViewById(R.id.wheel_right);
		mMonthWheel.setAdapter(new NumericWheelAdapter(1900, 2020, null));
		// unitWheel.setLabel(getString(R.string.number));
		// unitWheel.setCurrentItem(clockCount);
		mYearWheel.setCyclic(false);
	}

	// /**
	// * Get the user setting birthday value
	// *
	// * @return return birthday, such as 19920818
	// */
	// public Date getBirthday() {
	// // TODO...
	// }
	//
	// public int getAge() {
	// // TODO...
	// }

}
