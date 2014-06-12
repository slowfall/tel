package com.tranway.tleshine.viewMainTabs;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tranway.tleshine.R;

public class FriendsActivity extends Activity {

	private static final String TAG_INFO = "info";
	private static final String TAG_TODAY = "today";
	private static final String TAG_YESTERDAY = "yesterday";
	private static final String SAVED_TAG = "fragment_tag";

	private SocialInfoFragment mInfoFragment;
	private SocialRankFragment mTodayRankFragment, mYesterdayRankFragment;

	private String mRadioTag = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		initView();

		showInfoFragment();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(SAVED_TAG, mRadioTag);
	}

	private void initView() {
		RadioGroup mGroup = (RadioGroup) findViewById(R.id.radiogroup_social);
		mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.radio_my_info:
					showInfoFragment();
					break;
				case R.id.radio_rank_today:
					showTodayRankFragment();
					break;
				case R.id.radio_rank_yesterday:
					showTodayRankFragment();
					break;
				default:
					break;
				}
			}
		});
	}

	private void showInfoFragment() {
		mRadioTag = TAG_INFO;
		mInfoFragment = new SocialInfoFragment();
		getFragmentManager().beginTransaction().replace(R.id.fragment_social, mInfoFragment)
				.commit();
	}

	private void showTodayRankFragment() {
		mRadioTag = TAG_TODAY;
		mTodayRankFragment = new SocialRankFragment();
		getFragmentManager().beginTransaction().replace(R.id.fragment_social, mTodayRankFragment)
				.commit();
	}
}