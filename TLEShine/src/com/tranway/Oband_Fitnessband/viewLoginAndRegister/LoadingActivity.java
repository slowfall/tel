package com.tranway.Oband_Fitnessband.viewLoginAndRegister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.Util;
import com.tranway.Oband_Fitnessband.welcome.WelcomeActivity;

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
