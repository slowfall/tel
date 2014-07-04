package com.tranway.Oband_Fitnessband.viewSettings;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserInfo;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.util.UserInfoUtils;

public class SettingsNightGoalActivity extends Activity implements OnClickListener {
	private NightGoalFragment nightFragment;
	private OnTitleButtonClickListener mListener;

	private static final String UPDATE_END_URL = "/update";
	private TLEHttpRequest httpRequest = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_night_goal);

		initView();
		httpRequest = TLEHttpRequest.instance();
	}

	private void initView() {
		initTitleView();
		showNightFragment();
	}

	private void initTitleView() {
		Button mExsitBtn = (Button) findViewById(R.id.btn_title_left);
		mExsitBtn.setText(R.string.cancel);
		mExsitBtn.setVisibility(View.VISIBLE);
		mExsitBtn.setOnClickListener(this);
		Button mNextBtn = (Button) findViewById(R.id.btn_title_right);
		mNextBtn.setText(R.string.save);
		mNextBtn.setOnClickListener(this);
		mNextBtn.setVisibility(View.VISIBLE);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.my_sleep_goal);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			if (mListener == null || mListener.onTitleButtonClick(R.id.btn_title_right)) {
				syncUserInfoToServer();
			}
			break;
		}
	}

	private void showNightFragment() {
		if (nightFragment == null) {
			nightFragment = new NightGoalFragment();
		}
		if (nightFragment instanceof OnTitleButtonClickListener) {
			mListener = (OnTitleButtonClickListener) nightFragment;
		}
		getFragmentManager().beginTransaction().replace(R.id.layout_fragment, nightFragment)
				.commit();
	}

	/**
	 * Synchronous user goal to the server
	 * 
	 * @param userInfo
	 *            User detail information
	 */
	private void syncUserInfoToServer() {
		UserInfo userInfo = UserInfoKeeper.readUserInfo(this);
		if (userInfo == null) {
			return;
		}
		Map<String, String> params = UserInfoUtils.convertUserInfoToParamsMap(userInfo, false);
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
						ToastHelper.showToast(R.string.success_saved);
						finish();
					} else {
						String errorMsg = data.getString(TLEHttpRequest.MSG);
						ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
						Log.e("DayGoalFragment", "sync userinfo failed");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, this);
		httpRequest.post(UPDATE_END_URL + "/", params);
	}

	public interface OnTitleButtonClickListener {
		public boolean onTitleButtonClick(int id);
	}
}
