package com.tranway.Oband_Fitnessband.viewLoginAndRegister;

import java.text.ParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserInfo;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.util.UserInfoUtils;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsUserBirthdayActivity;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsUserHighActivity;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsUserStrideActivity;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsUserWeightActivity;

public class RegisterUserInfoActivity extends Activity implements OnClickListener {
	private static final String TAG = RegisterUserInfoActivity.class.getSimpleName();

	private static final String UPDATE_END_URL = "/update";

	private TextView mNameTxt, mHighTxt, mWeightTxt, mBirthdayTxt, mStrideTxt, mUserNameTxt;
	private RadioGroup mSexGroup;
	private RadioButton mMaleRadio, mFemaleRadio;

	private UserInfo userInfo;
	private boolean isRegister = false;
	private TLEHttpRequest httpRequest = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_user_info);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			isRegister = bundle.getBoolean("isRegister", false);
		}

		userInfo = UserInfoKeeper.readUserInfo(this);

		initView();
		updateUserInfoUI(userInfo);
		httpRequest = TLEHttpRequest.instance();
	}

	private void initView() {
		initTitleView();

		mUserNameTxt = (TextView) findViewById(R.id.txt_username);
		mNameTxt = (TextView) findViewById(R.id.txt_nikename);
		mNameTxt.setOnClickListener(this);
		mNameTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				mUserNameTxt.setText(arg0);
			}
		});
		mHighTxt = (TextView) findViewById(R.id.txt_height);
		mHighTxt.setOnClickListener(this);
		mWeightTxt = (TextView) findViewById(R.id.txt_weight);
		mWeightTxt.setOnClickListener(this);
		mBirthdayTxt = (TextView) findViewById(R.id.txt_birthday);
		mBirthdayTxt.setOnClickListener(this);
		mStrideTxt = (TextView) findViewById(R.id.txt_step);
		mStrideTxt.setOnClickListener(this);

		mMaleRadio = (RadioButton) findViewById(R.id.radio_male);
		mFemaleRadio = (RadioButton) findViewById(R.id.radio_female);
		mSexGroup = (RadioGroup) findViewById(R.id.group_sex);
		mSexGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if (arg1 == R.id.radio_female) {
					userInfo.setSex(UserInfo.SEX_FEMALE);
				} else {
					userInfo.setSex(UserInfo.SEX_MALE);
				}
			}
		});
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
		mTitleTxt.setText(R.string.my_info);
	}

	private void updateUserInfoUI(UserInfo userInfo) {
		if (userInfo == null) {
			return;
		}
		if (!TextUtils.isEmpty(userInfo.getName())) {
			mNameTxt.setText(userInfo.getName());
		}
		try {
			if (userInfo.getBirthday() >= 0) {
				String birth = UserInfoUtils.convertDateToBirthday(userInfo.getBirthday());
				mBirthdayTxt.setText(birth);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (userInfo.getHeight() >= 0) {
			mHighTxt.setText(UserInfoUtils.convertHighToString(userInfo.getHeight()));
		}
		if (userInfo.getWeight() >= 0) {
			mWeightTxt.setText(UserInfoUtils.convertWeightToString(userInfo.getWeight()));
		}
		if (userInfo.getStride() >= 0) {
			mStrideTxt.setText(userInfo.getStride() + " CM");
		}
		Log.d(TAG, "sex: " + userInfo.getSex());
		if (userInfo.getSex() == UserInfo.SEX_MALE) {
			mMaleRadio.setChecked(true);
			mFemaleRadio.setChecked(false);
		} else {
			mFemaleRadio.setChecked(true);
			mMaleRadio.setChecked(false);
		}
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
		case R.id.txt_height:
			Intent intent1 = new Intent(this, SettingsUserHighActivity.class);
			startActivityForResult(intent1, SettingsUserHighActivity.REQUEST_CODE);
			break;
		case R.id.txt_weight:
			Intent intent2 = new Intent(this, SettingsUserWeightActivity.class);
			startActivityForResult(intent2, SettingsUserWeightActivity.REQUEST_CODE);
			break;
		case R.id.txt_birthday:
			Intent intent3 = new Intent(this, SettingsUserBirthdayActivity.class);
			startActivityForResult(intent3, SettingsUserBirthdayActivity.REQUEST_CODE);
			break;
		case R.id.txt_step:
			Intent intent4 = new Intent(this, SettingsUserStrideActivity.class);
			startActivityForResult(intent4, SettingsUserStrideActivity.REQUEST_CODE);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		switch (requestCode) {
		case SettingsUserHighActivity.REQUEST_CODE:
			int high = data.getIntExtra(SettingsUserHighActivity.RESPONSE_NAME_VALUE, -1);
			Log.d(TAG, "get wheel view high=" + high);
			userInfo.setHeight(high);
			mHighTxt.setText(UserInfoUtils.convertHighToString(high));
			break;
		case SettingsUserWeightActivity.REQUEST_CODE:
			int weight = data.getIntExtra(SettingsUserWeightActivity.RESPONSE_NAME_VALUE, -1);
			userInfo.setWeight(weight);
			mWeightTxt.setText(UserInfoUtils.convertWeightToString(weight));
			break;
		case SettingsUserBirthdayActivity.REQUEST_CODE:
			long time = data.getLongExtra(SettingsUserBirthdayActivity.RESPONSE_NAME_VALUE, -1L);
			userInfo.setBirthday(time);
			try {
				String text = UserInfoUtils.convertDateToBirthday(time);
				mBirthdayTxt.setText(text);
				int age = UserInfoUtils.convertDateToAge(time);
				userInfo.setAge(age);
			} catch (ParseException e) {
				e.printStackTrace();
				ToastHelper.showToast(R.string.parse_date_exception);
			}
			break;
		case SettingsUserStrideActivity.REQUEST_CODE:
			int stride = data.getIntExtra(SettingsUserStrideActivity.RESPONSE_NAME_VALUE, -1);
			userInfo.setStride(stride);
			if (stride >= 0) {
				mStrideTxt.setText(stride + " CM");
			}
			break;
		default:
			break;
		}
	}

	private void onNextButtonClick() {
		if (userInfo == null) {
			return;
		}
		String name = mNameTxt.getText().toString();
		if (TextUtils.isEmpty(name)) {
			ToastHelper.showToast(R.string.prompt_name);
			return;
		} else {
			if (name.length() < 4 || name.length() > 20) {
				ToastHelper.showToast(getResources().getString(R.string.name_length_invalid));
				return;
			}
			if (!checkNameAvailable(name)) {
				ToastHelper.showToast(getResources().getString(R.string.name_invalid));
				return;
			}
			userInfo.setName(name);
		}
		if (userInfo.getBirthday() <= 0) {
			ToastHelper.showToast(R.string.prompt_birthday);
			return;
		} else if (userInfo.getHeight() <= 0) {
			ToastHelper.showToast(R.string.prompt_high);
			return;
		} else if (userInfo.getWeight() <= 0) {
			ToastHelper.showToast(R.string.prompt_weight);
			return;
		} else if (userInfo.getStride() <= 0) {
			ToastHelper.showToast(R.string.prompt_stride);
			return;
		} else {
			if (isRegister) {
				UserInfoKeeper.writeUserInfo(this, userInfo);
				Intent intent = new Intent(this, RegisterUserGoalActivity.class);
				startActivity(intent);
			} else {
				syncUserInfoToServer(userInfo);
			}
		}
	}

	/**
	 * Synchronous user information to the server
	 * 
	 * @param userInfo
	 *            User detail information
	 */
	private void syncUserInfoToServer(final UserInfo userInfo) {
		if (userInfo == null) {
			return;
		}
		Map<String, String> params = UserInfoUtils.convertUserInfoToParamsMap(userInfo, true);
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
						UserInfoKeeper.writeUserInfo(RegisterUserInfoActivity.this, userInfo);
						if (!isRegister) {
							ShowGotoSettingsDialog();
						}
					} else {
						String errorMsg = data.getString(TLEHttpRequest.MSG);
						ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
						Log.e(TAG, "sync userinfo failed");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, this);
		httpRequest.post(UPDATE_END_URL + "/", params);
	}

	private boolean checkNameAvailable(String name) {
		if (name == null) {
			return false;
		}
		String regEx = "^[\\w\u3E00-\u9FA5]+$";
		// String regEx = "^[0-9a-zA-Z_\u3E00-\u9FA5]+$";
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(name);
		return matcher.matches();
	}
	
	private void ShowGotoSettingsDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_confirm, null);
		final Dialog dialog = new Dialog(this, R.style.DialogTheme);
		TextView title = (TextView) dialogView.findViewById(R.id.layout_content);
		title.setText(getResources().getString(R.string.go_to_settings_dialog_tips));
		dialog.setContentView(dialogView);
		Button positiveBtn = (Button) dialogView.findViewById(R.id.positive);
		positiveBtn.setText(R.string.positive);
		positiveBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_OK);
				dialog.dismiss();
				finish();
			}
		});
		Button negativeBtn = (Button) dialogView.findViewById(R.id.negative);
		negativeBtn.setText(R.string.negative);
		negativeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				dialog.dismiss();
				finish();
			}
		});
		dialog.show();
	}

}