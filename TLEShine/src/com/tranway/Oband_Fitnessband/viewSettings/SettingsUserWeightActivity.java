package com.tranway.Oband_Fitnessband.viewSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.widget.CustomizedWeightWheelView;

/**
 * SettingsUserWeightActivity is a custom WheelView, used to set the weight, the returned results is
 * in units of 0.1Kg, through startActivityForResult to call the Activity, and through REQUEST_CODE
 * and RESPONSE_NAME_VALUE to get results.
 * 
 * @author shz
 * 
 */
public class SettingsUserWeightActivity extends Activity {

	public static final int REQUEST_CODE = 0x1002;
	public static final String RESPONSE_NAME_VALUE = "weight";

	private CustomizedWeightWheelView mWeightWheel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_set_user_weight);

		initView();
	}

	private void initView() {
		mWeightWheel = (CustomizedWeightWheelView) findViewById(R.id.weight_wheel);
		Button mCompleteBtn = (Button) findViewById(R.id.btn_complete);
		mCompleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent();
				mIntent.putExtra(RESPONSE_NAME_VALUE, mWeightWheel.getWeight());
				SettingsUserWeightActivity.this.setResult(0, mIntent);
				finish();
			}
		});
	}

}