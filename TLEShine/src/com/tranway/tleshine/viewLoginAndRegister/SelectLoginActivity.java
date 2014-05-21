package com.tranway.tleshine.viewLoginAndRegister;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tranway.tleshine.R;
import com.tranway.tleshine.model.AccessTokenKeeper;
import com.tranway.tleshine.model.Constants;

public class SelectLoginActivity extends Activity {
	private static final String TAG = SelectLoginActivity.class.getSimpleName();
	/** 微博 Web 授权类，提供登陆等功能 */
	private WeiboAuth mWeiboAuth;

	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
	private Oauth2AccessToken mAccessToken;

	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;

	private ViewPager mPager;
	private LinearLayout mDotsLayout;

	private List<View> viewList;

	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_login);

		setup(savedInstanceState);
	}

	private void setup(Bundle savedInstanceState) {
		// 创建微博实例
		mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY,
				Constants.REDIRECT_URL, Constants.SCOPE);

		// SSO 授权
		findViewById(R.id.btn_weibo_login).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						mSsoHandler = new SsoHandler(SelectLoginActivity.this,
								mWeiboAuth);
						mSsoHandler.authorize(new AuthListener());
					}
				});

		// 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
		// 第一次启动本应用，AccessToken 不可用
		mAccessToken = AccessTokenKeeper.readAccessToken(this);
		if (mAccessToken.isSessionValid()) {
			// TODO AccessToken
		}

		// FaceBook login
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(statusCallback));
			}
		}

		findViewById(R.id.btn_facebook_login).setOnClickListener(
				new OnClickListener() {
					public void onClick(View view) {
						onClickLogin();
					}
				});
		
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
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	/**
	 * 当 SSO 授权 Activity 退出时，该函数被调用。
	 * 
	 * @see {@link Activity#onActivityResult}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {

				// 保存 Token 到 SharedPreferences
				AccessTokenKeeper.writeAccessToken(SelectLoginActivity.this,
						mAccessToken);
				Toast.makeText(SelectLoginActivity.this,
						R.string.toast_auth_success, Toast.LENGTH_SHORT).show();
			} else {
				String code = values.getString("code");
				String message = getString(R.string.toast_auth_failed);
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(SelectLoginActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onCancel() {
			Toast.makeText(SelectLoginActivity.this,
					R.string.toast_auth_canceled, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(SelectLoginActivity.this,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this)
					.setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
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
		ImageView textview = (ImageView) view.findViewById(R.id.iguide_text);
		imageView.setImageResource(res);
		textview.setImageResource(text);
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
