package com.tranway.tleshine.viewMainTabs;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.UserInfo;
import com.tranway.tleshine.model.UserInfoKeeper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SocialFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_social, container, false);

		initView(v);
		return v;
	}

	private void initView(View v) {
		TextView userEmail = (TextView) v.findViewById(R.id.tv_user_info_email);
		userEmail.setText(UserInfoKeeper
				.readUserInfo(getActivity(), UserInfo.SEVER_KEY_CREATE_DATE));
		
	}
}
