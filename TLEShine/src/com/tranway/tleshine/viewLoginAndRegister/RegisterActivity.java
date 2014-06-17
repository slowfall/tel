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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.TLEHttpRequest;
import com.tranway.tleshine.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserInfo;
import com.tranway.tleshine.model.UserInfoKeeper;

public class RegisterActivity extends Activity implements OnClickListener {
	private static final String TAG = RegisterActivity.class.getSimpleName();
	private static final String CHECK_EMAIL_END_URL = "/CheckEmail";
	private static final String GET_GENCODE_END_URL = "/GenCode";
	private static final String CHECK_GENCODE_END_URL = "/CheckCode";

	private EditText mEmailTxt, mPwdTxt, mPhoneTxt, mCodeTxt;
	private TextView mRequestTxt;

	private String gencodeId = null;

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
		mPhoneTxt = (EditText) findViewById(R.id.phone);
		mCodeTxt = (EditText) findViewById(R.id.identify_code);
		mRequestTxt = (TextView) findViewById(R.id.request_identify);
		mRequestTxt.setOnClickListener(this);
		mPwdTxt = (EditText) findViewById(R.id.password);

		TextView mRequestTxt = (TextView) findViewById(R.id.request_identify);
		mRequestTxt.setOnClickListener(this);

	}

	private void initTitleView() {
		ImageButton mPreBtn = (ImageButton) findViewById(R.id.btn_title_icon_left);
		mPreBtn.setVisibility(View.VISIBLE);
		mPreBtn.setOnClickListener(this);
		Button mNextBtn = (Button) findViewById(R.id.btn_next);
		mNextBtn.setText(R.string.next_step);
		mNextBtn.setOnClickListener(this);
		mNextBtn.setVisibility(View.VISIBLE);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.register);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_icon_left:
			finish();
			break;
		case R.id.btn_next:
			nextButtonClick();
			break;
		case R.id.request_identify:
			requestGenCodeFromServer();
			break;
		default:
			break;
		}
	}

	private void requestGenCodeFromServer() {
		String phone = mPhoneTxt.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			mPhoneTxt.setError(getResources().getString(R.string.phone_empty));
			mPhoneTxt.requestFocus();
			return;
		}
		if (!checkPhoneAvailable(phone)) {
			mPhoneTxt.setError(getResources().getString(R.string.phone_invalid));
			mPhoneTxt.requestFocus();
			return;
		}

		getGenCodeFromServer(phone);
	}

	private void nextButtonClick() {
		String email = mEmailTxt.getText().toString();
		String password = mPwdTxt.getText().toString();
		String code = mCodeTxt.getText().toString();

		if (checkUserRegisterInfo(email, password, code)) {
			checkEmailToServer(email, code);
		}
	}

	/**
	 * check user input Email weather is available, goto next Activity if is
	 * available, else show error tips
	 * 
	 * @param email
	 *            Email address
	 */
	private void checkEmailToServer(String email, final String code) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					if (data.has(TLEHttpRequest.STATUS_CODE)) {
						int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
						if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
							checkGenCodeToServer(code);
						} else {
							ToastHelper.showToast(R.string.error_email_used, Toast.LENGTH_LONG);
							Log.e(TAG, "email is not available");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}
		}, this);
		httpRequest.get(CHECK_EMAIL_END_URL + "/" + email, null);
	}

	/**
	 * check GenCode to server
	 * 
	 * @param code
	 *            GenCode
	 */
	private void checkGenCodeToServer(String code) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					if (data.has(TLEHttpRequest.STATUS_CODE)) {
						int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
						if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
							saveUserResgiterInfo();
							Intent intent = new Intent();
							Bundle bundle = new Bundle();
							bundle.putBoolean("isRegister", true);
							intent.setClass(RegisterActivity.this, RegisterUserInfoActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						} else {
							ToastHelper.showToast(R.string.gencode_invalid, Toast.LENGTH_LONG);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}
		}, this);
		httpRequest.get(CHECK_GENCODE_END_URL + "/" + gencodeId + "?code=" + code, null);
	}

	/**
	 * get gencode from server by phone number
	 * 
	 * @param email
	 *            GenCode
	 */
	private void getGenCodeFromServer(String phone) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					if (data.has(TLEHttpRequest.STATUS_CODE)) {
						int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
						if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
							ToastHelper.showToast(R.string.get_gen_code_success, Toast.LENGTH_LONG);
							Log.d(TAG, "get gencode: " + data.toString());
							gencodeId = data.getString(UserInfo.SEVER_KEY_ID);
						} else {
							ToastHelper.showToast(R.string.get_gen_code_failed, Toast.LENGTH_LONG);
							Log.e(TAG, "email is not available");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}
		}, this);
		httpRequest.get(GET_GENCODE_END_URL + "/" + phone, null);
	}

	private boolean checkUserRegisterInfo(String email, String pwd, String code) {
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
		if (TextUtils.isEmpty(code)) {
			mCodeTxt.setError(getResources().getString(R.string.gencode_empty));
			mCodeTxt.requestFocus();
			return false;
		}

		return true;
	}

	private boolean saveUserResgiterInfo() {
		String password = mPwdTxt.getText().toString();
		String email = mEmailTxt.getText().toString();
		boolean p = UserInfoKeeper.writeUserInfo(this, UserInfoKeeper.KEY_EMAIL, email);
		boolean e = UserInfoKeeper.writeUserInfo(this, UserInfoKeeper.KEY_PWD, password);
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

	private boolean checkPhoneAvailable(String phone) {
		if (phone == null) {
			return false;
		}
		String regEx = "^\\d{11}$";
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(phone);
		return matcher.matches();
	}

}
