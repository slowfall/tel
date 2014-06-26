package com.tranway.Oband_Fitnessband.viewLoginAndRegister;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.MyApplication;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.viewMainTabs.MainActivity;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements OnClickListener {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] { "foo@example.com:hello",
			"bar@example.com:world" };
	private static final String LOGIN_END_URL = "/checklogin";
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private CheckBox mKeepSignInCheck;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		initTitleView();

		// Set up the login form.
		mEmail = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_EMAIL, "");
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPassword = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_PWD, "");
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(mPassword);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mKeepSignInCheck = (CheckBox) findViewById(R.id.cb_login_auto);

		boolean isKeepSignin = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_KEEP_SIGN_IN,
				false);
		mKeepSignInCheck.setChecked(isKeepSignin);

		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		mLoginFormView = findViewById(R.id.login_form);

		findViewById(R.id.tv_forgot_password).setOnClickListener(this);
		findViewById(R.id.btn_login).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		if (mKeepSignInCheck.isChecked()) {
			attemptLogin();
		}
		super.onResume();
	}

	private void initTitleView() {
		ImageButton leftBtn = (ImageButton) findViewById(R.id.btn_title_icon_left);
		// mPreBtn.setText(R.string.back);
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setOnClickListener(this);
		// Button mNextBtn = (Button) findViewById(R.id.btn_title_right);
		// mNextBtn.setText(R.string.next_step);
		// mNextBtn.setOnClickListener(this);
		// mNextBtn.setVisibility(View.VISIBLE);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.login);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_icon_left:
			finish();
			break;
		case R.id.btn_title_right:
			attemptLogin();
			break;
		case R.id.btn_login:
			attemptLogin();
			break;
		case R.id.tv_forgot_password:
			Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			request(mEmail, mPassword);
		}
	}

	private void request(final String email, final String password) {
		showProgress(true);
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				showProgress(false);
				try {
					JSONObject data = new JSONObject(result);
					if (data.has(TLEHttpRequest.STATUS_CODE)) {
						int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
						if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
							UserInfoKeeper.writeUserInfo(getApplicationContext(),
									UserInfoKeeper.KEY_EMAIL, email);
							UserInfoKeeper.writeUserInfo(getApplicationContext(),
									UserInfoKeeper.KEY_PWD, password);
							UserInfoKeeper.writeUserInfo(getApplicationContext(),
									UserInfoKeeper.KEY_KEEP_SIGN_IN, mKeepSignInCheck.isChecked());
							Intent intent = new Intent(MyApplication.getAppContext(),
									MainActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(intent);
						} else {
							// ToastHelper.showToast(R.string.error_incorrect_email_passowrd,
							// Toast.LENGTH_LONG);
							String errorMsg = data.getString(TLEHttpRequest.MSG);
							ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				showProgress(false);
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}
		}, this);
		// Map<String, String> data = new TreeMap<String, String>();
		// data.put(UserInfo.PASSWORD, password);
		httpRequest.get(LOGIN_END_URL + "/" + email + "/?password=" + password, null);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword);
				}
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
