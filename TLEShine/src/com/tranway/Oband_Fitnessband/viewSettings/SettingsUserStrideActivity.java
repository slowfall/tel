package com.tranway.Oband_Fitnessband.viewSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.widget.CustomizedStepWheelView;

/**
 * SettingsUserHighActivity is a custom WheelView, used to set the height, the returned results is
 * in units of centimeter, through startActivityForResult to call the Activity, and through
 * REQUEST_CODE and RESPONSE_NAME_VALUE to get results.
 * 
 * @author shz
 * 
 */
public class SettingsUserStrideActivity extends Activity {

	public static final int REQUEST_CODE = 0x1004;
	public static final String RESPONSE_NAME_VALUE = "stride";

	private CustomizedStepWheelView mStrideWheel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_set_user_stride);

		initView();
	}

	private void initView() {
		mStrideWheel = (CustomizedStepWheelView) findViewById(R.id.step_wheel);
		Button mCompleteBtn = (Button) findViewById(R.id.btn_complete);
		mCompleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent();
				mIntent.putExtra(RESPONSE_NAME_VALUE, mStrideWheel.getStride());
				SettingsUserStrideActivity.this.setResult(0, mIntent);
				finish();
			}
		});
	}

}