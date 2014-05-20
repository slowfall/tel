package com.tranway.tleshine.viewLoginAndRegister;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserInfoKeeper;

public class RegisterActivity extends Activity implements OnClickListener {

	private static final String TAG = RegisterActivity.class.getSimpleName();

	private EditText mEmailTxt, mPwdTxt, mConfirmPwdTxt;
	private Button mBackBtn, mNextBtn;

	private boolean isEmailAvaliable = false;
	private boolean isPasswordAvaliable = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		initView();
	}

	private void initView() {
		mEmailTxt = (EditText) findViewById(R.id.email);
		mEmailTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String email = mEmailTxt.getText().toString();
				if (!checkEmailAvailable(email)) {
					mEmailTxt.setError(getResources().getString(R.string.email_invalid));
					isEmailAvaliable = false;
				} else {
					isEmailAvaliable = true;
				}
				controlNextButton();
			}
		});

		mPwdTxt = (EditText) findViewById(R.id.password);
		mPwdTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String confirmPassword = mConfirmPwdTxt.getText().toString();
				String password = mPwdTxt.getText().toString();
				if (!checkPasswordAvailable(password, confirmPassword)) {
					mConfirmPwdTxt.setError(getResources().getString(R.string.password_invalid));
					isPasswordAvaliable = false;
				} else {
					isPasswordAvaliable = true;
				}
				controlNextButton();
			}
		});

		mConfirmPwdTxt = (EditText) findViewById(R.id.confirm_password);
		mConfirmPwdTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String confirmPassword = mConfirmPwdTxt.getText().toString();
				String password = mPwdTxt.getText().toString();
				if (!checkPasswordAvailable(password, confirmPassword)) {
					mConfirmPwdTxt.setError(getResources().getString(R.string.password_invalid));
					isPasswordAvaliable = false;
				} else {
					isPasswordAvaliable = true;
				}
				controlNextButton();
			}
		});

		mBackBtn = (Button) findViewById(R.id.btn_back);
		mBackBtn.setOnClickListener(this);
		mNextBtn = (Button) findViewById(R.id.btn_next);
		mNextBtn.setOnClickListener(this);
		mNextBtn.setClickable(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_next:
			nextButtonClick();
			break;
		default:
			break;
		}
	}

	private void nextButtonClick() {
		if (isEmailAvaliable && isPasswordAvaliable) {
			// TODO..

			// Assume registration is successful
			String password = mPwdTxt.getText().toString();
			String email = mEmailTxt.getText().toString();
			boolean p = UserInfoKeeper.writeUserinfo(this, UserInfoKeeper.KEY_EMAIL, email);
			boolean e = UserInfoKeeper.writeUserinfo(this, UserInfoKeeper.KEY_EMAIL, password);
			Log.d(TAG, "result p = " + p + "  e = " + e);
			Intent intent = new Intent(this, RegisterUserInfoActivity.class);
			startActivity(intent);
		} else {
			ToastHelper.showToast(R.string.register_info_incomplete);
		}
	}

	private void controlNextButton() {
		if (isEmailAvaliable && isPasswordAvaliable) {
			mNextBtn.setClickable(true);
		} else {
			mNextBtn.setClickable(false);
		}
	}

	private boolean checkEmailAvailable(String email) {
		if (email == null) {
			return false;
		}
		String regEx = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(email);
		return matcher.matches();
	}

	private boolean checkPasswordAvailable(String pwd, String confirmPwd) {
		if (pwd == null || confirmPwd == null || !confirmPwd.equals(pwd)) {
			return false;
		}

		return true;
	}

}
