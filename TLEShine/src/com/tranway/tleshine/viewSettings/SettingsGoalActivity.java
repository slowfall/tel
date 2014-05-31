package com.tranway.tleshine.viewSettings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.tranway.tleshine.R;

public class SettingsGoalActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings_goal);

		initView();
	}

	private void initView() {
		initTitleView();
		showDayFragment();
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
		mTitleTxt.setText(R.string.my_goal);

		// final RadioButton mDayRadio = (RadioButton)
		// findViewById(R.id.radio_day);
		// final RadioButton mNightRadio = (RadioButton)
		// findViewById(R.id.radio_night);
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if (arg1 == R.id.radio_day) {
					showDayFragment();
				} else {
					showNightFragment();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			saveGoal();
			finish();
			break;
		}
	}

	private void showDayFragment() {
		DayGoalFragment fragment = new DayGoalFragment();
		getFragmentManager().beginTransaction().replace(R.id.layout_fragment, fragment).commit();
	}

	private void showNightFragment() {
		NightGoalFragment fragment = new NightGoalFragment();
		getFragmentManager().beginTransaction().replace(R.id.layout_fragment, fragment).commit();
	}

	private void saveGoal() {
		// TODO..
	}
}