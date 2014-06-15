package com.tranway.tleshine.viewMainTabs;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.TLEHttpRequest;
import com.tranway.tleshine.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.tleshine.model.ToastHelper;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.model.Util;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnCheckedChangeListener {
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	public static final String LOG_ACTIVITY_SERVICE = "=====MainActivity====";
	private static final String GET_INFO_END_URL = "/Get";

	private static final int[] RADIO_BTN_IDS = new int[] { R.id.rb_h007, R.id.rb_pedometer, R.id.rb_contacts,
			R.id.rb_preset };

	private static final String TAB_1 = "h007";
	private static final String TAB_2 = "pedometer";
	private static final String TAB_3 = "contacts";
	private static final String TAB_4 = "preset";
	private static final String[] TABS = { TAB_1, TAB_2, TAB_3, TAB_4 };
	private static final int[] TABS_TITLE = { R.string.settings, R.string.activity, R.string.sleep, R.string.friends };

	private Intent mH007Intent;
	private Intent mPedometerIntent;
	private Intent mContactsIntent;
	private Intent mPresetIntent;
	private Intent[] mIntents = new Intent[TABS.length];
	private TabHost mHost;
	private RadioGroup mRadioGroup;
	private TextView mTitleTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Util.logD(LOG_TAG, "come in onCreate method");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		getUserInfoFromServer(UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_EMAIL, null));
		setup();
	}

	private void setup() {
		Util.logD(LOG_TAG, "come in setup method");

		mH007Intent = new Intent(this, MenuActivity.class);
		mPedometerIntent = new Intent(this, ActivityActivity.class);
		mContactsIntent = new Intent(this, SleepActivity.class);
		mPresetIntent = new Intent(this, FriendsActivity.class);
		mIntents = new Intent[] { mH007Intent, mPedometerIntent, mContactsIntent, mPresetIntent, };
		initTab();
		Util.logD(LOG_TAG, "go out setup method");
	}

	private void initTab() {
		mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setVisibility(View.VISIBLE);
		
		mHost = this.getTabHost();
		for (int i = 0; i < TABS.length; i++) {
			mHost.addTab(mHost.newTabSpec(TABS[i]).setIndicator(TABS[i]).setContent(mIntents[i]));
		}

		mRadioGroup = (RadioGroup) findViewById(R.id.rg_tabgroup);
		mRadioGroup.setOnCheckedChangeListener(this);
		mTitleTxt.setText(TABS_TITLE[mHost.getCurrentTab()]);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		for (int i = 0; i < RADIO_BTN_IDS.length; i++) {
			if (checkedId == RADIO_BTN_IDS[i]) {
				mHost.setCurrentTabByTag(TABS[i]);
				mTitleTxt.setText(TABS_TITLE[i]);
				break;
			}
		}
	}

	private void getUserInfoFromServer(String email) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					UserInfoKeeper.writeUserInfo(getApplicationContext(), data);
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.get_register_info_failed);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}
		}, this);
		if (email != null) {
			httpRequest.get(GET_INFO_END_URL + "/" + email, null);
		}
	}

}
