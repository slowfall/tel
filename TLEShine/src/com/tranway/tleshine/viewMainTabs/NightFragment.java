package com.tranway.tleshine.viewMainTabs;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.tranway.telshine.database.DBManager;
import com.tranway.tleshine.R;
import com.tranway.tleshine.model.ActivityInfo;
import com.tranway.tleshine.model.ExerciseContent;
import com.tranway.tleshine.model.ExerciseContentAdapter;
import com.tranway.tleshine.model.ExerciseUtils.Sport;
import com.tranway.tleshine.model.ViewPagerAdapter;
import com.tranway.tleshine.widget.chartview.BarAdapter;
import com.tranway.tleshine.widget.chartview.BarChartView;
import com.tranway.tleshine.widget.chartview.BarData;

@SuppressLint("NewApi")
// !!!!!!!!!!!!!!!!!!
public class NightFragment extends Fragment {
	// private static final String TAG = DayFragment.class.getSimpleName();
	private static final int MSG_SCROLL_OVER = 0;
	private static final int MSG_SCROLL_BOTTOM = 1;
	private static final int MSG_SCROLL_TOP = 2;

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

	private BarChartView mBarChart;
	private BarAdapter myAdapter;
	private List<BarData> data;
	private int pencent[];

	// private DBManager dbManager = new
	// DBManager(MyApplication.getAppContext());

	private ArrayList<ExerciseContent> mContentList = new ArrayList<ExerciseContent>();
	// private ArrayList<DailyExercise> mList = new ArrayList<DailyExercise>();

	private List<ActivityInfo> mActivityInfos = new ArrayList<ActivityInfo>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_night, container, false);
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
		mActivityInfos = DBManager.queryActivityInfo();
		initView(v);

		return v;
	}

	private void initView(View v) {
		Rect rect = new Rect();
		getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusHeight = rect.top + (int) getResources().getDimension(R.dimen.activity_title_height);
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int displayWidth = displayMetrics.widthPixels;
		int displayHeight = displayMetrics.heightPixels;

		mScrollView = (ScrollView) v.findViewById(R.id.scrollview);

		initViewPagerLayout(v, displayWidth, displayHeight);
		initChatLayout(v, displayWidth, displayHeight, statusHeight);
		initContentLayout(v);
	}

	private void initViewPagerLayout(View v, int displayWidth, int displayHeight) {
		mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
		mAdapter = new ViewPagerAdapter(getActivity(), mViewPager, mActivityInfos);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setPageMargin(10);
		mViewPager.setOffscreenPageLimit(3);
		// 设置ViewPager的width和height，width = 屏幕宽度*2/3，height = 屏幕高度*3/5
		ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(displayWidth * 2 / 3,
				(int) (displayHeight * VIEWPAGE_HEIGHT_PERCENT));
		mViewPager.setLayoutParams(params);
		mViewPager.setCurrentItem(mActivityInfos.size() - 1);
		mPagerLayout = (LinearLayout) v.findViewById(R.id.layout_viewpager);
		MyOnPageChangeListener pageChangeListener = new MyOnPageChangeListener();
		mViewPager.setOnPageChangeListener(pageChangeListener);
		mPagerLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mViewPager.dispatchTouchEvent(event);
			}
		});
	}

	private void initChatLayout(View v, int displayWidth, int displayHeight, int statusHeight) {
		mChartLayout = (LinearLayout) v.findViewById(R.id.layout_chart);
		mBarChart = (BarChartView) v.findViewById(R.id.barchart);
		mScrollBtn = (Button) v.findViewById(R.id.btn_scroll);
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

		myAdapter = new BarAdapter(getActivity(), displayWidth - 20, charHeight, 12 * 4);
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

	private void initContentLayout(View v) {
		mListView = (ListView) v.findViewById(R.id.list_content);

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

		mContentAdapter = new ExerciseContentAdapter(getActivity());
		mContentAdapter.setContentList(mContentList);
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

}
