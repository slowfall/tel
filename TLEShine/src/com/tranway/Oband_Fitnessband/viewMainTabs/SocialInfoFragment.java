package com.tranway.Oband_Fitnessband.viewMainTabs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.Tools;
import com.tranway.Oband_Fitnessband.model.FriendInfo;
import com.tranway.Oband_Fitnessband.model.FriendsAdapter;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserInfo;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;

public class SocialInfoFragment extends Fragment {
	private static final String TAG = "SocialInfoFragment";

	private static final String GET_FRIENDS_END_URL = "/GetFriends";

	private ImageView mUserPicture;
	private List<FriendInfo> mFriendList = new ArrayList<FriendInfo>();
	private FriendsAdapter mAdapter;
	private ListView mListView;
	private UserInfo mUserInfo;
	private TextView mContinueCountText;
	private TextView mMaxPointText;
	private TextView mGoalCountText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_social_info, container, false);

		mUserInfo = UserInfoKeeper.readUserInfo(getActivity());
		initView(v);
		return v;
	}

	@Override
	public void onResume() {
		getUserFriendsFromServer(mUserInfo.getId());
		long continueCount = UserInfoKeeper.readUserInfo(getActivity(),
				UserInfoKeeper.KEY_CONTINUE_COUNT, 0l);
		long maxPoint = UserInfoKeeper
				.readUserInfo(getActivity(), UserInfoKeeper.KEY_MAX_POINT, 0l);
		long goalCount = UserInfoKeeper.readUserInfo(getActivity(), UserInfoKeeper.KEY_GOAL_COUNT,
				0l);
		mContinueCountText.setText(String.valueOf(continueCount));
		mMaxPointText.setText(String.valueOf(maxPoint));
		mGoalCountText.setText(String.valueOf(goalCount));

		super.onResume();
	}

	private void initView(View v) {
		ImageView userPicture = (ImageView) v.findViewById(R.id.iv_user_picture);
		if (mUserInfo.getAvatar() != null) {
			userPicture.setImageBitmap(Tools.base64ToBitmap(mUserInfo.getAvatar()));
		}
		TextView userEmail = (TextView) v.findViewById(R.id.tv_user_info_email);
		userEmail.setText(mUserInfo.getEmail());

		mContinueCountText = (TextView) v.findViewById(R.id.tv_user_info_cycle1);
		mMaxPointText = (TextView) v.findViewById(R.id.tv_user_info_cycle2);
		mGoalCountText = (TextView) v.findViewById(R.id.tv_user_info_cycle3);

		TextView userDate = (TextView) v.findViewById(R.id.tv_user_info_day_user);
		SimpleDateFormat format = new SimpleDateFormat("MM-dd", Locale.getDefault());
		userDate.setText(String.format(getString(R.string.day_string_of_user_login),
				format.format(new Date())));

		((ImageButton) v.findViewById(R.id.btn_add_friend))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), AddFriendActivity.class);
						startActivity(intent);
					}
				});

		mListView = (ListView) v.findViewById(R.id.list_social_friends);
		mAdapter = new FriendsAdapter(getActivity(), mFriendList);
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	private void getUserFriendsFromServer(long userId) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, String result) {
				try {
					getFriendListFromJSON(result);
					mAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.get_user_friend_list_failed, Toast.LENGTH_LONG);
			}
		}, getActivity());
		httpRequest.get(GET_FRIENDS_END_URL + "/" + userId, null);
	}

	private void getFriendListFromJSON(String result) throws JSONException {
		mFriendList.clear();
		JSONArray jArray = new JSONArray(result);
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject jObj = jArray.getJSONObject(i);
			FriendInfo info = new FriendInfo();
			info.setEmail(jObj.getString(UserInfo.SEVER_KEY_EMAIL));
			info.setId(jObj.getLong(UserInfo.SEVER_KEY_ID));
			info.setName(jObj.getString(UserInfo.SEVER_KEY_NAME));
			mFriendList.add(info);
		}
	}

}
