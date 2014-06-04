package com.tranway.tleshine.viewLoginAndRegister;

import java.text.ParseException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.android.segmented.SegmentedGroup;
import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserInfo;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.util.UserInfoUtils;
import com.tranway.tleshine.viewSettings.SettingsUserBirthdayActivity;
import com.tranway.tleshine.viewSettings.SettingsUserHighActivity;
import com.tranway.tleshine.viewSettings.SettingsUserWeightActivity;

public class RegisterUserInfoActivity extends Activity implements OnClickListener {
	private static final String TAG = RegisterUserInfoActivity.class.getSimpleName();

	private TextView mHighTxt, mWeightTxt, mBirthdayTxt;
	private SegmentedGroup mSexGroup;
	private RadioButton mMaleRadio, mFemaleRadio;

	private UserInfo userInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_user_info);

		userInfo = UserInfoKeeper.readUserInfo(this);
		userInfo.setSex(UserInfo.SEX_MALE); // set default sex

		initView();
		updateUserInfoUI(userInfo);
	}

	private void initView() {
		initTitleView();

		mHighTxt = (TextView) findViewById(R.id.txt_high);
		mHighTxt.setOnClickListener(this);
		mWeightTxt = (TextView) findViewById(R.id.txt_weight);
		mWeightTxt.setOnClickListener(this);
		mBirthdayTxt = (TextView) findViewById(R.id.txt_birthday);
		mBirthdayTxt.setOnClickListener(this);

		mMaleRadio = (RadioButton) findViewById(R.id.radio_male);
		mFemaleRadio = (RadioButton) findViewById(R.id.radio_female);
		mSexGroup = (SegmentedGroup) findViewById(R.id.group_sex);
		mSexGroup.setTintColor(getResources().getColor(R.color.radio_button_bg_checked_color_gray));
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
		mExsitBtn.setText(R.string.pre_step);
		mExsitBtn.setVisibility(View.VISIBLE);
		mExsitBtn.setOnClickListener(this);
		Button mNextBtn = (Button) findViewById(R.id.btn_title_right);
		mNextBtn.setText(R.string.next_step);
		mNextBtn.setOnClickListener(this);
		mNextBtn.setVisibility(View.VISIBLE);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.my_info);
	}

	private void updateUserInfoUI(UserInfo userInfo) {
		if (userInfo == null || userInfo.getBirthday() == 0 || userInfo.getHeight() == 0
				|| userInfo.getWeight() == 0) {
			return;
		}
		try {
			String birth = UserInfoUtils.convertDateToBirthday(userInfo.getBirthday());
			mBirthdayTxt.setText(birth);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		mHighTxt.setText(UserInfoUtils.convertHighToString(userInfo.getHeight()));
		mWeightTxt.setText(UserInfoUtils.convertWeightToString(userInfo.getWeight()));
		if (userInfo.getSex() == UserInfo.SEX_FEMALE) {
			mFemaleRadio.setChecked(true);
			mMaleRadio.setChecked(false);
		} else {
			mMaleRadio.setChecked(true);
			mFemaleRadio.setChecked(false);
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
		case R.id.txt_high:
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
			int high = data.getIntExtra(SettingsUserHighActivity.RESPONSE_NAME_VALUE, 0);
			Log.d(TAG, "get wheel view high=" + high);
			userInfo.setHeight(high);
			mHighTxt.setText(UserInfoUtils.convertHighToString(high));
			break;
		case SettingsUserWeightActivity.REQUEST_CODE:
			int weight = data.getIntExtra(SettingsUserWeightActivity.RESPONSE_NAME_VALUE, 0);
			userInfo.setWeight(weight);
			mWeightTxt.setText(UserInfoUtils.convertWeightToString(weight));
			break;
		case SettingsUserBirthdayActivity.REQUEST_CODE:
			long time = data.getLongExtra(SettingsUserBirthdayActivity.RESPONSE_NAME_VALUE, 0L);
			userInfo.setBirthday(time);
			try {
				String text = UserInfoUtils.convertDateToBirthday(time);
				mBirthdayTxt.setText(text);
				int age = UserInfoUtils.convertDateToAge(time);
				userInfo.setAge(age);
				Log.d(TAG, "-------dirthday time: " + time + "; age: " + age);
			} catch (ParseException e) {
				e.printStackTrace();
				ToastHelper.showToast(R.string.parse_date_exception);
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
		if (userInfo.getBirthday() == 0) {
			ToastHelper.showToast(R.string.prompt_birthday);
			return;
		} else if (userInfo.getHeight() == 0) {
			ToastHelper.showToast(R.string.prompt_high);
			return;
		} else if (userInfo.getWeight() == 0) {
			ToastHelper.showToast(R.string.prompt_weight);
			return;
		} else {
			UserInfoKeeper.writeUserInfo(this, userInfo);
			Intent intent = new Intent(this, RegisterUserGoalActivity.class);
			startActivity(intent);
		}
	}

	// private int getUserSelectSex() {
	// int sex = UserInfo.SEX_FEMALE;
	// if (mSexGroup == null || mMaleRadio == null || mFemaleRadio == null) {
	// return sex;
	// }
	//
	// int check = mSexGroup.getCheckedRadioButtonId();
	// if (check == mMaleRadio.getId()) {
	// sex = UserInfo.SEX_MALE;
	// } else if (check == mFemaleRadio.getId()) {
	// sex = UserInfo.SEX_FEMALE;
	// }
	//
	// return sex;
	// }

}
