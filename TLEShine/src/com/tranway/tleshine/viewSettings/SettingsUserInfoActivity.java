package com.tranway.tleshine.viewSettings;

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

public class SettingsUserInfoActivity extends Activity implements OnClickListener {
	private static final String TAG = SettingsUserInfoActivity.class.getSimpleName();

	private TextView mHighTxt, mWeightTxt, mBirthdayTxt, mEmailTxt;
	private SegmentedGroup mSexGroup;
	private RadioButton mMaleRadio, mFemaleRadio;

	private UserInfo userInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_user_info);

		userInfo = UserInfoKeeper.readUserInfo(this);

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
		mEmailTxt = (TextView) findViewById(R.id.txt_email);

		mMaleRadio = (RadioButton) findViewById(R.id.radio_male);
		mFemaleRadio = (RadioButton) findViewById(R.id.radio_female);
		mSexGroup = (SegmentedGroup) findViewById(R.id.group_sex);
		mSexGroup.setTintColor(getResources().getColor(R.color.radio_button_bg_checked_color_gray));
		mSexGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if (arg1 == mFemaleRadio.getId()) {
					userInfo.setSex(UserInfo.SEX_FEMALE);
				} else {
					userInfo.setSex(UserInfo.SEX_MALE);
				}
			}
		});
	}

	private void updateUserInfoUI(UserInfo userInfo) {
		if (userInfo == null) {
			ToastHelper.showToast(R.string.read_user_info_exception);
			return;
		}
		mEmailTxt.setText(userInfo.getEmail());
		try {
			String birth = UserInfoUtils.convertDateToBirthday(userInfo.getBirthday());
			mBirthdayTxt.setText(birth);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
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

	private void initTitleView() {
		Button mPreBtn = (Button) findViewById(R.id.btn_title_left);
		mPreBtn.setText(R.string.pre_step);
		mPreBtn.setVisibility(View.VISIBLE);
		mPreBtn.setOnClickListener(this);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.edit_my_info);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			savedUserInfoToSP();
			finish();
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
			userInfo.setHeight(high);
			Log.d(TAG, "get wheel view high=" + high);
			mHighTxt.setText(UserInfoUtils.convertHighToString(high));
			break;
		case SettingsUserWeightActivity.REQUEST_CODE:
			int weight = data.getIntExtra(SettingsUserWeightActivity.RESPONSE_NAME_VALUE, 0);
			userInfo.setWeight(weight);
			mWeightTxt.setText(UserInfoUtils.convertWeightToString(weight));
			break;
		case SettingsUserBirthdayActivity.REQUEST_CODE:
			long birthday = data.getLongExtra(SettingsUserBirthdayActivity.RESPONSE_NAME_VALUE, 0);
			userInfo.setBirthday(birthday);
			try {
				String text = UserInfoUtils.convertDateToBirthday(birthday);
				mBirthdayTxt.setText(text);
				int age = UserInfoUtils.convertDateToAge(birthday);
				userInfo.setAge(age);
			} catch (ParseException e) {
				e.printStackTrace();
				ToastHelper.showToast(R.string.parse_date_exception);
			}
			break;
		default:
			break;
		}
	}

	private boolean savedUserInfoToSP() {
		if (userInfo == null) {
			return false;
		}

		return UserInfoKeeper.writeUserInfo(this, userInfo);
	}

	private int getUserSelectSex() {
		int sex = UserInfo.SEX_FEMALE;
		if (mSexGroup == null || mMaleRadio == null || mFemaleRadio == null) {
			return sex;
		}

		int check = mSexGroup.getCheckedRadioButtonId();
		if (check == mMaleRadio.getId()) {
			sex = UserInfo.SEX_MALE;
		} else if (check == mFemaleRadio.getId()) {
			sex = UserInfo.SEX_FEMALE;
		}

		return sex;
	}

}
