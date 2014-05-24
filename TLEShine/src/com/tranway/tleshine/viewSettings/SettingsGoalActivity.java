package com.tranway.tleshine.viewSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.TextView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.Exercise;
import com.tranway.tleshine.model.Exercise.Intensity;
import com.tranway.tleshine.model.Exercise.Sport;
import com.tranway.tleshine.model.UserExerciseKeeper;
import com.tranway.tleshine.viewMainTabs.MainTabsActivity;
import com.tranway.tleshine.widget.ExerciseIntensityView;

public class SettingsGoalActivity extends Activity implements OnClickListener {
	private static final String TAG = SettingsGoalActivity.class.getSimpleName();

	private TextView mExerciseTxt, mWalkTimeTxt, mRunTimeTxt, mSwimTimeTxt, mPointTxt;

	private Exercise mExercise = new Exercise();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_goal);

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
				mView.setSelectPosition(arg2);
				mExercise.setIntensity(mView.getSelectExerciseIntensity());
				updateInstensityText(mExercise.getIntensity());
			}
		});
		mView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mView.setSelectPosition(arg2);
				mExercise.setIntensity(mView.getSelectExerciseIntensity());
				updateInstensityText(mExercise.getIntensity());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		mView.setSelectPositon(mExercise.getIntensity());
		updateInstensityText(mExercise.getIntensity());
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

	private void updateInstensityText(Intensity intensity) {
		if (intensity == null) {
			return;
		}
		int resId = mExercise.getExerciseIntensityTitle();
		if (resId != -1) {
			mExerciseTxt.setText(getResources().getString(resId));
		}

		int time = mExercise.getExerciseGoalTime(Sport.WALK);
		mWalkTimeTxt.setText(minToHour(time));
		time = mExercise.getExerciseGoalTime(Sport.RUN);
		mRunTimeTxt.setText(minToHour(time));
		time = mExercise.getExerciseGoalTime(Sport.SWIM);
		mSwimTimeTxt.setText(minToHour(time));

		mPointTxt.setText(String.valueOf(mExercise.getExercisePoint()));
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

	private String minToHour(int min) {
		if (min < 60) {
			return min + " 分钟";
		} else {
			float h = (float) min / 60;
			return h + " 小时";
		}
	}

	private void onNextButtonClick() {
		UserExerciseKeeper.writeExerciseGoal(this, mExercise);
		Intent intent = new Intent(this, MainTabsActivity.class);
		startActivity(intent);
		finish();
	}

}
