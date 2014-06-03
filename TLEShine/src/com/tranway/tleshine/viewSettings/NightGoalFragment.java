package com.tranway.tleshine.viewSettings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserGoalKeeper;
import com.tranway.tleshine.viewSettings.SettingsGoalActivity.OnTitleButtonClickListener;
import com.tranway.tleshine.widget.CustomizedTimeWheelView;
import com.tranway.tleshine.widget.OnWheelScrollListener;
import com.tranway.tleshine.widget.WheelView;

public class NightGoalFragment extends Fragment implements OnTitleButtonClickListener {

	private CustomizedTimeWheelView mTimeWheel;
	private TextView mTimeTxt;
	private int goalTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_goal_night, container, false);

		initView(v);
		setUserSleepTimeFromSP();

		return v;
	}

	private void initView(View v) {
		mTimeWheel = (CustomizedTimeWheelView) v.findViewById(R.id.time_wheel);
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
		return UserGoalKeeper.writeSleepGoal(getActivity(), goalTime);
	}

	private void setUserSleepTimeFromSP() {
		goalTime = UserGoalKeeper.readSleepGoalTime(getActivity());
		mTimeWheel.setCurrentGoal(goalTime);
		updateAchieveGoalTips(goalTime);
	}

	private void notifyWheelScroll() {
		goalTime = mTimeWheel.getTime();
		updateAchieveGoalTips(goalTime);
	}

	private void updateAchieveGoalTips(int time) {
		if (time < 0) {
			return;
		}
		int hour = time / 60;
		int min = time % 60;
		mTimeTxt.setText(hour + "小时" + min + "分钟");
	}
}
