package com.tranway.tleshine.widget;

import java.text.DecimalFormat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tranway.tleshine.R;

public class CustomizedWeightWheelView extends LinearLayout {

	private String[] weightUnitArray = { "公斤" };
	private WheelView mKgWheel;
	private WheelView mGWheel;
	private WheelView mUnitWheel;

	private String[] gStrings; // centimeter string on WheelView

	public CustomizedWeightWheelView(Context context) {
		super(context);
	}

	public CustomizedWeightWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.customized_wheel, this, true);
		initialize();
	}

	private void initialize() {
		mKgWheel = (WheelView) findViewById(R.id.wheel_left);
		mKgWheel.setAdapter(new NumericWheelAdapter(0, 200, null));
		// meterWheel.setLabel(getString(R.string.number));
		// meterWheel.setCurrentItem(phoneCount);
		mKgWheel.setCyclic(false);

		gStrings = gToKgStrings();
		mGWheel = (WheelView) findViewById(R.id.wheel_middle);
		mGWheel.setAdapter(new ArrayWheelAdapter<String>(gStrings, gStrings.length));
		// centimeterWheel.setLabel(getString(R.string.number));
		// centimeterWheel.setCurrentItem(smsCount);
		mGWheel.setCyclic(false);

		mUnitWheel = (WheelView) findViewById(R.id.wheel_right);
		mUnitWheel
				.setAdapter(new ArrayWheelAdapter<String>(weightUnitArray, weightUnitArray.length));
		// unitWheel.setLabel(getString(R.string.number));
		// unitWheel.setCurrentItem(clockCount);
		mUnitWheel.setCyclic(false);
	}

	/**
	 * Get the user setting height value
	 * 
	 * @return return weight, unit is G
	 */
	public int getWeight() {
		return mKgWheel.getCurrentItem() * 10 + mGWheel.getCurrentItem();
	}

	private String[] gToKgStrings() {
		int[] unit = getGArray();
		DecimalFormat df = new DecimalFormat("#.0");
		String[] str = new String[unit.length];
		for (int i = 0; i < unit.length; ++i) {
			String s = df.format((float) unit[i] / 10);
			str[i] = s.substring(s.lastIndexOf("."), s.length());
		}
		return str;
	}

	private int[] getGArray() {
		int[] unit = new int[10];
		for (int i = 0; i < 10; i++) {
			unit[i] = i;
		}
		return unit;
	}

}
