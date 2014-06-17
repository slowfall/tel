package com.tranway.tleshine.viewLoginAndRegister;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.tleshine.BLEConnectActivity;
import com.tranway.tleshine.R;
import com.tranway.tleshine.model.TLEHttpRequest;
import com.tranway.tleshine.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserGoalKeeper;
import com.tranway.tleshine.model.UserInfo;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.util.UserInfoUtils;
import com.tranway.tleshine.viewMainTabs.MainActivity;
import com.tranway.tleshine.widget.CustomizedTimeWheelView;
import com.tranway.tleshine.widget.OnWheelScrollListener;
import com.tranway.tleshine.widget.WheelView;

public class RegisterUserSleepActivity extends Activity implements OnClickListener {
	private static final String TAG = RegisterUserSleepActivity.class.getSimpleName();

	private static final String CHECK_REGISTER_USER_URL = "/add";

	private TLEHttpRequest httpRequest;
	private CustomizedTimeWheelView mTimeWheel;
	private TextView mTimeTxt;
	private int goalTime = 8 * 60;
	private UserInfo userInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_user_sleep);

		initView();
		setUserSleepTimeFromSP();
		httpRequest = TLEHttpRequest.instance();
	}

	private void initView() {
		initTitleView();

		mTimeWheel = (CustomizedTimeWheelView) findViewById(R.id.time_wheel);
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

		mTimeTxt = (TextView) findViewById(R.id.txt_time);
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
		mTitleTxt.setText(R.string.my_sleep_goal);
	}

	private void setUserSleepTimeFromSP() {
		userInfo = UserInfoKeeper.readUserInfo(this);
		int userGoal = UserGoalKeeper.readSleepGoalTime(this);
		if (userGoal != -1) {
			goalTime = userGoal;
		}
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_title_right:
			onNextButtonClick();
			break;
		case R.id.btn_title_left:
			finish();
			break;
		default:
			break;
		}
	}

	private void onNextButtonClick() {
		if (goalTime <= 0) {
			ToastHelper.showToast(R.string.sleep_goal_cannot_null);
			return;
		}
		UserGoalKeeper.writeSleepGoal(this, goalTime);
		syncUserInfoToServer(userInfo);
	}

	/**
	 * Synchronous user information to the server
	 * 
	 * @param userInfo
	 *            User detail information
	 */
	private void syncUserInfoToServer(UserInfo userInfo) {
		if (userInfo == null) {
			return;
		}
		Map<String, String> params = UserInfoUtils.convertUserInfoToParamsMap(userInfo);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String createDate = simpleDateFormat.format(new Date());
		params.put(UserInfo.SEVER_KEY_CREATE_DATE, createDate);
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				// TODO Auto-generated method stub
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}

			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
					if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
						ToastHelper.showToast(R.string.success_register_user, Toast.LENGTH_LONG);
						Intent intent = new Intent(RegisterUserSleepActivity.this,
								MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(intent);
						finish();
					} else {
						String errorMsg = data.getString(TLEHttpRequest.MSG);
						ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
						Log.e(TAG, "register failed");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, this);
		httpRequest.post(CHECK_REGISTER_USER_URL + "/", params);
	}
}
