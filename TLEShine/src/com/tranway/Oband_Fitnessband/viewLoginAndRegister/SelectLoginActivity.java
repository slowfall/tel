package com.tranway.Oband_Fitnessband.viewLoginAndRegister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;

public class SelectLoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_login);
		setup();
	}

	@Override
	protected void onResume() {
		boolean isKeepSignin = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_KEEP_SIGN_IN,
				false);
		if (isKeepSignin) {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
		}
		super.onResume();
	}

	private void setup() {
		findViewById(R.id.btn_login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.btn_register).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(intent);
			}
		});
	}
}
