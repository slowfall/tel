package com.tranway.tleshine.widget;

import java.text.DecimalFormat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tranway.tleshine.R;

public class CustomizedHighWheelView extends LinearLayout {

	private WheelView mMeterWheel;
	private WheelView mCmWheel;
	private WheelView mUnitWheel;

	private String[] cmStrings; // WheelView上显示的厘米部分，如 .65
	private String[] unit = { "米" };
	private int default_m = 1;
	private int default_cm = 7;

	public CustomizedHighWheelView(Context context) {
		super(context);
	}

	public CustomizedHighWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.customized_wheel, this, true);
		initialize();
	}

	private void initialize() {
		mMeterWheel = (WheelView) findViewById(R.id.wheel_left);
		mMeterWheel.setAdapter(new NumericWheelAdapter(0, 2, null));
		mMeterWheel.setCyclic(false);
		mMeterWheel.setCurrentItem(default_m);

		cmStrings = cmToMeterStrings();
		mCmWheel = (WheelView) findViewById(R.id.wheel_middle);
		mCmWheel.setAdapter(new ArrayWheelAdapter<String>(cmStrings, cmStrings.length));
		mCmWheel.setCyclic(false);
		mCmWheel.setCurrentItem(default_cm);

		mUnitWheel = (WheelView) findViewById(R.id.wheel_right);
		mUnitWheel.setAdapter(new ArrayWheelAdapter<String>(unit, unit.length));
		mUnitWheel.setCyclic(false);
	}

	/**
	 * Get the user setting height value
	 * 
	 * @return return high, unit is centimeter
	 */
	public int getHigh() {
		return mMeterWheel.getCurrentItem() * 100 + mCmWheel.getCurrentItem();
	}

	private String[] cmToMeterStrings() {
		int[] unit = getCmArray();
		DecimalFormat df = new DecimalFormat("#.00");
		String[] str = new String[unit.length];
		for (int i = 0; i < unit.length; ++i) {
			String s = df.format((float) unit[i] / 100);
			str[i] = s.substring(s.lastIndexOf("."), s.length());
		}
		return str;
	}

	private int[] getCmArray() {
		int[] unit = new int[100];
		for (int i = 0; i < 100; i++) {
			unit[i] = i;
		}
		return unit;
	}

}
