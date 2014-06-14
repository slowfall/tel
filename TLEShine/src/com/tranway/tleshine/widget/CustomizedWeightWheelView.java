package com.tranway.tleshine.widget;

import java.text.DecimalFormat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tranway.tleshine.R;

public class CustomizedWeightWheelView extends LinearLayout {

	private WheelView mKgWheel;
	private WheelView mGWheel;
	private WheelView mUnitWheel;

	private String[] gStrings;
	private static final int KG_BASE = 10;
	private int default_kg = 50;
	private int default_g = 6;

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
		mKgWheel.setAdapter(new NumericWheelAdapter(KG_BASE, 200, null));
		mKgWheel.setCyclic(false);
		mKgWheel.setCurrentItem(default_kg);

		gStrings = gToKgStrings();
		mGWheel = (WheelView) findViewById(R.id.wheel_middle);
		mGWheel.setAdapter(new ArrayWheelAdapter<String>(gStrings, gStrings.length));
		mGWheel.setCyclic(false);
		mGWheel.setCurrentItem(default_g);

		mUnitWheel = (WheelView) findViewById(R.id.wheel_right);
		mUnitWheel.setLabel("公斤");
		mUnitWheel.setCyclic(false);
	}

	/**
	 * Get the user setting weight
	 * 
	 * @return return weight, unit is 0.1KG, such as 510*0.1KG
	 */
	public int getWeight() {
		return (mKgWheel.getCurrentItem() + KG_BASE) * 10 + mGWheel.getCurrentItem();
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
