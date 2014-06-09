package com.tranway.tleshine.viewMainTabs;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tranway.tleshine.R;

public class SocialFragment extends Fragment {

	private SocialInfoFragment mInfoFragment;
	private SocialRankFragment mTodayRankFragment, mYesterdayRankFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_social, container, false);

		initView(v);

		// showTodayRankFragment();

		return v;
	}

	private void initView(View v) {
		RadioGroup mGroup = (RadioGroup) v.findViewById(R.id.radiogroup_social);
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

					break;
				default:
					break;
				}
			}
		});

	}

	private void showInfoFragment() {
		if (mInfoFragment == null) {
			mInfoFragment = new SocialInfoFragment();
		}
		getFragmentManager().beginTransaction().replace(R.id.fragment_social, mInfoFragment).commit();
	}

	private void showTodayRankFragment() {
		if (mTodayRankFragment == null) {
			mTodayRankFragment = new SocialRankFragment();
		}
		getFragmentManager().beginTransaction().replace(R.id.fragment_social, mTodayRankFragment).commit();
	}
}
