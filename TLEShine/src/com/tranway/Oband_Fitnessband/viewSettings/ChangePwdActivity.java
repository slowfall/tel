package com.tranway.Oband_Fitnessband.viewSettings;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserInfo;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;

public class ChangePwdActivity extends Activity implements OnClickListener {

	private EditText mCurPwdTxt, mNewPwdTxt, mConfirmPwdTxt;

	private static final String UPDATE_END_URL = "/update";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_change_pwd);

		initView();
	}

	private void initView() {
		initTitleView();

		mCurPwdTxt = (EditText) findViewById(R.id.cur_password);
		mNewPwdTxt = (EditText) findViewById(R.id.new_password);
		mConfirmPwdTxt = (EditText) findViewById(R.id.confirm_new_password);

		((Button) findViewById(R.id.btn_change)).setOnClickListener(this);
	}

	private void initTitleView() {
		ImageButton leftBtn = (ImageButton) findViewById(R.id.btn_title_icon_left);
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setOnClickListener(this);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.change_pwd);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_change:
			changeBtnClick();
			break;
		case R.id.btn_title_icon_left:
			finish();
			break;
		}
	}

	private void changeBtnClick() {
		String curPwd = mCurPwdTxt.getText().toString();
		String newPwd = mNewPwdTxt.getText().toString();
		String confirmPwd = mConfirmPwdTxt.getText().toString();

		if (TextUtils.isEmpty(curPwd)) {
			mCurPwdTxt.setError(getResources().getString(R.string.cur_password_empty));
			mCurPwdTxt.requestFocus();
			return;
		}

		if (TextUtils.isEmpty(newPwd)) {
			mNewPwdTxt.setError(getResources().getString(R.string.new_password_empty));
			mNewPwdTxt.requestFocus();
			return;
		}

		if (newPwd.length() < 4) {
			mNewPwdTxt.setError(getResources().getString(R.string.error_invalid_password));
			mNewPwdTxt.requestFocus();
			return;
		}

		if (TextUtils.isEmpty(confirmPwd)) {
			mConfirmPwdTxt.setError(getResources().getString(R.string.confirm_password_empty));
			mConfirmPwdTxt.requestFocus();
			return;
		}

		if (!newPwd.equals(confirmPwd)) {
			mConfirmPwdTxt.setError(getResources().getString(R.string.confirm_pwd_not_math));
			mConfirmPwdTxt.requestFocus();
			return;
		}
		
		if (newPwd.equals(curPwd)) {
			mNewPwdTxt.setError(getResources().getString(R.string.new_password_not_diff));
			mNewPwdTxt.requestFocus();
			return;
		}

		changeUserPwd(curPwd, newPwd);
	}

	private void changeUserPwd(String curPwd, String newPwd) {
		String pwd = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_PWD, "");
		if (!pwd.equals(curPwd)) {
			mCurPwdTxt.setError(getResources().getString(R.string.cur_password_error));
			mCurPwdTxt.requestFocus();
			return;
		}

		long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1L);
		syncNewPwdToServer(userId, newPwd);
	}

	/**
	 * Synchronous user goal to the server
	 * 
	 * @param userInfo
	 *            User detail information
	 */
	private void syncNewPwdToServer(long userId, final String newPwd) {
		if (userId == -1 || newPwd == null) {
			return;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put(UserInfo.SEVER_KEY_ID, String.valueOf(userId));
		params.put(UserInfo.SEVER_KEY_PASSWORD, newPwd);
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}

			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
					if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
						ToastHelper.showToast(R.string.change_pwd_success);
						UserInfoKeeper.writeUserInfo(ChangePwdActivity.this, UserInfoKeeper.KEY_PWD, newPwd);
						finish();
					} else {
						String errorMsg = data.getString(TLEHttpRequest.MSG);
						ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, this);
		httpRequest.post(UPDATE_END_URL + "/", params);
	}
}
