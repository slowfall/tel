package com.tranway.tleshine.viewLoginAndRegister;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.Toast;

import com.tranway.tleshine.BLEConnectActivity;
import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ExerciseUtils;
import com.tranway.tleshine.model.ExerciseUtils.Sport;
import com.tranway.tleshine.model.TLEHttpRequest;
import com.tranway.tleshine.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserGoalKeeper;
import com.tranway.tleshine.model.UserInfo;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.util.UserInfoUtils;
import com.tranway.tleshine.widget.ExerciseIntensityView;

public class RegisterUserGoalActivity extends Activity implements OnClickListener {
	private static final String TAG = RegisterUserGoalActivity.class.getSimpleName();

	private static final String CHECK_REGISTER_USER_URL = "/add";

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
		mWalkTimeTxt.setText(UserInfoUtils.convertMinToHour(time));
		time = ExerciseUtils.getAchieveGoalTime(point, Sport.RUN);
		mRunTimeTxt.setText(UserInfoUtils.convertMinToHour(time));
		time = ExerciseUtils.getAchieveGoalTime(point, Sport.SWIM);
		mSwimTimeTxt.setText(UserInfoUtils.convertMinToHour(time));
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
		syncUserInfoToServer(UserInfoKeeper.readUserInfo(this));
	}

	/**
	 * Synchronous user information to the server
	 * 
	 * @param userInfo User detail information
	 */
	private void syncUserInfoToServer(UserInfo userInfo) {
		if (userInfo == null) {
			return;
		}
		Map<String, String> params = UserInfoUtils.convertUserInfoToParamsMap(userInfo);

		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, JSONObject data) {
				if (data.has(TLEHttpRequest.STATUS_CODE)) {
					try {
						int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
						if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
							ToastHelper
									.showToast(R.string.success_register_user, Toast.LENGTH_LONG);
							Intent intent = new Intent(RegisterUserGoalActivity.this,
									BLEConnectActivity.class);
							startActivity(intent);
							finish();
						} else {
							// ToastHelper.showToast(R.string.failed_register_user,
							// Toast.LENGTH_LONG);
							String errorMsg = data.getString(TLEHttpRequest.MSG);
							ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
							Log.e(TAG, "register failed");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}
		});

		httpRequest.post(CHECK_REGISTER_USER_URL + "/", params);
	}
}
