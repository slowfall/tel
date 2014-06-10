package com.tranway.tleshine.viewSettings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ExerciseUtils;
import com.tranway.tleshine.model.ExerciseUtils.Sport;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserGoalKeeper;
import com.tranway.tleshine.util.UserInfoUtils;
import com.tranway.tleshine.viewSettings.SettingsGoalActivity.OnTitleButtonClickListener;
import com.tranway.tleshine.widget.CustomizedGoalWheelView;
import com.tranway.tleshine.widget.OnWheelScrollListener;
import com.tranway.tleshine.widget.WheelView;

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
		int userGoal = UserGoalKeeper.readExerciseGoalPoint(getActivity());
		if (userGoal != 0) {
			goalPoint = userGoal;
		}
		mGoalWheel.setCurrentGoal(goalPoint);
		updateAchieveGoalTips(goalPoint);
	}

	private void notifyWheelScroll() {
		goalPoint = mGoalWheel.getGoalPoint();
		updateAchieveGoalTips(goalPoint);
	}

	private void updateAchieveGoalTips(int point) {
		if (point < 0) {
			return;
		}
		mPointTxt.setText(point + " 点");
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
		return UserGoalKeeper.writeExerciseGoal(getActivity(), goalPoint);
	}
}
