package com.tranway.tleshine.viewLoginAndRegister;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.UserInfoKeeper;

public class RegisterActivity extends Activity implements OnClickListener {

	private static final String TAG = RegisterActivity.class.getSimpleName();

	private EditText mEmailTxt, mPwdTxt, mConfirmPwdTxt;
	private Button mBackBtn, mNextBtn;

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
		mExsitBtn.setText(R.string.exsit);
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

		if (!checkUserRegisterInfo(email, password, confirmPassword)) {
			return;
		}
		// TODO..
		// assume register success...
		saveUserResgiterInfo();
		Intent intent = new Intent(this, RegisterUserInfoActivity.class);
		startActivity(intent);
	}

	private boolean checkUserRegisterInfo(String email, String pwd, String cPwd) {
		if (!checkEmailAvailable(email)) {
			mEmailTxt.setError(getResources().getString(R.string.email_invalid));
			mEmailTxt.requestFocus();
			return false;
		}
		if (pwd.equals("")) {
			mPwdTxt.setError(getResources().getString(R.string.password_empty));
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
		String regEx = "^([a-z0-9A-Z]+[_-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
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
		if (pwd.equals("")) {
			return false;
		}

		return true;
	}

}
