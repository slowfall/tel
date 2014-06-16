package com.tranway.tleshine.viewMainTabs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tranway.tleshine.R;

public class FriendsActivity extends Activity {

	private static final String TAG_INFO = "info";
	private static final String TAG_TODAY = "today";
	private static final String TAG_YESTERDAY = "yesterday";
	public static final String SAVED_TAG = "fragment_tag";

	private SocialInfoFragment mInfoFragment;
	private SocialRankFragment mDayRankFragment, mYesterdayRankFragment;

	private String mRadioTag = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		initView();
		//		showInfoFragment();
		showFragment(R.id.radio_my_info);
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
				showFragment(arg1);
			}
		});
	}

	private void showFragment(int radioId) {
		switch (radioId) {
		case R.id.radio_my_info:
			mInfoFragment = new SocialInfoFragment();
			getFragmentManager().beginTransaction().replace(R.id.fragment_social, mInfoFragment)
					.commit();
			break;
		case R.id.radio_rank_today:
		case R.id.radio_rank_yesterday:
			mDayRankFragment = new SocialRankFragment();
			Bundle bundle = new Bundle();
			bundle.putInt(SAVED_TAG, radioId);
			mDayRankFragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.fragment_social, mDayRankFragment)
					.commit();
			break;
		default:
			break;
		}
	}
	private void showInfoFragment() {
		mRadioTag = TAG_INFO;
		mInfoFragment = new SocialInfoFragment();
		getFragmentManager().beginTransaction().replace(R.id.fragment_social, mInfoFragment)
				.commit();
	}

	private void showTodayRankFragment() {
		mRadioTag = TAG_TODAY;
		mDayRankFragment = new SocialRankFragment();
		getFragmentManager().beginTransaction().replace(R.id.fragment_social, mDayRankFragment)
				.commit();
	}
}
