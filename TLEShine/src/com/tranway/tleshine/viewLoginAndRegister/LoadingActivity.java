package com.tranway.tleshine.viewLoginAndRegister;

import com.tranway.tleshine.R;
import com.tranway.tleshine.R.layout;
import com.tranway.tleshine.model.UserInfo;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.model.Util;
import com.tranway.tleshine.welcome.WelcomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class LoadingActivity extends Activity {
	public static String FIRST_USE = "first_use";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		
		Intent intent;
		if (Util.readPreferences(this, FIRST_USE, true)) {
			intent = new Intent(getApplicationContext(), WelcomeActivity.class);
		} else {
			intent = new Intent(getApplicationContext(), SelectLoginActivity.class);
		}
		startActivity(intent);
		finish();
	}
}
