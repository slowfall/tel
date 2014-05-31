package com.tranway.tleshine.viewSettings;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.tranway.tleshine.R;

public class NightGoalFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_goal_night, container, false);

		initView(v);

		return v;
	}

	private void initView(View v) {
		// v.findViewById(R.id.btn_settings).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		default:
			break;
		}

	}
}
