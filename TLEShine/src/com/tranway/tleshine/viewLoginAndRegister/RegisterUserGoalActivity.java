package com.tranway.tleshine.viewLoginAndRegister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.TextView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ExerciseUtils;
import com.tranway.tleshine.model.ExerciseUtils.Sport;
import com.tranway.tleshine.model.UserGoalKeeper;
import com.tranway.tleshine.util.UserInfoOperation;
import com.tranway.tleshine.viewMainTabs.MainTabsActivity;
import com.tranway.tleshine.widget.ExerciseIntensityView;

public class RegisterUserGoalActivity extends Activity implements OnClickListener {
	private static final String TAG = RegisterUserGoalActivity.class.getSimpleName();

	private TextView mExerciseTxt, mWalkTimeTxt, mRunTimeTxt, mSwimTimeTxt, mPointTxt;
	private int selectIndex = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_user_goal);

		initView();
	}

	private void initView() {
		initTitleView();

		mExerciseTxt = (TextView) findViewById(R.id.txt_exercise_intensity);
		mWalkTimeTxt = (TextView) findViewById(R.id.txt_time_walk);
		mRunTimeTxt = (TextView) findViewById(R.id.txt_time_run);
		mSwimTimeTxt = (TextView) findViewById(R.id.txt_time_swim);
		mPointTxt = (TextView) findViewById(R.id.txt_goal_point);

		final ExerciseIntensityView mView = (ExerciseIntensityView) findViewById(R.id.intensity_view);
		mView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectIndex = arg2;
				mView.setSelectPosition(arg2);
				updateInstensityText(arg2);
			}
		});
		mView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectIndex = arg2;
				mView.setSelectPosition(arg2);
				updateInstensityText(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		mView.setSelectPositon(selectIndex);
		updateInstensityText(selectIndex);
	}

	private void initTitleView() {
		Button mPreBtn = (Button) findViewById(R.id.btn_title_left);
		mPreBtn.setText(R.string.pre_step);
		mPreBtn.setVisibility(View.VISIBLE);
		mPreBtn.setOnClickListener(this);
		Button mNextBtn = (Button) findViewById(R.id.btn_title_right);
		mNextBtn.setText(R.string.next_step);
		mNextBtn.setVisibility(View.VISIBLE);
		mNextBtn.setOnClickListener(this);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.setting_goal);
	}

	private void updateInstensityText(int index) {
		if (index < 0) {
			return;
		}
		int point = 0;
		int resId = -1;
		if (index == 0) {
			resId = R.string.intensity_light;
			point = ExerciseUtils.GOAL_POINT_LIGHT;
		} else if (index == 1) {
			resId = R.string.intensity_moderate;
			point = ExerciseUtils.GOAL_POINT_MODERATE;
		} else {
			resId = R.string.intensity_strenuous;
			point = ExerciseUtils.GOAL_POINT_STRENUOUS;
		}
		if (resId != -1) {
			mExerciseTxt.setText(getResources().getString(resId));
		}
		mPointTxt.setText(String.valueOf(point));

		int time = ExerciseUtils.getAchieveGoalTime(point, Sport.WALK);
		mWalkTimeTxt.setText(UserInfoOperation.convertMinToHour(time));
		time = ExerciseUtils.getAchieveGoalTime(point, Sport.RUN);
		mRunTimeTxt.setText(UserInfoOperation.convertMinToHour(time));
		time = ExerciseUtils.getAchieveGoalTime(point, Sport.SWIM);
		mSwimTimeTxt.setText(UserInfoOperation.convertMinToHour(time));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			onNextButtonClick();
			break;
		}
	}

	private void onNextButtonClick() {
		int point = 0;
		if (selectIndex == 0) {
			point = ExerciseUtils.GOAL_POINT_LIGHT;
		} else if (selectIndex == 1) {
			point = ExerciseUtils.GOAL_POINT_MODERATE;
		} else {
			point = ExerciseUtils.GOAL_POINT_STRENUOUS;
		}
		UserGoalKeeper.writeExerciseGoal(this, point);
		Intent intent = new Intent(this, MainTabsActivity.class);
		startActivity(intent);
		finish();
	}

}
