package com.tranway.tleshine.viewLoginAndRegister;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.tranway.tleshine.R;

public class ForgotPasswordActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		initTitleView();
	}

	private void initTitleView() {
		Button leftBtn = (Button) findViewById(R.id.btn_title_icon_left);
		leftBtn.setText(R.string.back);
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setOnClickListener(this);
		// Button mNextBtn = (Button) findViewById(R.id.btn_title_right);
		// mNextBtn.setText(R.string.next_step);
		// mNextBtn.setOnClickListener(this);
		// mNextBtn.setVisibility(View.VISIBLE);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.forgot_password);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_icon_left:
			finish();
			break;

		default:
			break;
		}

	}

}
