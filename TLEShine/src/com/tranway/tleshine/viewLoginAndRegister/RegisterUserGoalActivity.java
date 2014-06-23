package com.tranway.tleshine.viewLoginAndRegister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ExerciseUtils;
import com.tranway.tleshine.model.ExerciseUtils.Sport;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.util.UserInfoUtils;
import com.tranway.tleshine.widget.CustomizedGoalWheelView;
import com.tranway.tleshine.widget.OnWheelScrollListener;
import com.tranway.tleshine.widget.WheelView;

public class RegisterUserGoalActivity extends Activity implements OnClickListener {
	private static final String TAG = RegisterUserGoalActivity.class.getSimpleName();

	private CustomizedGoalWheelView mGoalWheel;
	private TextView mWalkTimeTxt, mRunTimeTxt, mSwimTimeTxt, mPointTxt;
	private int goalPoint = 600;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_user_goal);

		initView();
		setUserGoalFromSP();
	}

	private void initView() {
		initTitleView();

		mGoalWheel = (CustomizedGoalWheelView) findViewById(R.id.goal_wheel);
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

		mWalkTimeTxt = (TextView) findViewById(R.id.txt_time_walk);
		mRunTimeTxt = (TextView) findViewById(R.id.txt_time_run);
		mSwimTimeTxt = (TextView) findViewById(R.id.txt_time_swim);
		mPointTxt = (TextView) findViewById(R.id.txt_point);
	}

	private void initTitleView() {
		Button mPreBtn = (Button) findViewById(R.id.btn_title_left);
		mPreBtn.setText(R.string.cancel);
		mPreBtn.setVisibility(View.VISIBLE);
		mPreBtn.setOnClickListener(this);
		Button mSaveBtn = (Button) findViewById(R.id.btn_title_right);
		mSaveBtn.setText(R.string.save);
		mSaveBtn.setVisibility(View.VISIBLE);
		mSaveBtn.setOnClickListener(this);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.my_goal);
	}

	private void setUserGoalFromSP() {
		int userGoal = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_STEPSTARGET, 0);
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

	private void updateAchieveGoalTips(int step) {
		if (step < 0) {
			return;
		}
		mPointTxt.setText(step + " 步");
		int time = ExerciseUtils.getAchieveGoalTime(step, Sport.WALK);
		mWalkTimeTxt.setText(UserInfoUtils.convertMinToHour(time));
		time = ExerciseUtils.getAchieveGoalTime(step, Sport.RUN);
		mRunTimeTxt.setText(UserInfoUtils.convertMinToHour(time));
		time = ExerciseUtils.getAchieveGoalTime(step, Sport.SWIM);
		mSwimTimeTxt.setText(UserInfoUtils.convertMinToHour(time));
	}

	private void onNextButtonClick() {
		if (goalPoint == 0) {
			ToastHelper.showToast(R.string.exercise_goal_cannot_null);
			return;
		}
		UserInfoKeeper.writeUserInfo(this, UserInfoKeeper.KEY_STEPSTARGET, goalPoint);

		Intent intent = new Intent(this, RegisterUserSleepActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			onNextButtonClick();
			break;
		default:
			break;
		}
	}
}
