package com.tranway.tleshine.viewMainTabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.tranway.tleshine.R;
import com.tranway.tleshine.viewSettings.SettingsActivity;
import com.tranway.tleshine.viewSettings.SettingsGoalActivity;

public class MenuActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		initView();
	}

	private void initView() {
		findViewById(R.id.btn_goal).setOnClickListener(this);
//		findViewById(R.id.btn_shine).setOnClickListener(this);
		findViewById(R.id.btn_settings).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		Intent intent;
		switch (arg0.getId()) {
		case R.id.btn_goal:
			intent = new Intent(this, SettingsGoalActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_shine:

			break;
		case R.id.btn_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}
}
