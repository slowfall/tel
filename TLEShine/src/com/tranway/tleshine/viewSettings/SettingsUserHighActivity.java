package com.tranway.tleshine.viewSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.tranway.tleshine.R;
import com.tranway.tleshine.widget.CustomizedHighWheelView;

public class SettingsUserHighActivity extends Activity {

	public static final int REQUEST_CODE = 0x1001;
	public static final String RESPONSE_NAME_VALUE = "high";

	private CustomizedHighWheelView mHightWheel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set_user_high);

		initView();
	}

	private void initView() {
		mHightWheel = (CustomizedHighWheelView) findViewById(R.id.hight_wheel);
		Button mCompleteBtn = (Button) findViewById(R.id.btn_complete);
		mCompleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent();
				mIntent.putExtra(RESPONSE_NAME_VALUE, mHightWheel.getHigh());
				SettingsUserHighActivity.this.setResult(0, mIntent);
				finish();
			}
		});
	}

}