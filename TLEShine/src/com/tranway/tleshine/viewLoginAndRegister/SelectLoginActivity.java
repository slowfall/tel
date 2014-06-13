package com.tranway.tleshine.viewLoginAndRegister;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tranway.tleshine.R;
import com.tranway.tleshine.welcome.WelcomeActivity;

public class SelectLoginActivity extends Activity {
	private static final String TAG = SelectLoginActivity.class.getSimpleName();

	private ViewPager mPager;
	private LinearLayout mDotsLayout;

	private List<View> viewList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_login);
		setup();
	}

	private void setup() {
		findViewById(R.id.btn_login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.btn_register).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(intent);
			}
		});
		mPager = (ViewPager) findViewById(R.id.vp_guide);
		mDotsLayout = (LinearLayout) findViewById(R.id.ll_guide_dots);

		initPager();
		mPager.setAdapter(new ViewPagerAdapter(viewList));
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
					if (i == arg0) {
						mDotsLayout.getChildAt(i).setSelected(true);
					} else {
						mDotsLayout.getChildAt(i).setSelected(false);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void initPager() {
		viewList = new ArrayList<View>();
		int[] images = new int[] { R.drawable.new01, R.drawable.new02,
				R.drawable.new03, R.drawable.new04 };
		int[] texts = new int[] { R.drawable.new_text1, R.drawable.new_text2,
				R.drawable.new_text3, R.drawable.new_text4 };
		for (int i = 0; i < images.length; i++) {
			viewList.add(initView(images[i], texts[i]));
		}
		initDots(images.length);
	}

	private void initDots(int count) {
		for (int j = 0; j < count; j++) {
			mDotsLayout.addView(initDot());
		}
		mDotsLayout.getChildAt(0).setSelected(true);
	}

	private View initDot() {
		return LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.layout_dot, null);
	}

	private View initView(int res, int text) {
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.item_guide, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.iguide_img);
//		ImageView textview = (ImageView) view.findViewById(R.id.iguide_text);
		imageView.setImageResource(res);
//		textview.setImageResource(text);
		return view;
	}

	class ViewPagerAdapter extends PagerAdapter {

		private List<View> data;

		public ViewPagerAdapter(List<View> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			container.addView(data.get(position));
			return data.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(data.get(position));
		}

	}
}
