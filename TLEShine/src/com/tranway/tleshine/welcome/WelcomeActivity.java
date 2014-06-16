package com.tranway.tleshine.welcome;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.Util;
import com.tranway.tleshine.viewLoginAndRegister.LoadingActivity;
import com.tranway.tleshine.viewLoginAndRegister.SelectLoginActivity;

public class WelcomeActivity extends Activity implements OnPageChangeListener, OnClickListener {
	private static final int[] pics = { R.drawable.welcome_01, R.drawable.welcome_02,
			R.drawable.welcome_03, R.drawable.welcome_04, R.drawable.welcome_05 };
	private Button mFinishBtn;
	private ViewPager mViewPager;
	private ViewPagerAdapter mPagerAdapter;
	private List<View> mViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		setup();
	}

	private void setup() {
		mFinishBtn = (Button) findViewById(R.id.btn_finish);
		mFinishBtn.setOnClickListener(this);

		mViews = new ArrayList<View>();
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		for (int i = 0; i < pics.length; i++) {
			if (i == (pics.length - 1)) {
				RelativeLayout layout = new RelativeLayout(this);
				layout.setLayoutParams(mParams);

				ImageView iv = new ImageView(this);
				RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.MATCH_PARENT);
				iv.setLayoutParams(rlParams);
				iv.setImageResource(pics[i]);
				iv.setScaleType(ScaleType.FIT_XY);
				layout.addView(iv);

				Button finishBtn = new Button(this);
				rlParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
				finishBtn.setLayoutParams(rlParams);
				finishBtn.setText(R.string.now_use);
				finishBtn.setTextColor(getResources().getColor(android.R.color.white));
				finishBtn.setBackgroundResource(R.drawable.btn_welcome_over);
				finishBtn.setTag(LoadingActivity.FIRST_USE);
				finishBtn.setOnClickListener(this);
				layout.addView(finishBtn);

				mViews.add(layout);
			} else {
				ImageView iv = new ImageView(this);
				iv.setLayoutParams(mParams);
				iv.setImageResource(pics[i]);
				iv.setScaleType(ScaleType.FIT_XY);
				mViews.add(iv);
			}
		}
		mViewPager = (ViewPager) findViewById(R.id.vp_guide);
		mPagerAdapter = new ViewPagerAdapter(mViews);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}

	@Override
	public void onClick(View v) {
		String tag = (String) v.getTag();
		if (LoadingActivity.FIRST_USE.equals(tag)) {
			Util.writePreferences(this, LoadingActivity.FIRST_USE, false);
			Intent intent = new Intent(getApplicationContext(), SelectLoginActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
