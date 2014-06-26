package com.tranway.Oband_Fitnessband.viewMainTabs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.FriendInfo;
import com.tranway.Oband_Fitnessband.model.RankAdapter;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserInfo;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.model.Util;

public class SocialRankFragment extends Fragment {

	private static final String GET_RANK_END_URL = "/GetSportPointRank";

	private ListView mListView;
	private RankAdapter mAdapter;
	private List<FriendInfo> mRankList = new ArrayList<FriendInfo>();
	private UserInfo mUserInfo;
	private int mDayOffset = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_social_rank, container, false);

		mUserInfo = UserInfoKeeper.readUserInfo(getActivity());

		initView(v);

		Bundle bundle = getArguments();
		switch (bundle.getInt(FriendsActivity.SAVED_TAG)) {
		case R.id.radio_rank_today:
			mDayOffset = 0;
			break;
		case R.id.radio_rank_yesterday:
			mDayOffset = -1;
			break;
		default:
			break;
		}

		getRankFromServer(mUserInfo.getId());

		return v;
	}

	private void initView(View v) {
		mListView = (ListView) v.findViewById(R.id.list_social_rank);
		mAdapter = new RankAdapter(getActivity(), mRankList);
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	private void getRankFromServer(long userId) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, String result) {
				try {
					getRankListFromJSON(result);
					mAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.get_rank_list_failed, Toast.LENGTH_LONG);
			}
		}, getActivity());
		httpRequest.get(GET_RANK_END_URL + "/" + userId + "?begintime="
				+ getDateTime(mDayOffset - 1) + "&endtime=" + getDateTime(mDayOffset), null);
	}

	private void getRankListFromJSON(String result) throws JSONException {
		mRankList.clear();
		JSONArray jArray = new JSONArray(result);
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject jObj = jArray.getJSONObject(i);
			FriendInfo info = new FriendInfo();
			info.setPoint(jObj.getInt(UserInfo.SEVER_KEY_POINT));
			JSONObject userObject = jObj.getJSONObject(UserInfo.SEVER_KEY_USER);
			info.setEmail(userObject.getString(UserInfo.SEVER_KEY_EMAIL));
			info.setId(userObject.getLong(UserInfo.SEVER_KEY_ID));
			info.setName(userObject.getString(UserInfo.SEVER_KEY_NAME));
			info.setSex(userObject.getInt(UserInfo.SEVER_KEY_SEX));
			mRankList.add(info);
		}
		Collections.sort(mRankList);
	}

	/**
	 * get date time 'previous' before today
	 * 
	 * @param previous
	 *            previous day before today
	 * @return date time
	 */
	private long getDateTime(int previous) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH) + previous, 0, 0, 0);
		return calendar.getTimeInMillis() / 1000;
	}
}
