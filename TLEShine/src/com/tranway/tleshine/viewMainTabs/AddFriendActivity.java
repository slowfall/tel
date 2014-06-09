package com.tranway.tleshine.viewMainTabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.FriendInfo;
import com.tranway.tleshine.model.FriendsAdapter;
import com.tranway.tleshine.model.FriendsAdapter.OnListItemClickListener;
import com.tranway.tleshine.model.TLEHttpRequest;
import com.tranway.tleshine.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserInfo;
import com.tranway.tleshine.model.UserInfoKeeper;

public class AddFriendActivity extends Activity implements OnClickListener {
	private static final String TAG = "SocialInfoFragment";

	private static final String SEARCH_USER_END_URL = "/GetUsersByEmail";
	private static final String ADD_FRIEND_END_URL = "/AddFriend";

	private List<FriendInfo> mSearchList = new ArrayList<FriendInfo>();
	private FriendsAdapter mAdapter;
	private ListView mListView;
	private EditText mSearchTxt;
	private TextView mEmptyTxt;

	private long mUserId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_social_add_friend);

		mUserId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1L);
		initView();
	}

	private void initView() {
		initTitleView();

		mSearchTxt = (EditText) findViewById(R.id.txt_search_info);
		((Button) findViewById(R.id.btn_search)).setOnClickListener(this);
		mEmptyTxt = (TextView) findViewById(R.id.txt_empty);

		mListView = (ListView) findViewById(R.id.list_search_friends);
		mAdapter = new FriendsAdapter(this, mSearchList, true);
		mAdapter.setOnListItemClickListener(new OnListItemClickListener() {
			@Override
			public void onItemButtonClick(int index) {
				if (mSearchList.size() >= index) {
					addFriendToServer(mUserId, mSearchList.get(index).getId());
				}
			}
		});
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	private void initTitleView() {
		Button mExsitBtn = (Button) findViewById(R.id.btn_title_left);
		mExsitBtn.setText(R.string.back);
		mExsitBtn.setVisibility(View.VISIBLE);
		mExsitBtn.setOnClickListener(this);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.add_friend);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_search:
			searchUserFromServer(mSearchTxt.getText().toString());
			break;
		default:
			break;
		}
	}

	private void searchUserFromServer(String info) {
		if (info == null || TextUtils.isEmpty(info)) {
			ToastHelper.showToast(R.string.input_search_info, Toast.LENGTH_LONG);
			return;
		}

		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, String result) {
				try {
					getUserListFromJSON(result);
					notifySearchResultListView();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.search_friend_failed, Toast.LENGTH_LONG);
			}
		}, this);
		httpRequest.get(SEARCH_USER_END_URL + "/" + info, null);
	}

	private void addFriendToServer(long userId, long friendId) {
		userId = 78; // for test
		if (userId < 0 || friendId < 0) {
			Log.e(TAG, "userId or friendId exception, userId = " + userId + "; friendId = " + friendId);
			return;
		}

		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {
			@Override
			public void onSuccess(String url, String result) {
				JSONObject data;
				try {
					data = new JSONObject(result);
					if (data.has(TLEHttpRequest.STATUS_CODE)) {
						int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
						if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
							ToastHelper.showToast(R.string.add_friend_success, Toast.LENGTH_LONG);
						} else {
							String errorMsg = data.getString(TLEHttpRequest.MSG);
							ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.add_friend_failed, Toast.LENGTH_LONG);
			}
		}, this);

		Map<String, String> data = new TreeMap<String, String>();
		data.put("UserID", String.valueOf(userId));
		data.put("FriendID", String.valueOf(friendId));
		data.put("CreateDate", String.valueOf(System.currentTimeMillis() / 1000));
		httpRequest.post(ADD_FRIEND_END_URL, data);
	}

	private void getUserListFromJSON(String result) throws JSONException {
		Log.d(TAG, "search result: " + result);
		mSearchList.clear();
		JSONArray jArray = new JSONArray(result);
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject jObj = jArray.getJSONObject(i);
			FriendInfo info = new FriendInfo();
			info.setEmail(jObj.getString(UserInfo.SEVER_KEY_EMAIL));
			info.setId(jObj.getLong(UserInfo.SEVER_KEY_ID));
			info.setName(jObj.getString(UserInfo.SEVER_KEY_NAME));
			mSearchList.add(info);
		}
	}

	private void notifySearchResultListView() {
		if (mSearchList.size() <= 0) {
			mEmptyTxt.setVisibility(View.VISIBLE);
		} else {
			mEmptyTxt.setVisibility(View.GONE);
		}
		mAdapter.notifyDataSetChanged();
	}

}
