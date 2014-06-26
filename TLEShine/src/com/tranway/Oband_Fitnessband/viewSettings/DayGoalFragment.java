package com.tranway.Oband_Fitnessband.viewSettings;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.ActivityInfo;
import com.tranway.Oband_Fitnessband.model.ExerciseUtils;
import com.tranway.Oband_Fitnessband.model.ExerciseUtils.Sport;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.model.Util;
import com.tranway.Oband_Fitnessband.util.UserInfoUtils;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsGoalActivity.OnTitleButtonClickListener;
import com.tranway.Oband_Fitnessband.widget.CustomizedGoalWheelView;
import com.tranway.Oband_Fitnessband.widget.OnWheelScrollListener;
import com.tranway.Oband_Fitnessband.widget.WheelView;
import com.tranway.telshine.database.DBManager;

public class DayGoalFragment extends Fragment implements OnTitleButtonClickListener {

	private CustomizedGoalWheelView mGoalWheel;
	private TextView mWalkTimeTxt, mRunTimeTxt, mSwimTimeTxt, mPointTxt;
	private int goalPoint = 600;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_goal_day, container, false);

		initView(v);
		setUserGoalFromSP();

		return v;
	}

	private void initView(View v) {
		mGoalWheel = (CustomizedGoalWheelView) v.findViewById(R.id.goal_wheel);
		mGoalWheel.setOnScrollLisenter(new OnWheelScrollListener() {

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

		mWalkTimeTxt = (TextView) v.findViewById(R.id.txt_time_walk);
		mRunTimeTxt = (TextView) v.findViewById(R.id.txt_time_run);
		mSwimTimeTxt = (TextView) v.findViewById(R.id.txt_time_swim);
		mPointTxt = (TextView) v.findViewById(R.id.txt_point);
	}

	@Override
	public boolean onTitleButtonClick(int id) {
		boolean ret = true;
		switch (id) {
		case R.id.btn_title_right:
			ret = saveUserGoalSettings();
			break;
		default:
			break;
		}

		return ret;
	}

	private void setUserGoalFromSP() {
		// int userGoal = UserGoalKeeper.readExerciseGoalPoint(getActivity());
		int userGoal = UserInfoKeeper.readUserInfo(getActivity(), UserInfoKeeper.KEY_STEPSTARGET, -1);
		Log.d("DayGoalFragment", "UserGoalKeeper: " + userGoal);
		if (userGoal > 0) {
			goalPoint = userGoal;
		}
		mGoalWheel.setCurrentStep(goalPoint);
		updateAchieveGoalTips(goalPoint);
	}

	private void notifyWheelScroll() {
		goalPoint = mGoalWheel.getGoalStep();
		updateAchieveGoalTips(goalPoint);
	}

	private void updateAchieveGoalTips(int point) {
		if (point < 0) {
			return;
		}
		mPointTxt.setText(point + getString(R.string.activity_step));
		int time = ExerciseUtils.getAchieveGoalTime(point, Sport.WALK);
		mWalkTimeTxt.setText(UserInfoUtils.convertMinToHour(time));
		time = ExerciseUtils.getAchieveGoalTime(point, Sport.RUN);
		mRunTimeTxt.setText(UserInfoUtils.convertMinToHour(time));
		time = ExerciseUtils.getAchieveGoalTime(point, Sport.SWIM);
		mSwimTimeTxt.setText(UserInfoUtils.convertMinToHour(time));
	}

	private boolean saveUserGoalSettings() {
		if (goalPoint == 0) {
			ToastHelper.showToast(R.string.exercise_goal_cannot_null);
			return false;
		}
		long userId = UserInfoKeeper.readUserInfo(getActivity(), UserInfoKeeper.KEY_ID, -1l);
		long utcTime = System.currentTimeMillis() / 1000 / Util.SECONDS_OF_ONE_DAY;
		ActivityInfo info = DBManager.queryActivityInfoByTime(userId, utcTime);
		if (info.getUtcTime() == utcTime) {
			info.setGoal(goalPoint);
			DBManager.addActivityInfo(userId, info);
		}
		return UserInfoKeeper.writeUserInfo(getActivity(), UserInfoKeeper.KEY_STEPSTARGET, goalPoint);
	}

}
