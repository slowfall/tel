package com.tranway.tleshine.viewSettings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.MyApplication;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.viewLoginAndRegister.RegisterUserInfoActivity;
import com.tranway.tleshine.viewLoginAndRegister.SelectLoginActivity;

public class SettingsActivity extends Activity implements OnClickListener {
	private static final String TAG = SettingsActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings);

		initView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_FIRST_USER) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initView() {
		initTitleView();

		findViewById(R.id.btn_userinfo).setOnClickListener(this);
		findViewById(R.id.btn_logout).setOnClickListener(this);

	}

	private void initTitleView() {
		ImageButton mPreBtn = (ImageButton) findViewById(R.id.btn_title_icon_left);
		mPreBtn.setVisibility(View.VISIBLE);
		mPreBtn.setOnClickListener(this);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.my_settings);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_userinfo:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putBoolean("isRegister", false);
			intent.setClass(this, RegisterUserInfoActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, RESULT_FIRST_USER);
			break;
		case R.id.btn_logout:
			confirmLogoutDialog();
			break;
		case R.id.btn_title_icon_left:
			finish();
			break;
		}
	}

	protected void confirmLogoutDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_confirm, null);
		final Dialog dialog = new Dialog(this, R.style.DialogTheme);
		TextView title = (TextView) dialogView.findViewById(R.id.layout_content);
		title.setText(getResources().getString(R.string.confirm_logout));
		dialog.setContentView(dialogView);
		dialog.show();
		dialog.setTitle(R.string.app_name);
		Button positiveBtn = (Button) dialogView.findViewById(R.id.positive);
		positiveBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(MyApplication.getAppContext(), SelectLoginActivity.class);
				UserInfoKeeper.writeUserInfo(getApplicationContext(),
						UserInfoKeeper.KEY_KEEP_SIGN_IN, false);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				// SettingsActivity.this.finish();
			}
		});
		Button negativeBtn = (Button) dialogView.findViewById(R.id.negative);
		negativeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
}
