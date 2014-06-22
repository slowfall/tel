package com.tranway.tleshine.viewMainTabs;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TabActivity;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
@SuppressLint("NewApi")
public class MainActivity extends TabActivity implements OnCheckedChangeListener {
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	public static final String LOG_ACTIVITY_SERVICE = "=====MainActivity====";
	private static final String GET_INFO_END_URL = "/Get";

	private static final int[] RADIO_BTN_IDS = new int[] { R.id.rb_activity, R.id.rb_sleep,
			R.id.rb_friends, R.id.rb_settings };

	private static final String TAB_1 = "activity";
	private static final String TAB_2 = "sleep";
	private static final String TAB_3 = "friends";
	private static final String TAB_4 = "settings";
	private static final String[] TABS = { TAB_1, TAB_2, TAB_3, TAB_4 };
	private static final int[] TABS_TITLE = { R.string.activity, R.string.sleep, R.string.friends,
			R.string.settings };

	private Intent mActivityIntent;
	private Intent mSleepIntent;
	private Intent mFriendsIntent;
	private Intent mSettingsIntent;
	private Intent[] mIntents = new Intent[TABS.length];
	private TabHost mHost;
	private RadioGroup mRadioGroup;
	private TextView mTitleTxt;
	private Dialog mCheckBLEDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Util.logD(LOG_TAG, "come in onCreate method");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		getUserInfoFromServer(UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_EMAIL, null));
		setup();
		checkBLE();
	}

	@Override
	protected void onDestroy() {
		if (mCheckBLEDialog != null && mCheckBLEDialog.isShowing()) {
			mCheckBLEDialog.dismiss();
		}
		super.onDestroy();
	}

	private void setup() {
		Util.logD(LOG_TAG, "come in setup method");

		mActivityIntent = new Intent(this, TestActivity.class);
		mSleepIntent = new Intent(this, SleepActivity.class);
		mFriendsIntent = new Intent(this, FriendsActivity.class);
		mSettingsIntent = new Intent(this, MenuActivity.class);
		mIntents = new Intent[] { mActivityIntent, mSleepIntent, mFriendsIntent, mSettingsIntent };
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

	private void checkBLE() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			ShowBleNotSupportDialog();
			return;
		}

		final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

		if (mBluetoothManager.getAdapter() == null) {
			ShowBleNotSupportDialog();
			return;
		}
	}

	private void ShowBleNotSupportDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_confirm, null);
		mCheckBLEDialog = new Dialog(this, R.style.DialogTheme);
		TextView title = (TextView) dialogView.findViewById(R.id.layout_content);
		title.setText(getResources().getString(R.string.ble_not_supported_tips));
		mCheckBLEDialog.setContentView(dialogView);
		Button positiveBtn = (Button) dialogView.findViewById(R.id.positive);
		positiveBtn.setVisibility(View.GONE);
		ImageView line = (ImageView) dialogView.findViewById(R.id.line_btn);
		line.setVisibility(View.GONE);
		Button negativeBtn = (Button) dialogView.findViewById(R.id.negative);
		negativeBtn.setText(R.string.OK);
		negativeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mCheckBLEDialog.dismiss();
			}
		});
		mCheckBLEDialog.show();
	}

}
