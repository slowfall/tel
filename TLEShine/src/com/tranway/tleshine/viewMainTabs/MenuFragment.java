package com.tranway.tleshine.viewMainTabs;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tranway.tleshine.R;
import com.tranway.tleshine.viewSettings.SettingsActivity;
import com.tranway.tleshine.viewSettings.SettingsGoalActivity;

public class MenuFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_menu, container, false);

		initView(v);

		return v;
	}

	private void initView(View v) {
		v.findViewById(R.id.btn_goal).setOnClickListener(this);
//		v.findViewById(R.id.btn_shine).setOnClickListener(this);
		v.findViewById(R.id.btn_settings).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		Intent intent;
		switch (arg0.getId()) {
		case R.id.btn_goal:
			intent = new Intent(getActivity(), SettingsGoalActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_shine:

			break;
		case R.id.btn_settings:
			intent = new Intent(getActivity(), SettingsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}
}
