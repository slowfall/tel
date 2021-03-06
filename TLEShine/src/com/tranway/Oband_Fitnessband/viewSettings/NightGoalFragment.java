package com.tranway.Oband_Fitnessband.viewSettings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserGoalKeeper;
import com.tranway.Oband_Fitnessband.util.UserInfoUtils;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsGoalActivity.OnTitleButtonClickListener;
import com.tranway.Oband_Fitnessband.widget.CustomizedSleepWheelView;
import com.tranway.Oband_Fitnessband.widget.OnWheelScrollListener;
import com.tranway.Oband_Fitnessband.widget.WheelView;

public class NightGoalFragment extends Fragment implements OnTitleButtonClickListener {

	private CustomizedSleepWheelView mTimeWheel;
	private TextView mTimeTxt;
	private int goalTime = 0;
	private String goalRange = "00:00-00:00";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_goal_night, container, false);

		initView(v);
		setUserSleepTimeFromSP();

		return v;
	}

	private void initView(View v) {
		mTimeWheel = (CustomizedSleepWheelView) v.findViewById(R.id.time_wheel);
		mTimeWheel.setOnScrollLisenter(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				notifyWheelScroll();
			}
		});

		mTimeTxt = (TextView) v.findViewById(R.id.txt_time);
	}

	@Override
	public boolean onTitleButtonClick(int id) {
		boolean ret = true;
		switch (id) {
		case R.id.btn_title_right:
			ret = saveUserSleepTimeSettings();
			break;
		default:
			break;
		}

		return ret;
	}

	private boolean saveUserSleepTimeSettings() {
		if (goalTime == 0) {
			ToastHelper.showToast(R.string.sleep_goal_cannot_null);
			return false;
		}

		UserGoalKeeper.writeSleepGoal(getActivity(), goalTime);
		UserGoalKeeper.writeSleepGoalRange(getActivity(), goalRange);

		return true;
	}

	private void setUserSleepTimeFromSP() {
		int userGoal = UserGoalKeeper.readSleepGoalTime(getActivity());
		String range = UserGoalKeeper.readSleepGoalTimeRange(getActivity());
		if (userGoal != -1 && range != null) {
			goalTime = userGoal;
			goalRange = range;
		}
		mTimeWheel.setCurrentGoalRange(goalRange);
		updateAchieveGoalTips(goalTime);
	}

	private void notifyWheelScroll() {
		goalRange = mTimeWheel.getTime();
		goalTime = UserInfoUtils.convertTimeRangeToTime(goalRange);
		updateAchieveGoalTips(goalTime);
	}

	private void updateAchieveGoalTips(int time) {
		if (time < 0) {
			return;
		}
		int hour = time / 60;
		int min = time % 60;
		mTimeTxt.setText(hour + getString(R.string.hours) + min + getString(R.string.minutes));
	}
}
