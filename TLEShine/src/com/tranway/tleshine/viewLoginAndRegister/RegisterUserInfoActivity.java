package com.tranway.tleshine.viewLoginAndRegister;

import java.text.DecimalFormat;
import java.text.ParseException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.util.DateUtil;
import com.tranway.tleshine.viewSettings.SettingsUserBirthdayActivity;
import com.tranway.tleshine.viewSettings.SettingsUserHighActivity;
import com.tranway.tleshine.viewSettings.SettingsUserWeightActivity;

public class RegisterUserInfoActivity extends Activity implements OnClickListener {
	private static final String TAG = RegisterUserInfoActivity.class.getSimpleName();

	private TextView mHighTxt, mWeightTxt, mBirthdayTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_user_info);

		initView();
	}

	private void initView() {
		initTitleView();

		mHighTxt = (TextView) findViewById(R.id.txt_high);
		mHighTxt.setOnClickListener(this);
		mWeightTxt = (TextView) findViewById(R.id.txt_weight);
		mWeightTxt.setOnClickListener(this);
		mBirthdayTxt = (TextView) findViewById(R.id.txt_birthday);
		mBirthdayTxt.setOnClickListener(this);
	}

	private void initTitleView() {
		Button mExsitBtn = (Button) findViewById(R.id.btn_left);
		mExsitBtn.setText(R.string.exsit);
		mExsitBtn.setVisibility(View.VISIBLE);
		mExsitBtn.setOnClickListener(this);
		Button mNextBtn = (Button) findViewById(R.id.btn_right);
		mNextBtn.setText(R.string.next);
		mNextBtn.setOnClickListener(this);
		mNextBtn.setVisibility(View.VISIBLE);
		mNextBtn.setClickable(false);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.my_info);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
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
			mHighTxt.setText(highValueToString(high));
			break;
		case SettingsUserWeightActivity.REQUEST_CODE:
			int weight = data.getIntExtra(SettingsUserWeightActivity.RESPONSE_NAME_VALUE, 0);
			mWeightTxt.setText(weightValueToString(weight));
			break;
		case SettingsUserBirthdayActivity.REQUEST_CODE:
			String birthday = data.getStringExtra(SettingsUserBirthdayActivity.RESPONSE_NAME_VALUE);
			try {
				String text = DateUtil.getBirthdayByFormatDate(birthday);
				mBirthdayTxt.setText(text);
			} catch (ParseException e) {
				e.printStackTrace();
				ToastHelper.showToast(R.string.parse_date_exception);
			}
			break;
		default:
			break;
		}
	}

	private String highValueToString(int high) {
		DecimalFormat df = new DecimalFormat("#0.00");
		String s = df.format((float) high / 100) + " 米";
		return s;
	}

	private String weightValueToString(int weight) {
		DecimalFormat df = new DecimalFormat("#0.0");
		String s = df.format((float) weight / 10) + " 公斤";
		return s;
	}
}
