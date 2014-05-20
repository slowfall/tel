package com.tranway.tleshine.viewSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.tranway.tleshine.R;
import com.tranway.tleshine.widget.CustomizedBirthdayWheelView;
import com.tranway.tleshine.widget.CustomizedWeightWheelView;

public class SettingsUserBirthdayActivity extends Activity {

	public static final int REQUEST_CODE = 0x1003;
	public static final String RESPONSE_NAME_VALUE = "birthday";

	private CustomizedBirthdayWheelView mBirthdayWheel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set_user_birthday);

		initView();
	}

	private void initView() {
		mBirthdayWheel = (CustomizedBirthdayWheelView) findViewById(R.id.weight_wheel);
		Button mCompleteBtn = (Button) findViewById(R.id.btn_complete);
		mCompleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent();
				// TODO...
				// mIntent.putExtra(RESPONSE_NAME_VALUE, mBirthdayWheel.);
				SettingsUserBirthdayActivity.this.setResult(0, mIntent);
				finish();
			}
		});
	}

}