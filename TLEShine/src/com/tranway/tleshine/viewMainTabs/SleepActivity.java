package com.tranway.tleshine.viewMainTabs;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tranway.telshine.database.DBInfo;
import com.tranway.telshine.database.DBManager;
import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ExerciseContent;
import com.tranway.tleshine.model.ExerciseContentAdapter;
import com.tranway.tleshine.model.UserInfoKeeper;
import com.tranway.tleshine.model.ExerciseUtils.Sport;
import com.tranway.tleshine.widget.RoundProgressBar;
import com.tranway.tleshine.widget.chartview.BarAdapter;
import com.tranway.tleshine.widget.chartview.BarChartView;
import com.tranway.tleshine.widget.chartview.BarData;

@SuppressLint("NewApi")
// !!!!!!!!!!!!!!!!!!
public class SleepActivity extends Activity {
	// private static final String TAG = DayFragment.class.getSimpleName();
	private static final int MSG_SCROLL_OVER = 0;
	private static final int MSG_SCROLL_BOTTOM = 1;
	private static final int MSG_SCROLL_TOP = 2;

	private static final float VIEWPAGE_HEIGHT_PERCENT = 0.5f;
	private static final long SECONDS_OF_ONE_DAY = 24 * 3600;

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

	private BarChartView mBarChart;
	private BarAdapter myAdapter;
	private List<BarData> data;
	private int pencent[];

	// private DBManager dbManager = new
	// DBManager(MyApplication.getAppContext());

	private ArrayList<ExerciseContent> mContentList = new ArrayList<ExerciseContent>();
	// private ArrayList<DailyExercise> mList = new ArrayList<DailyExercise>();

	private List<Map<String, Object>> mSleepData = new ArrayList<Map<String,Object>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep);
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
		initView();
	}

	private void initView() {
		Rect rect = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusHeight = rect.top + (int) getResources().getDimension(R.dimen.activity_title_height);
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int displayWidth = displayMetrics.widthPixels;
		int displayHeight = displayMetrics.heightPixels;

		mScrollView = (ScrollView) findViewById(R.id.scrollview);

		initViewPagerLayout(displayWidth, displayHeight);
//		initChatLayout(displayWidth, displayHeight, statusHeight);
//		initContentLayout();
	}

	private void initViewPagerLayout(int displayWidth, int displayHeight) {
		long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1l);
		mSleepData = DBManager.queryAllSleepInfos(userId);
		if (mSleepData.size() <= 0) {
			return;
		}
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mAdapter = new ViewPagerAdapter(this, mViewPager, mSleepData);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setPageMargin(10);
		mViewPager.setOffscreenPageLimit(3);
		// 设置ViewPager的width和height，width = 屏幕宽度*2/3，height = 屏幕高度*3/5
		ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(displayWidth * 2 / 3,
				(int) (displayHeight * VIEWPAGE_HEIGHT_PERCENT));
		mViewPager.setLayoutParams(params);
		mViewPager.setCurrentItem(mSleepData.size() - 1);
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

	private void initChatLayout(int displayWidth, int displayHeight, int statusHeight) {
		mChartLayout = (LinearLayout) findViewById(R.id.layout_chart);
		mBarChart = (BarChartView) findViewById(R.id.barchart);
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

		int charHeight = 0;
		ViewGroup.LayoutParams chartParams = mBarChart.getLayoutParams();
		chartParams.width = displayWidth - 40;
		charHeight = displayHeight / 4 + 20;
		chartParams.height = charHeight;
		mBarChart.setLayoutParams(chartParams);

		myAdapter = new BarAdapter(this, displayWidth - 20, charHeight, 12 * 4);
		pencent = new int[24 * 12];
		for (int i = 0; i < 24 * 12; i++) {
			if (i > 200) {
				pencent[i] = 100;
			} else if (i > 80 && i <= 200) {
				pencent[i] = 240;
			} else if (i > 20 && i <= 80) {
				pencent[i] = 140;
			} else {
				pencent[i] = 250;
			}
		}
		data = new ArrayList<BarData>();
		for (int i = 0; i < pencent.length; i++) {
			BarData model = new BarData();
			model.setPencent(((float) pencent[i] / (float) (256)));
			data.add(model);
		}
		myAdapter.setData(data);
		mBarChart.setAdapter(myAdapter);
	}

	private void initContentLayout() {
		mListView = (ListView) findViewById(R.id.list_content);

		// for test
		ExerciseContent content = new ExerciseContent(11, 12, Sport.WALK, 200);
		mContentList.add(content);
		content = new ExerciseContent(12, 13, Sport.WALK, 400);
		mContentList.add(content);
		content = new ExerciseContent(13, 14, Sport.RUN, 500);
		mContentList.add(content);
		content = new ExerciseContent(14, 15, Sport.SWIM, 300);
		mContentList.add(content);
		content = new ExerciseContent(15, 16, Sport.SWIM, 200);
		mContentList.add(content);
		content = new ExerciseContent(16, 17, Sport.SWIM, 200);
		mContentList.add(content);

		mContentAdapter = new ExerciseContentAdapter(this);
//		mContentAdapter.setContentList(mContentList);
		mListView.setAdapter(mContentAdapter);
		mContentAdapter.notifyDataSetChanged();
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
	public class ViewPagerAdapter extends PagerAdapter {

		private LayoutInflater mInflater;
		// private ArrayList<DailyExercise> mList;
		private List<Map<String, Object>> mSleepDatas;
		private int position;

		public ViewPagerAdapter(Context context, ViewPager mPager,
		// ArrayList<DailyExercise> mList) {
				List<Map<String, Object>> mList) {
			mInflater = LayoutInflater.from(context);
			this.mSleepDatas = mList;
		}

		class ViewHolder {
			RoundProgressBar mProgress;
			TextView mTimeTxt;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			this.position = position;
			ViewHolder holder = new ViewHolder();
			View view = mInflater.inflate(R.layout.layout_point, null);
			holder.mProgress = (RoundProgressBar) view.findViewById(R.id.progress);
			holder.mTimeTxt = (TextView) view.findViewById(R.id.txt_date);

			// DailyExercise ex = mList.get(position);
			// holder.mProgress.setProgress(ex.getAchieve(), ex.getGoal());
			//
			// SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.CHINA);
			// Date date = new Date(ex.getDate() * 1000);
			// holder.mTimeTxt.setText(df.format(date));
			Map<String, Object> info = mSleepDatas.get(position);
			long deepSleepTime = (Long) info.get(DBInfo.KEY_SLEEP_DEEP_TIME);
			long shallowSleepTime = (Long) info.get(DBInfo.KEY_SLEEP_SHALLOW_TIME);
			long sleepGoal = (Long) info.get(DBInfo.KEY_SLEEP_GOAL);
			holder.mProgress.setProgress(deepSleepTime, deepSleepTime + shallowSleepTime);
			long todayUtcTime = System.currentTimeMillis() / 1000 / SECONDS_OF_ONE_DAY;
			long sleepUtcTime = (Long) info.get(DBInfo.KEY_UTC_TIME);
			long utcTime = sleepUtcTime / SECONDS_OF_ONE_DAY;
			if (todayUtcTime == utcTime) {
				holder.mTimeTxt.setText(R.string.today);
			} else if (todayUtcTime - utcTime == 1) {
				holder.mTimeTxt.setText(R.string.yestoday);
			} else {
				SimpleDateFormat df = new SimpleDateFormat("MM-dd", Locale.getDefault());
				Date date = new Date(utcTime * 1000 * 3600 * 24);
				holder.mTimeTxt.setText(df.format(date));
			}
			container.addView(view);
			return view;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mSleepDatas.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object obj) {
			// container.removeView(mPager.findViewFromObject(position));
			((ViewPager) container).removeView((View) (obj));
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		public int getCurrentPosition() {
			return this.position;
		}
	}
}
