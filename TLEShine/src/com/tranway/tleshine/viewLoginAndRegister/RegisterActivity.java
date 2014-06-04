package com.tranway.tleshine.viewLoginAndRegister;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.TLEHttpRequest;
import com.tranway.tleshine.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserInfoKeeper;

public class RegisterActivity extends Activity implements OnClickListener {
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private static final String CHECK_EMAIL_URL = "/CheckEmail";

	private EditText mEmailTxt, mPwdTxt, mConfirmPwdTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		initView();
	}

	private void initView() {
		initTitleView();

		mEmailTxt = (EditText) findViewById(R.id.email);
		mPwdTxt = (EditText) findViewById(R.id.password);
		mConfirmPwdTxt = (EditText) findViewById(R.id.confirm_password);

	}

	private void initTitleView() {
		Button mExsitBtn = (Button) findViewById(R.id.btn_title_left);
		mExsitBtn.setText(R.string.pre_step);
		mExsitBtn.setVisibility(View.VISIBLE);
		mExsitBtn.setOnClickListener(this);
		Button mNextBtn = (Button) findViewById(R.id.btn_title_right);
		mNextBtn.setText(R.string.next_step);
		mNextBtn.setOnClickListener(this);
		mNextBtn.setVisibility(View.VISIBLE);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.register);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			nextButtonClick();
			break;
		default:
			break;
		}
	}

	private void nextButtonClick() {
		String email = mEmailTxt.getText().toString();
		String confirmPassword = mConfirmPwdTxt.getText().toString();
		String password = mPwdTxt.getText().toString();

		if (checkUserRegisterInfo(email, password, confirmPassword)) {
			checkEmailToServer(email);
		}
	}

	/**
	 * check user input Email weather is available, goto next Activity if is available, else show
	 * error tips
	 * 
	 * @param email Email address
	 */
	private void checkEmailToServer(String email) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, JSONObject data) {
				if (data.has(TLEHttpRequest.STATUS_CODE)) {
					try {
						int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
						if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
							saveUserResgiterInfo();
							Intent intent = new Intent(RegisterActivity.this,
									RegisterUserInfoActivity.class);
							startActivity(intent);
						} else {
							ToastHelper.showToast(R.string.error_email_used, Toast.LENGTH_LONG);
							Log.e(TAG, "email is not available");
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
		httpRequest.get(CHECK_EMAIL_URL + "/" + email, null);
	}

	private boolean checkUserRegisterInfo(String email, String pwd, String cPwd) {
		if (TextUtils.isEmpty(email)) {
			mEmailTxt.setError(getResources().getString(R.string.email_empty));
			mEmailTxt.requestFocus();
			return false;
		}
		if (!checkEmailAvailable(email)) {
			mEmailTxt.setError(getResources().getString(R.string.email_invalid));
			mEmailTxt.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(pwd)) {
			mPwdTxt.setError(getResources().getString(R.string.password_empty));
			mPwdTxt.requestFocus();
			return false;
		}
		if (pwd.length() < 4) {
			mPwdTxt.setError(getResources().getString(R.string.error_invalid_password));
			mPwdTxt.requestFocus();
			return false;
		}
		if (!checkPasswordAvailable(pwd, cPwd)) {
			mConfirmPwdTxt.setError(getResources().getString(R.string.password_invalid));
			mConfirmPwdTxt.requestFocus();
			return false;
		}

		return true;
	}

	private boolean saveUserResgiterInfo() {
		String password = mPwdTxt.getText().toString();
		String email = mEmailTxt.getText().toString();
		boolean p = UserInfoKeeper.writeUserinfo(this, UserInfoKeeper.KEY_EMAIL, email);
		boolean e = UserInfoKeeper.writeUserinfo(this, UserInfoKeeper.KEY_PWD, password);
		return p && e;
	}

	private boolean checkEmailAvailable(String email) {
		if (email == null) {
			return false;
		}
		// ^([a-z0-9A-Z]+[_-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$
		String regEx = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(email);
		return matcher.matches();
	}

	private boolean checkPasswordAvailable(String pwd, String confirmPwd) {
		if (pwd == null || confirmPwd == null) {
			return false;
		}
		if (!confirmPwd.equals(pwd)) {
			return false;
		}
		if (TextUtils.isEmpty(pwd)) {
			return false;
		}

		return true;
	}

}
