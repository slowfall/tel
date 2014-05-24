package com.tranway.tleshine.viewSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tranway.tleshine.R;

public class SettingsActivity extends Activity implements OnClickListener {
	private static final String TAG = SettingsActivity.class.getSimpleName();

	// private SegmentedGroup mDistanceGroup, mWeightGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings);

		initView();
	}

	private void initView() {
		initTitleView();
		
		findViewById(R.id.btn_userinfo).setOnClickListener(this);
		findViewById(R.id.btn_connect).setOnClickListener(this);
		findViewById(R.id.btn_logout).setOnClickListener(this);
		findViewById(R.id.btn_support).setOnClickListener(this);
		findViewById(R.id.btn_provision).setOnClickListener(this);

	}

	private void initTitleView() {
		Button mPreBtn = (Button) findViewById(R.id.btn_title_left);
		mPreBtn.setText(R.string.pre_step);
		mPreBtn.setVisibility(View.VISIBLE);
		mPreBtn.setOnClickListener(this);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.settings);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_userinfo:
			Intent intent = new Intent(this, SettingsUserInfoActivity.class);
			startActivity(intent);
			break;
		}
	}
}
