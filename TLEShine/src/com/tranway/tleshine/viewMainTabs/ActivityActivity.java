package com.tranway.tleshine.viewMainTabs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.tranway.telshine.database.DBInfo;
import com.tranway.telshine.database.DBManager;
import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ActivityInfo;
import com.tranway.tleshine.model.ExerciseContentAdapter;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.model.ViewPagerAdapter;
import com.tranway.tleshine.widget.chartview.ChartView;
import com.tranway.tleshine.widget.chartview.LabelAdapter;
import com.tranway.tleshine.widget.chartview.LabelAdapter.LabelOrientation;
import com.tranway.tleshine.widget.chartview.LinearSeries;
import com.tranway.tleshine.widget.chartview.LinearSeries.LinearPoint;

@SuppressLint("NewApi")
// !!!!!!!!!!!!!!!!!!
public class ActivityActivity extends Activity {
	// private static final String TAG = DayFragment.class.getSimpleName();
	private static final int MSG_SCROLL_OVER = 0;
	private static final int MSG_SCROLL_BOTTOM = 1;
	private static final int MSG_SCROLL_TOP = 2;
	private static final long ONE_DAY_SECONDS = 2400 * 36;
	private static final float VIEWPAGE_HEIGHT_PERCENT = 0.5f;

	// private JazzyViewPager mPager;
	private ViewPager mViewPager;
	private Button mScrollBtn;
	private LinearLayout mPagerLayout, mChartLayout;
	private ScrollView mScrollView;
	private ListView mListView;
	private ExerciseContentAdapter mContentAdapter;
	private boolean isScrolling = false;
	private boolean isInTop = true;
	private ViewPagerAdapter mAdapter;

	// private DBManager dbManager = new
	// DBManager(MyApplication.getAppContext());

//	private ArrayList<ExerciseContent> mContentList = new ArrayList<ExerciseContent>();
	private List<Map<String, Object>> mEvery15MinPackets = new ArrayList<Map<String, Object>>();
	// private ArrayList<DailyExercise> mList = new ArrayList<DailyExercise>();

	private List<ActivityInfo> mActivityInfos = new ArrayList<ActivityInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity);
		// ArrayList<DailyExercise> exList = new ArrayList<DailyExercise>();
		// for (int i = 0; i <= 6; i++) {
		// DailyExercise mExercise = new DailyExercise();
		// mExercise.setDate(83834387L); // !!!
		// mExercise.setGoal(1000);
		// mExercise.setAchieve(800);
		// exList.add(mExercise);
		// }
		//
		// long ret = dbManager.addDailyExerciseInfo(exList);
		// if (ret != -1) {
		// mList.addAll(dbManager.queryDailyExerciseInfo());
		// // for (DailyExercise ex : mList) {
		// // Log.d("------", "date : " + ex.getDate());
		// // }
		// }

		long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1);
		mActivityInfos = DBManager.queryActivityInfo(userId);
		ActivityInfo activityInfo = new ActivityInfo();
		activityInfo.setCalorie(100);
		activityInfo.setDistance(1000);
		activityInfo.setSteps(100);
		activityInfo.setUtcTime(System.currentTimeMillis() * 1000);
		mActivityInfos.add(activityInfo);

	}

	@Override
	public void onResume() {
		super.onResume();
		initView();
	}

	private void initView() {
		Rect rect = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusHeight = rect.top
				+ (int) getResources().getDimension(R.dimen.activity_title_height);
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int displayWidth = displayMetrics.widthPixels;
		int displayHeight = displayMetrics.heightPixels;

		mScrollView = (ScrollView) findViewById(R.id.scrollview);

		initViewPagerLayout(displayWidth, displayHeight);
		initContentLayout();
		initChatLayout(displayWidth, displayHeight, statusHeight);
	}

	private void initViewPagerLayout(int displayWidth, int displayHeight) {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mAdapter = new ViewPagerAdapter(this, mViewPager, mActivityInfos);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setPageMargin(10);
		mViewPager.setOffscreenPageLimit(3);
		// 设置ViewPager的width和height，width = 屏幕宽度*2/3，height = 屏幕高度*3/5
		ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(displayWidth * 2 / 3,
				(int) (displayHeight * VIEWPAGE_HEIGHT_PERCENT));
		mViewPager.setLayoutParams(params);
		mViewPager.setCurrentItem(mActivityInfos.size() - 1);
		mPagerLayout = (LinearLayout) findViewById(R.id.layout_viewpager);
		MyOnPageChangeListener pageChangeListener = new MyOnPageChangeListener();
		mViewPager.setOnPageChangeListener(pageChangeListener);
		mPagerLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mViewPager.dispatchTouchEvent(event);
			}
		});
	}

	private void initContentLayout() {
		mListView = (ListView) findViewById(R.id.list_content);

		// // for test
		// ExerciseContent content = new ExerciseContent(11, 12, Sport.WALK,
		// 200);
		// mContentList.add(content);
		// content = new ExerciseContent(12, 13, Sport.WALK, 400);
		// mContentList.add(content);
		// content = new ExerciseContent(13, 14, Sport.RUN, 500);
		// mContentList.add(content);
		// content = new ExerciseContent(14, 15, Sport.SWIM, 300);
		// mContentList.add(content);
		// content = new ExerciseContent(15, 16, Sport.SWIM, 200);
		// mContentList.add(content);
		// content = new ExerciseContent(16, 17, Sport.SWIM, 200);
		// mContentList.add(content);
		long utcTime = System.currentTimeMillis() / 1000;
		long dayUtc = utcTime / ONE_DAY_SECONDS;
		long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1);
		mEvery15MinPackets = DBManager.queryEvery15MinPackets(userId, dayUtc * ONE_DAY_SECONDS, (dayUtc + 1)
				* ONE_DAY_SECONDS);
//		//TODO test
		for (int i = 0; i < 14; i++) {
			Map<String, Object> map = new TreeMap<String, Object>();
			map.put(DBInfo.KEY_UTC_TIME, utcTime - 3600 * i);
			map.put(DBInfo.KEY_CALORIE, 10 * i + 1);
			map.put(DBInfo.KEY_STEPS, 100 * i + 10);
			mEvery15MinPackets.add(map);
		}
		mContentAdapter = new ExerciseContentAdapter(this);
		mContentAdapter.setContentList(mEvery15MinPackets);
		mListView.setAdapter(mContentAdapter);
		mContentAdapter.notifyDataSetChanged();
	}

	private void initChatLayout(int displayWidth, int displayHeight, int statusHeight) {
		mChartLayout = (LinearLayout) findViewById(R.id.layout_chart);
		mScrollBtn = (Button) findViewById(R.id.btn_scroll);
		mScrollBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isScrolling) {
					if (isInTop) {
						scrollToBottom();
					} else {
						scrollToTop();
					}
				}
			}
		});

		ViewGroup.LayoutParams chartLayoutParams = mChartLayout.getLayoutParams();
		chartLayoutParams.width = displayWidth;
		chartLayoutParams.height = displayHeight - statusHeight;
		mChartLayout.setLayoutParams(chartLayoutParams);

		ChartView chartView = (ChartView) findViewById(R.id.chart_view);
		ViewGroup.LayoutParams chartParams = chartView.getLayoutParams();
		chartParams.width = displayWidth - 40;
		chartParams.height = displayHeight / 4 + 20;
		Log.d("-----------", "height = " + chartParams.height);
		chartView.setLayoutParams(chartParams);
		chartView.setGridLinesHorizontal(3);
		chartView.setGridLinesVertical(0);
		LinearSeries series = new LinearSeries();
		series.setLineColor(getResources().getColor(R.color.yellow));
		series.setLineWidth(5);
		series.addPoint(new LinearPoint(0, 0));
		series.addPoint(new LinearPoint(6, 0));
		series.addPoint(new LinearPoint(12, 0));
		series.addPoint(new LinearPoint(18, 0));
//		series.addPoint(new LinearPoint(24, 0));
		for (Map<String, Object> every15MinPacket : mEvery15MinPackets) {
			long utcTime = (Long) every15MinPacket.get(DBInfo.KEY_UTC_TIME);
			int step = (Integer) every15MinPacket.get(DBInfo.KEY_STEPS);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(utcTime * 1000);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
//			Util.logD(getTag(), "hour:" + hour + ", step:" + step);
			series.addPoint(new LinearPoint(hour, step));
		}
		String[] labels = { "0h", "6h", "12h", "18h", "24h" };
		// Add chart view data
		chartView.addSeries(series);
		LabelAdapter mAdapter = new LabelAdapter(this,LabelOrientation.HORIZONTAL);
		mAdapter.setLabelValues(labels);
		chartView.setBottomLabelAdapter(mAdapter);
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// to refresh frameLayout
			if (mPagerLayout != null) {
				mPagerLayout.invalidate();
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private void scrollToTop() {
		mHandler.post(toTopRunnable);
		isScrolling = true;
		isInTop = true;
	}

	private void scrollToBottom() {
		mHandler.post(toBottomRunnable);
		isScrolling = true;
		isInTop = false;
	}

	Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == MSG_SCROLL_BOTTOM) {
				mHandler.postDelayed(toBottomRunnable, 10);
			} else if (msg.what == MSG_SCROLL_TOP) {
				mHandler.postDelayed(toTopRunnable, 10);
			} else if (msg.what == MSG_SCROLL_OVER) {
				isScrolling = false;
			}
			return false;
		}
	});

	// Scroll to bottom Runnable
	private Runnable toBottomRunnable = new Runnable() {
		public void run() {
			mScrollView.scrollTo(0, mScrollView.getScrollY() + 20);
			// TODO .. needs to add main tab height
			if (mScrollView.getScrollY() < mPagerLayout.getMeasuredHeight()) {
				mHandler.sendEmptyMessage(MSG_SCROLL_BOTTOM);
			} else {
				mHandler.sendEmptyMessage(MSG_SCROLL_OVER);
			}
		}
	};

	// Scroll to top Runnable
	private Runnable toTopRunnable = new Runnable() {
		public void run() {
			mScrollView.scrollTo(0, mScrollView.getScrollY() - 20);
			if (mScrollView.getScrollY() > 0) {
				mHandler.sendEmptyMessage(MSG_SCROLL_TOP);
			} else {
				mHandler.sendEmptyMessage(MSG_SCROLL_OVER);
			}
		}
	};

}
