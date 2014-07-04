package com.tranway.Oband_Fitnessband.viewMainTabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.FriendInfo;
import com.tranway.Oband_Fitnessband.model.RequestAdapter;
import com.tranway.Oband_Fitnessband.model.RequestAdapter.OnRequestListItemClickListener;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserInfo;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.util.ListViewUtil;

public class NewFriendFragment extends Fragment {
	private static final String TAG = "NewFriendFragment";

	private static final String GET_REQUEST_FRIENDS_END_URL = "/GetWaitingFriends";
	private static final String GET_RECEIVE_FRIENDS_END_URL = "/GetReceivedRequests";
	private static final String UPDATE_FRIENDS_END_URL = "/UpdateFriend";

	private List<FriendInfo> mRequestFriendList = new ArrayList<FriendInfo>();
	private List<FriendInfo> mReceiveFriendList = new ArrayList<FriendInfo>();
	private RequestAdapter mRequestAdapter, mReceiveAdapter;
	private ListView mRequestListView, mReceiveListView;
	private long userId;
	private TextView mEmptyRequestText, mEmptyReceiveText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_social_new_friend, container, false);

		userId = UserInfoKeeper.readUserInfo(getActivity(), UserInfoKeeper.KEY_ID, 1L);
		if (userId != -1) {
			initView(v);
			getRequestListFromServer(userId);
			// getReceiveListFromServer(userId);
		} else {
			ToastHelper.showToast(R.string.app_exception);
		}
		return v;
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	private void initView(View v) {
		mEmptyRequestText = (TextView) v.findViewById(R.id.txt_empty_request);
		mEmptyReceiveText = (TextView) v.findViewById(R.id.txt_empty_received);

		mRequestListView = (ListView) v.findViewById(R.id.list_request);
		mReceiveListView = (ListView) v.findViewById(R.id.list_received);

		mRequestAdapter = new RequestAdapter(getActivity(), mRequestFriendList);
		mRequestListView.setAdapter(mRequestAdapter);
		mRequestAdapter.notifyDataSetChanged();

		mReceiveAdapter = new RequestAdapter(getActivity(), mReceiveFriendList, true);
		mReceiveListView.setAdapter(mReceiveAdapter);
		mReceiveAdapter.setOnListItemClickListener(new OnRequestListItemClickListener() {

			@Override
			public void onItemButtonClick(View view, int index) {
				long reqId = mReceiveFriendList.get(index).getId();
				handleRequestToServer(reqId, (view.getId() == R.id.btn_accept) ? true : false);
			}
		});
		mReceiveAdapter.notifyDataSetChanged();
	}

	private void handleRequestToServer(long reqId, boolean isAccept) {
		Log.d(TAG, "---userId: " + userId + ";  reqId: " + reqId + ";  handle: " + isAccept);
		if (userId < 0 || reqId < 0) {
			ToastHelper.showToast(R.string.app_exception);
			return;
		}

		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		Map<String, String> params = new HashMap<String, String>();
		params.put(UserInfo.SEVER_KEY_USER_ID, String.valueOf(userId));
		params.put(UserInfo.SEVER_KEY_FRIEND_ID, String.valueOf(reqId));
		params.put(UserInfo.SEVER_KEY_HANDLE_STATE, String.valueOf(isAccept));
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				// TODO Auto-generated method stub
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}

			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
					if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
						ToastHelper.showToast(R.string.success_handle_request, Toast.LENGTH_LONG);
						getReceiveListFromServer(userId);
					} else {
						String errorMsg = data.getString(TLEHttpRequest.MSG);
						ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, getActivity());
		httpRequest.post(UPDATE_FRIENDS_END_URL + "/", params);
	}

	private void updateRequestListViewUI() {

		Log.d(TAG, "-----1------");
		if (mRequestFriendList.size() == 0) {
			mEmptyRequestText.setVisibility(View.VISIBLE);
			mRequestListView.setVisibility(View.GONE);
		} else {
			mEmptyRequestText.setVisibility(View.GONE);
			mRequestListView.setVisibility(View.VISIBLE);
		}
		mRequestAdapter.notifyDataSetChanged();
		ListViewUtil.setListViewHeightBasedOnChildren(mRequestListView);
	}

	private void updateReceiveListViewUI() {
		Log.d(TAG, "-----2------");
		if (mReceiveFriendList.size() == 0) {
			mEmptyReceiveText.setVisibility(View.VISIBLE);
			mReceiveListView.setVisibility(View.GONE);
		} else {
			mEmptyReceiveText.setVisibility(View.GONE);
			mReceiveListView.setVisibility(View.VISIBLE);
		}
		mReceiveAdapter.notifyDataSetChanged();
		ListViewUtil.setListViewHeightBasedOnChildren(mReceiveListView);
	}

	private void getRequestListFromServer(final long userId) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, String result) {
				try {
					Log.d(TAG, "-----1--1----");
					getFriendListFromJSON(mRequestFriendList, result);
					updateRequestListViewUI();
				} catch (JSONException e) {
					e.printStackTrace();
				}

				getReceiveListFromServer(userId);
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.get_user_friend_list_failed, Toast.LENGTH_LONG);

				getReceiveListFromServer(userId);
			}
		}, getActivity());

		httpRequest.get(GET_REQUEST_FRIENDS_END_URL + "/" + userId, null);
	}

	private void getReceiveListFromServer(long userId) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, String result) {
				try {
					Log.d(TAG, "-----2--2----");
					getFriendListFromJSON(mReceiveFriendList, result);
					updateReceiveListViewUI();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.get_user_friend_list_failed, Toast.LENGTH_LONG);
			}
		}, getActivity());

		httpRequest.get(GET_RECEIVE_FRIENDS_END_URL + "/" + userId, null);
	}

	private void getFriendListFromJSON(List<FriendInfo> mList, String result) throws JSONException {
		mList.clear();
		JSONArray jArray = new JSONArray(result);
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject jObj = jArray.getJSONObject(i);
			FriendInfo info = new FriendInfo();
			info.setEmail(jObj.getString(UserInfo.SEVER_KEY_EMAIL));
			info.setId(jObj.getLong(UserInfo.SEVER_KEY_ID));
			info.setName(jObj.getString(UserInfo.SEVER_KEY_NAME));
			if (!jObj.isNull(UserInfo.SEVER_KEY_AVATAR)) {
				info.setAvatar(jObj.getString(UserInfo.SEVER_KEY_AVATAR));
			}
			Log.d(TAG, "---friend name: " + info.getName());
			mList.add(info);
		}
	}

}
