package com.tranway.tleshine.viewMainTabs;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.tranway.tleshine.R;
import com.tranway.tleshine.model.Util;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnCheckedChangeListener {
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	public static final String LOG_ACTIVITY_SERVICE = "=====MainActivity====";

	private static final int[] RADIO_BTN_IDS = new int[] { R.id.rb_h007, R.id.rb_pedometer,
			R.id.rb_contacts, R.id.rb_preset };

	private static final String TAB_1 = "h007";
	private static final String TAB_2 = "pedometer";
	private static final String TAB_3 = "contacts";
	private static final String TAB_4 = "preset";
	private static final String[] TABS = { TAB_1, TAB_2, TAB_3, TAB_4 };

	private Intent mH007Intent;
	private Intent mPedometerIntent;
	private Intent mContactsIntent;
	private Intent mPresetIntent;
	private Intent[] mIntents = new Intent[TABS.length];
	private TabHost mHost;
	private RadioGroup mRadioGroup;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Util.logD(LOG_TAG, "come in onCreate method");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
		mHost=this.getTabHost();
		for (int i = 0; i < TABS.length; i++) {
			mHost.addTab(mHost.newTabSpec(TABS[i]).setIndicator(TABS[i]).setContent(mIntents[i]));
		}

		mRadioGroup = (RadioGroup) findViewById(R.id.rg_tabgroup);
		mRadioGroup.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		for (int i = 0; i < RADIO_BTN_IDS.length; i++) {
			if (checkedId == RADIO_BTN_IDS[i]) {
				mHost.setCurrentTabByTag(TABS[i]);
				break;
			}
		}
	}
}
