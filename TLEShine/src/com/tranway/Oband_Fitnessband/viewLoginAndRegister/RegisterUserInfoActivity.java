package com.tranway.Oband_Fitnessband.viewLoginAndRegister;

import java.io.File;
import java.text.ParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.Tools;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserInfo;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.util.UserInfoUtils;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsUserBirthdayActivity;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsUserHighActivity;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsUserStrideActivity;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsUserWeightActivity;

public class RegisterUserInfoActivity extends Activity implements OnClickListener {
	private static final String TAG = RegisterUserInfoActivity.class.getSimpleName();

	private static final String UPDATE_END_URL = "/update";

	private TextView mNameTxt, mHighTxt, mWeightTxt, mBirthdayTxt, mStrideTxt, mUserNameTxt;
	private RadioGroup mSexGroup;
	private RadioButton mMaleRadio, mFemaleRadio;

	private UserInfo userInfo;
	private boolean isRegister = false;
	private TLEHttpRequest httpRequest = null;
	/* 组件 */
	private RelativeLayout switchAvatar;
	private ImageView faceImage;

	private String[] items = null;
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_user_info);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			isRegister = bundle.getBoolean("isRegister", false);
		}

		userInfo = UserInfoKeeper.readUserInfo(this);

		initView();
		updateUserInfoUI(userInfo);
		httpRequest = TLEHttpRequest.instance();
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			showDialog();
		}
	};

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this).setTitle(R.string.profile_picture)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Tools.hasSdcard()) {

								intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
										.fromFile(new File(Environment
												.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
							break;
						}
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 96);
		intent.putExtra("outputY", 96);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			String base64 = Tools.bitmapToBase64(photo);
			userInfo.setAvatar(base64);
			faceImage.setImageBitmap(photo);
		}
	}

	private void initView() {
		initTitleView();

		mUserNameTxt = (TextView) findViewById(R.id.txt_username);
		mNameTxt = (TextView) findViewById(R.id.txt_nikename);
		mNameTxt.setOnClickListener(this);
		mNameTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				mUserNameTxt.setText(arg0);
			}
		});
		mHighTxt = (TextView) findViewById(R.id.txt_height);
		mHighTxt.setOnClickListener(this);
		mWeightTxt = (TextView) findViewById(R.id.txt_weight);
		mWeightTxt.setOnClickListener(this);
		mBirthdayTxt = (TextView) findViewById(R.id.txt_birthday);
		mBirthdayTxt.setOnClickListener(this);
		mStrideTxt = (TextView) findViewById(R.id.txt_step);
		mStrideTxt.setOnClickListener(this);

		mMaleRadio = (RadioButton) findViewById(R.id.radio_male);
		mFemaleRadio = (RadioButton) findViewById(R.id.radio_female);
		mSexGroup = (RadioGroup) findViewById(R.id.group_sex);
		mSexGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if (arg1 == R.id.radio_female) {
					userInfo.setSex(UserInfo.SEX_FEMALE);
				} else {
					userInfo.setSex(UserInfo.SEX_MALE);
				}
			}
		});

		switchAvatar = (RelativeLayout) findViewById(R.id.switch_face_rl);
		faceImage = (ImageView) findViewById(R.id.face);
		items = new String[] { getString(R.string.select_picture), getString(R.string.take_picture) };
		// 设置事件监听
		switchAvatar.setOnClickListener(listener);
	}

	private void initTitleView() {
		Button mExsitBtn = (Button) findViewById(R.id.btn_title_left);
		mExsitBtn.setText(R.string.cancel);
		mExsitBtn.setVisibility(View.VISIBLE);
		mExsitBtn.setOnClickListener(this);
		Button mNextBtn = (Button) findViewById(R.id.btn_title_right);
		mNextBtn.setText(R.string.save);
		mNextBtn.setOnClickListener(this);
		mNextBtn.setVisibility(View.VISIBLE);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.my_info);
	}

	private void updateUserInfoUI(UserInfo userInfo) {
		if (userInfo == null) {
			return;
		}
		if (!TextUtils.isEmpty(userInfo.getName())) {
			mNameTxt.setText(userInfo.getName());
		}
		try {
			if (userInfo.getBirthday() >= 0) {
				String birth = UserInfoUtils.convertDateToBirthday(userInfo.getBirthday());
				mBirthdayTxt.setText(birth);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (userInfo.getHeight() >= 0) {
			mHighTxt.setText(UserInfoUtils.convertHighToString(userInfo.getHeight()));
		}
		if (userInfo.getWeight() >= 0) {
			mWeightTxt.setText(UserInfoUtils.convertWeightToString(userInfo.getWeight()));
		}
		if (userInfo.getStride() >= 0) {
			mStrideTxt.setText(userInfo.getStride() + " CM");
		}
		Log.d(TAG, "sex: " + userInfo.getSex());
		if (userInfo.getSex() == UserInfo.SEX_MALE) {
			mMaleRadio.setChecked(true);
			mFemaleRadio.setChecked(false);
		} else {
			mFemaleRadio.setChecked(true);
			mMaleRadio.setChecked(false);
		}
		if (userInfo.getAvatar() != null) {
			Bitmap photo = Tools.base64ToBitmap(userInfo.getAvatar());
			faceImage.setImageBitmap(photo);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_title_right:
			onNextButtonClick();
			break;
		case R.id.txt_height:
			Intent intent1 = new Intent(this, SettingsUserHighActivity.class);
			startActivityForResult(intent1, SettingsUserHighActivity.REQUEST_CODE);
			break;
		case R.id.txt_weight:
			Intent intent2 = new Intent(this, SettingsUserWeightActivity.class);
			startActivityForResult(intent2, SettingsUserWeightActivity.REQUEST_CODE);
			break;
		case R.id.txt_birthday:
			Intent intent3 = new Intent(this, SettingsUserBirthdayActivity.class);
			startActivityForResult(intent3, SettingsUserBirthdayActivity.REQUEST_CODE);
			break;
		case R.id.txt_step:
			Intent intent4 = new Intent(this, SettingsUserStrideActivity.class);
			startActivityForResult(intent4, SettingsUserStrideActivity.REQUEST_CODE);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(Environment.getExternalStorageDirectory()
							+ IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(RegisterUserInfoActivity.this, R.string.can_not_find_sd_card,
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}

		if (data == null) {
			return;
		}
		switch (requestCode) {
		case SettingsUserHighActivity.REQUEST_CODE:
			int high = data.getIntExtra(SettingsUserHighActivity.RESPONSE_NAME_VALUE, -1);
			Log.d(TAG, "get wheel view high=" + high);
			userInfo.setHeight(high);
			mHighTxt.setText(UserInfoUtils.convertHighToString(high));
			break;
		case SettingsUserWeightActivity.REQUEST_CODE:
			int weight = data.getIntExtra(SettingsUserWeightActivity.RESPONSE_NAME_VALUE, -1);
			userInfo.setWeight(weight);
			mWeightTxt.setText(UserInfoUtils.convertWeightToString(weight));
			break;
		case SettingsUserBirthdayActivity.REQUEST_CODE:
			long time = data.getLongExtra(SettingsUserBirthdayActivity.RESPONSE_NAME_VALUE, -1L);
			userInfo.setBirthday(time);
			try {
				String text = UserInfoUtils.convertDateToBirthday(time);
				mBirthdayTxt.setText(text);
				int age = UserInfoUtils.convertDateToAge(time);
				userInfo.setAge(age);
			} catch (ParseException e) {
				e.printStackTrace();
				ToastHelper.showToast(R.string.parse_date_exception);
			}
			break;
		case SettingsUserStrideActivity.REQUEST_CODE:
			int stride = data.getIntExtra(SettingsUserStrideActivity.RESPONSE_NAME_VALUE, -1);
			userInfo.setStride(stride);
			if (stride >= 0) {
				mStrideTxt.setText(stride + " CM");
			}
			break;
		default:
			break;
		}
	}

	private void onNextButtonClick() {
		if (userInfo == null) {
			return;
		}
		String name = mNameTxt.getText().toString();
		if (TextUtils.isEmpty(name)) {
			ToastHelper.showToast(R.string.prompt_name);
			return;
		} else {
			if (name.length() < 4 || name.length() > 20) {
				ToastHelper.showToast(getResources().getString(R.string.name_length_invalid));
				return;
			}
			if (!checkNameAvailable(name)) {
				ToastHelper.showToast(getResources().getString(R.string.name_invalid));
				return;
			}
			userInfo.setName(name);
		}
		if (userInfo.getBirthday() <= 0) {
			ToastHelper.showToast(R.string.prompt_birthday);
			return;
		} else if (userInfo.getHeight() <= 0) {
			ToastHelper.showToast(R.string.prompt_high);
			return;
		} else if (userInfo.getWeight() <= 0) {
			ToastHelper.showToast(R.string.prompt_weight);
			return;
		} else if (userInfo.getStride() <= 0) {
			ToastHelper.showToast(R.string.prompt_stride);
			return;
		} else {
			if (isRegister) {
				UserInfoKeeper.writeUserInfo(this, userInfo);
				Intent intent = new Intent(this, RegisterUserGoalActivity.class);
				startActivity(intent);
			} else {
				syncUserInfoToServer(userInfo);
			}
		}
	}

	/**
	 * Synchronous user information to the server
	 * 
	 * @param userInfo
	 *            User detail information
	 */
	private void syncUserInfoToServer(final UserInfo userInfo) {
		if (userInfo == null) {
			return;
		}
		Map<String, String> params = UserInfoUtils.convertUserInfoToParamsMap(userInfo, true);
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				// TODO Auto-generated method stub
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}

			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
					if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
						UserInfoKeeper.writeUserInfo(RegisterUserInfoActivity.this, userInfo);
						if (!isRegister) {
							ShowGotoSettingsDialog();
						}
					} else {
						String errorMsg = data.getString(TLEHttpRequest.MSG);
						ToastHelper.showToast(errorMsg, Toast.LENGTH_LONG);
						Log.e(TAG, "sync userinfo failed");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, this);
		httpRequest.post(UPDATE_END_URL + "/", params);
	}

	private boolean checkNameAvailable(String name) {
		if (name == null) {
			return false;
		}
		String regEx = "^[\\w\u3E00-\u9FA5]+$";
		// String regEx = "^[0-9a-zA-Z_\u3E00-\u9FA5]+$";
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(name);
		return matcher.matches();
	}

	private void ShowGotoSettingsDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_confirm, null);
		final Dialog dialog = new Dialog(this, R.style.DialogTheme);
		TextView title = (TextView) dialogView.findViewById(R.id.layout_content);
		title.setText(getResources().getString(R.string.go_to_settings_dialog_tips));
		dialog.setContentView(dialogView);
		Button positiveBtn = (Button) dialogView.findViewById(R.id.positive);
		positiveBtn.setText(R.string.positive);
		positiveBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_OK);
				dialog.dismiss();
				finish();
			}
		});
		Button negativeBtn = (Button) dialogView.findViewById(R.id.negative);
		negativeBtn.setText(R.string.negative);
		negativeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				dialog.dismiss();
				finish();
			}
		});
		dialog.show();
	}

}
