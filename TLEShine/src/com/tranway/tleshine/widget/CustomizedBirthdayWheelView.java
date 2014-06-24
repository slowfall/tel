package com.tranway.tleshine.widget;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.tranway.tleshine.R;
import com.tranway.tleshine.util.UserInfoUtils;

public class CustomizedBirthdayWheelView extends LinearLayout {

	private static final String TAG = CustomizedBirthdayWheelView.class.getSimpleName();

	private static final int YEAR_MAX = Calendar.getInstance().get(Calendar.YEAR) - 1;
	private static final int YEAR_BASE = 1970;
	private static final int MONTH_BASE = 1;
	private static final int DAY_BASE = 1;
	private WheelView mMonthWheel;
	private WheelView mDayWheel;
	private WheelView mYearWheel;

	private int default_year = 1990 - YEAR_BASE;
	private int default_month = 6;
	private int default_day = 5;

	public CustomizedBirthdayWheelView(Context context) {
		super(context);
	}

	public CustomizedBirthdayWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.customized_wheel, this, true);
		initialize();
	}

	private void initialize() {
		mMonthWheel = (WheelView) findViewById(R.id.wheel_middle);
		mMonthWheel.setAdapter(new NumericWheelAdapter(MONTH_BASE, 12, null));
		mMonthWheel.setCyclic(false);
		mMonthWheel.setLabel(getContext().getString(R.string.month));
		mMonthWheel.setCurrentItem(default_month);
		mMonthWheel.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				onMonthChanged(newValue);
			}
		});

		mDayWheel = (WheelView) findViewById(R.id.wheel_right);
		int maxDay = getMaxDayInMonth(mMonthWheel.getCurrentItem());
		mDayWheel.setAdapter(new NumericWheelAdapter(DAY_BASE, maxDay, null));
		mDayWheel.setCyclic(false);
		mDayWheel.setLabel(getContext().getString(R.string.day));
		mDayWheel.setCurrentItem(default_day);

		mYearWheel = (WheelView) findViewById(R.id.wheel_left);
		mYearWheel.setAdapter(new NumericWheelAdapter(YEAR_BASE, YEAR_MAX, null));
		mYearWheel.setCyclic(false);
		mYearWheel.setLabel(getContext().getString(R.string.year));
		mYearWheel.setCurrentItem(default_year);
	}

	private void onMonthChanged(int month) {
		updateDayWheel(getMaxDayInMonth(month));
	}

	private void updateDayWheel(int maxDay) {
		int curItem = mDayWheel.getCurrentItem();
		mDayWheel.setAdapter(new NumericWheelAdapter(DAY_BASE, maxDay, null));
		Log.d(TAG, "curItem = " + curItem + "  max = " + maxDay);
		int index = (curItem > (maxDay - DAY_BASE)) ? (maxDay - DAY_BASE) : curItem;
		mDayWheel.setCurrentItem(index);
	}

	private int getMaxDayInMonth(int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		return calendar.getActualMaximum(Calendar.DATE);
	}

	/**
	 * get birthday UNIX date
	 * 
	 * @return birthday UNIX date
	 */
	public long getBirthday() {
		int year = mYearWheel.getCurrentItem() + YEAR_BASE;
		int month = mMonthWheel.getCurrentItem() + MONTH_BASE;
		int day = mDayWheel.getCurrentItem() + DAY_BASE;

		return UserInfoUtils.convertBirthdayToUnixDate(year, month, day);
	}
}
