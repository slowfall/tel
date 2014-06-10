package com.tranway.tleshine.viewMainTabs;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.tranway.tleshine.R;

public class MainTabsActivity extends Activity {
	private static final int TAB_TAG_MENU = 0;
	private static final int TAB_TAG_DAY = 1;
	private static final int TAB_TAG_NIGHT = 2;
	private static final int TAB_TAG_SOCIAL = 3;

	private static final String SELECTED_ITEM = "selected_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_tabs);

		setup();
	}

	private void setup() {
		ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		Tab menuTab = actionBar.newTab();
		menuTab.setIcon(R.drawable.setting);
		menuTab.setTag(TAB_TAG_MENU);
		menuTab.setTabListener(new MyTabListener(new MenuFragment()));

		Tab dayTab = actionBar.newTab();
		dayTab.setIcon(R.drawable.target);
		dayTab.setTag(TAB_TAG_DAY);
		dayTab.setTabListener(new MyTabListener(new DayFragment()));

		Tab nightTab = actionBar.newTab();
		nightTab.setIcon(R.drawable.sleep);
		nightTab.setTag(TAB_TAG_NIGHT);
		nightTab.setTabListener(new MyTabListener(new NightFragment()));

		Tab socialTab = actionBar.newTab();
		socialTab.setIcon(R.drawable.friends);
		socialTab.setTag(TAB_TAG_SOCIAL);
		socialTab.setTabListener(new MyTabListener(new SocialFragment()));

		actionBar.addTab(menuTab);
		actionBar.addTab(dayTab);
		actionBar.addTab(nightTab);
		actionBar.addTab(socialTab);
		actionBar.selectTab(dayTab);
	}

	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(SELECTED_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_ITEM));
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(SELECTED_ITEM, getActionBar().getSelectedNavigationIndex());
	}

	protected class MyTabListener implements TabListener {
		private Fragment mFragment;

		public MyTabListener(Fragment fragment) {
			mFragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// int tag = (Integer) tab.getTag();
			// switch (tag) {
			// case TAB_TAG_MENU:
			// ft.replace(R.id.fl_fragment_replace, mFragment);
			// break;
			// case TAB_TAG_DAY:
			// case TAB_TAG_NIGHT:
			// ft.replace(R.id.fl_fragment_replace, mFragment);
			// break;
			// case TAB_TAG_SOCIAL:
			// ft.replace(R.id.fl_fragment_replace, mFragment);
			// default:
			// break;
			// }
			ft.replace(R.id.fl_fragment_replace, mFragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// int tag = (Integer) tab.getTag();
			// switch (tag) {
			// case TAB_TAG_MENU:
			// // do nothing
			// break;
			// case TAB_TAG_DAY:
			// case TAB_TAG_NIGHT:
			// ft.remove(mFragment);
			// break;
			// case TAB_TAG_SOCIAL:
			// ft.remove(mFragment);
			// break;
			// default:
			// break;
			// }
			ft.remove(mFragment);
		}
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
	}
}
