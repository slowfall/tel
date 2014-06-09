package com.tranway.tleshine.viewMainTabs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.FriendInfo;
import com.tranway.tleshine.model.RankAdapter;
import com.tranway.tleshine.model.TLEHttpRequest;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserInfo;
import com.tranway.tleshine.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.tleshine.model.UserInfoKeeper;

public class SocialRankFragment extends Fragment {

	private static final String GET_RANK_END_URL = "/GetSportPointRank";

	private ListView mListView;
	private RankAdapter mAdapter;
	private List<FriendInfo> mRankList = new ArrayList<FriendInfo>();
	private UserInfo mUserInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_social_rank, container, false);

		mUserInfo = UserInfoKeeper.readUserInfo(getActivity());

		initView(v);

		getRankFromServer(mUserInfo.getId(), "2014-06-05", "2014-06-06");

		return v;
	}

	private void initView(View v) {
		mListView = (ListView) v.findViewById(R.id.list_social_rank);
		mAdapter = new RankAdapter(getActivity(), mRankList);
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	private void getRankFromServer(long userId, String begin, String end) {
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
		httpRequest.get(GET_RANK_END_URL + "/" + 1 + "?begintime=" + getDateTime(-1) + "&endtime=" + getDateTime(0),
				null);
	}

	private void getRankListFromJSON(String result) throws JSONException {
		mRankList.clear();
		JSONArray jArray = new JSONArray(result);
		Log.d("-------", jArray.toString());
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
		calendar.add(Calendar.DATE, previous);
		return calendar.getTimeInMillis() / 1000;
	}
}
