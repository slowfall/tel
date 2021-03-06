package com.tranway.Oband_Fitnessband.viewMainTabs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.Oband_Fitnessband.R;
import com.tranway.Oband_Fitnessband.model.ActivityInfo;
import com.tranway.Oband_Fitnessband.model.BLEPacket;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserGoalKeeper;
import com.tranway.Oband_Fitnessband.model.UserInfo;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.model.Util;
import com.tranway.Oband_Fitnessband.util.UserInfoUtils;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsActivity;
import com.tranway.Oband_Fitnessband.viewSettings.SettingsGoalActivity;
import com.tranway.Oband_Fitnessband.widget.CustomizedProgressDialog;
import com.tranway.tleshine.bluetooth.RBLService;
import com.tranway.tleshine.database.DBInfo;
import com.tranway.tleshine.database.DBManager;

@SuppressLint("NewApi")
public class MenuActivity extends Activity implements OnClickListener {
	private static final String TAG = MenuActivity.class.getSimpleName();
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 15000;
	private static final String CHECK_DEVICE_END_URL = "/CheckDevice";
	private static final String ADD_SPORT_POINT_END_URL = "/AddSportPoint";
	private static final String UPDATE_USER_INFO_END_URL = "/update";

	// private boolean connState = false;
	private BluetoothGattCharacteristic characteristicTx = null;
	private RBLService mBluetoothLeService;
	private BluetoothAdapter mBluetoothAdapter;
	private String mDeviceAddress;
	private List<byte[]> mEvery15MinPackect = new ArrayList<byte[]>();
	private List<byte[]> mSleepPakect = new ArrayList<byte[]>();
	private CustomizedProgressDialog mConnectAndSyncDialog;
	private Map<String, String> mDeviceAddressAndName = new TreeMap<String, String>();
	private String mDeviceID = null;

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((RBLService.LocalBinder) service).getService();
			Util.logD(TAG, "onServiceConnected(), mBluetoothLeService:" + mBluetoothLeService);
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				// finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
				// ToastHelper.showToast(R.string.disconnected);
				dismissConnectAndSyncDialog(R.string.syncing);
				// setButtonDisable();
			} else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				ToastHelper.showToast(R.string.connected);
				showConnectAndSyncDialog(R.string.syncing);
				getGattService(mBluetoothLeService.getSupportedGattService());
			} else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
				byte[] data = intent.getByteArrayExtra(RBLService.EXTRA_DATA);
				handleBLEData(data);
			} else if (RBLService.ACTION_GATT_RSSI.equals(action)) {
				// displayData(intent.getStringExtra(RBLService.EXTRA_DATA));
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		Util.logD(TAG, "mBluetoothLeService:" + mBluetoothLeService);
		mDeviceID = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_DEVICE_ID, null);
		initView();
		// checkBLE();
		Intent gattServiceIntent = new Intent(this, RBLService.class);
		getApplicationContext()
				.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onResume() {
		super.onResume();

		Util.logD(TAG, "on Resume");
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(mGattUpdateReceiver);
		if (mServiceConnection != null) {
			getApplicationContext().unbindService(mServiceConnection);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				if (checkBLE()) {
					scanAndConnectDevice();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean checkBLE() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			// Toast.makeText(this, R.string.ble_not_supported,
			// Toast.LENGTH_LONG).show();
			ShowBleNotSupportDialog();
			mServiceConnection = null;
			return false;
		}

		final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			// Toast.makeText(this, R.string.ble_not_supported,
			// Toast.LENGTH_LONG).show();
			ShowBleNotSupportDialog();
			mServiceConnection = null;
			return false;
		}

		if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return false;
		}

		return true;
	}

	private void getGattService(BluetoothGattService gattService) {
		if (gattService == null)
			return;

		// setButtonEnable();
		// startReadRssi();

		characteristicTx = gattService.getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);

		BluetoothGattCharacteristic characteristicRx = gattService
				.getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
		mBluetoothLeService.setCharacteristicNotification(characteristicRx, true);
		mBluetoothLeService.readCharacteristic(characteristicRx);
	}

	private void handleBLEData(final byte[] data) {
		final BLEPacket packet = new BLEPacket();
		byte sequenceMask = (byte) 0xf0;
		byte typeMask = (byte) 0x0f;
		int type = data[0] & typeMask;
		byte[] ack;
		byte sequenceNumber = (byte) (((data[0] & sequenceMask) >> 4) & 0x0f);
		switch (type) {
		// User Info command
		case 0x01:
			if (packet.checkChecksum(data)) {
				UserInfo userInfo = UserInfoKeeper.readUserInfo(getApplicationContext());
				byte[] info = packet.makeUserInfoForWrite(true, sequenceNumber, userInfo);
				characteristicTx.setValue(info);
				mBluetoothLeService.writeCharacteristic(characteristicTx);
			}
			break;
		// UTC command
		case 0x02:
			if (packet.checkChecksum(data)) {
				long getUtcTime = packet.resolveUTCTime(data);

				Calendar calendar = Calendar.getInstance();
				TimeZone zone = calendar.getTimeZone();
				long offset = zone.getOffset(calendar.getTimeInMillis()) / 1000;
				long utcTime = calendar.getTimeInMillis() / 1000 + offset;
				Util.logD(TAG, "Time Zone offset:" + offset);
				// boolean isUpdateUtc = (Math.abs(utcTime - getUtcTime) > 5);
				boolean isUpdateUtc = true;
				byte[] utc = packet.makeUTCForWrite(isUpdateUtc, sequenceNumber, utcTime);
				characteristicTx.setValue(utc);
				Util.logD(TAG, "isUpdateUtc:" + isUpdateUtc + ", getUtcTime:" + getUtcTime
						+ ", utcTime:" + utcTime);
				mBluetoothLeService.writeCharacteristic(characteristicTx);
			}
			break;
		// total steps and calories since last sync.
		case ActivityInfo.COMMAND:
			if (packet.checkChecksum(data)) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						saveActivityInfo(data);
					}
				}).start();
				ack = packet.makeReplyACK(sequenceNumber);
				characteristicTx.setValue(ack);
				mBluetoothLeService.writeCharacteristic(characteristicTx);
			}
			break;
		case 0x04:
			if (packet.checkChecksum(data)) {
				mEvery15MinPackect.add(data);
				if (data[1] == (byte) 0x03) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							saveEvery15MinPacket(mEvery15MinPackect);
						}
					}).start();
					ack = packet.makeReplyACK(sequenceNumber);
					characteristicTx.setValue(ack);
					mBluetoothLeService.writeCharacteristic(characteristicTx);
				}
			}
			break;
		case 0x05:
			if (packet.checkChecksum(data)) {
				mSleepPakect.add(data);
				if (data[1] == (byte) 0x03) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							saveSleepPacket(mSleepPakect);
						}
					}).start();
					ack = packet.makeReplyACK(sequenceNumber);
					characteristicTx.setValue(ack);
					mBluetoothLeService.writeCharacteristic(characteristicTx);
				}
			}
			break;
		case 0x06:
			mBluetoothLeService.disconnect();
			long time = System.currentTimeMillis();
			UserInfoKeeper.writeUserInfo(getApplicationContext(),
					UserInfoKeeper.KEY_SYNC_BLUETOOTH_TIME, time);
			ToastHelper.showToast(R.string.sync_finished, Toast.LENGTH_LONG);
			dismissConnectAndSyncDialog(R.string.syncing);
			break;
		default:
			ack = packet.makeReplyACK(sequenceNumber);
			characteristicTx.setValue(ack);
			mBluetoothLeService.writeCharacteristic(characteristicTx);
			break;
		}
	}

	private void saveActivityInfo(byte[] byteData) {
		BLEPacket packet = new BLEPacket();
		ActivityInfo activityInfo = packet.resolveCurrentActivityInfo(byteData);
		if (activityInfo.getSteps() > 0 || activityInfo.getCalorie() > 0
				|| activityInfo.getDistance() > 0) {
			long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1l);
			int goal = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_STEPSTARGET, 0);
			// int goal = UserGoalKeeper.readExerciseGoalPoint(this);
			activityInfo.setGoal(goal);
			DBManager.addActivityInfo(userId, activityInfo);

			TLEHttpRequest request = TLEHttpRequest.instance();
			request.setOnHttpRequestListener(null, null);
			Map<String, String> data = new TreeMap<String, String>();
			data.put("StepCount", String.valueOf(activityInfo.getSteps()));
			data.put("UserID", String.valueOf(UserInfoKeeper.readUserInfo(getApplicationContext(),
					UserInfoKeeper.KEY_ID, 0l)));
			data.put("CreateDate", String.valueOf(System.currentTimeMillis() / 1000));
			request.post(ADD_SPORT_POINT_END_URL, data);
		}
	}

	private void saveEvery15MinPacket(List<byte[]> every15MinPacket) {
		BLEPacket blePacket = new BLEPacket();
		List<Map<String, Object>> every15MinDatas = blePacket
				.resolveEvery15MinPacket(every15MinPacket);
		long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1l);
		DBManager.addEvery15MinData(userId, every15MinDatas);
	}

	private void saveSleepPacket(List<byte[]> sleepPacket) {
		BLEPacket blePacket = new BLEPacket();
		Map<String, Object> sleepData = blePacket.resolveSleepPacket(sleepPacket);
		long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1l);
		// TODO add sleep goal to Map
		long sleepGoal = UserGoalKeeper.readSleepGoalTime(this) * 60l;
		if (sleepGoal <= 0) {
			sleepGoal = 8 * 3600l;
		}
		sleepData.put(DBInfo.KEY_SLEEP_GOAL, sleepGoal);
		DBManager.addSleepInfo(userId, sleepData);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(RBLService.ACTION_GATT_RSSI);

		return intentFilter;
	}

	private void initView() {
		findViewById(R.id.btn_goal).setOnClickListener(this);
		findViewById(R.id.btn_connect_fitband).setOnClickListener(this);
		findViewById(R.id.btn_settings).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		Intent intent;
		switch (arg0.getId()) {
		case R.id.btn_goal:
			intent = new Intent(this, SettingsGoalActivity.class);
			startActivityForResult(intent, RESULT_FIRST_USER);
			break;
		case R.id.btn_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_connect_fitband:
			if (checkBLE()) {
				scanAndConnectDevice();
			}
			// Test code
			// saveActivityInfo(Util.getActivityInfoTestData());
			// saveEvery15MinPacket(Util.getTestBytesList());
			// saveSleepPacket(Util.getTestBytesList());
			break;
		default:
			break;
		}

	}

	private void showConnectAndSyncDialog(int msgId) {
		if (mConnectAndSyncDialog == null) {
			mConnectAndSyncDialog = new CustomizedProgressDialog(this, msgId);
			mConnectAndSyncDialog.setCancelable(false);
		} else {
			if (mConnectAndSyncDialog.isShowing()) {
				mConnectAndSyncDialog.dismiss();
			}
		}

		mConnectAndSyncDialog.setMsgId(msgId);
		mConnectAndSyncDialog.show();
	}

	private void dismissConnectAndSyncDialog(int msgId) {
		if (mConnectAndSyncDialog != null && mConnectAndSyncDialog.isShowing()
				&& msgId == mConnectAndSyncDialog.getMsgId()) {
			mConnectAndSyncDialog.dismiss();
		}
	}

	private void scanAndConnectDevice() {
		showConnectAndSyncDialog(R.string.connecting);
		scanLeDevice();

		Timer mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (mDeviceAddress == null) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast toast = Toast.makeText(MenuActivity.this,
									R.string.couldnot_search_ble_device, Toast.LENGTH_SHORT);
							toast.show();
						}
					});
				}
				dismissConnectAndSyncDialog(R.string.connecting);
			}
		}, SCAN_PERIOD);

		// if (connState == false) {
		// mBluetoothLeService.connect(mDeviceAddress);
		// } else {
		// mBluetoothLeService.disconnect();
		// mBluetoothLeService.close();
		// }
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			String address = device.getAddress();
			Log.i(TAG, "device address:" + address + ", device name:" + device.getName()
					+ ", scanRecord=" + Util.bytesToHex(scanRecord));
			if (!mDeviceAddressAndName.containsKey(address)) {
				mDeviceAddressAndName.put(address, device.getName());
				if (mDeviceID != null && mDeviceID.equals(address)) {
					mDeviceAddress = address;
					mBluetoothLeService.connect(mDeviceAddress);
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					dismissConnectAndSyncDialog(R.string.connecting);
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							checkDevice(device.getAddress());
						}
					});
				}
			}
		}
	};

	private void checkDevice(final String address) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					if (data.has(TLEHttpRequest.STATUS_CODE)) {
						int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
						if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
							// String msg =
							// getString(R.string.error_device_address);
							// if (data.has(TLEHttpRequest.MSG)) {
							// msg = data.getString(TLEHttpRequest.MSG);
							// }
							// ToastHelper.showToast(msg, Toast.LENGTH_LONG);
						} else {
							mDeviceAddress = address;
							mBluetoothLeService.connect(mDeviceAddress);
							mBluetoothAdapter.stopLeScan(mLeScanCallback);
							dismissConnectAndSyncDialog(R.string.connecting);
							UserInfoKeeper.writeUserInfo(MenuActivity.this,
									UserInfoKeeper.KEY_DEVICE_ID, mDeviceAddress);
							UserInfo info = UserInfoKeeper.readUserInfo(MenuActivity.this);
							Map<String, String> postData = UserInfoUtils
									.convertUserInfoToParamsMap(info);
							TLEHttpRequest httpRequest = TLEHttpRequest.instance();
							httpRequest.post(UPDATE_USER_INFO_END_URL, postData);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);

				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				dismissConnectAndSyncDialog(R.string.connecting);
			}
		}, null);
		Map<String, String> data = new TreeMap<String, String>();
		data.put("DeviceID", address);
		httpRequest.post(CHECK_DEVICE_END_URL, data);
	}

	private void scanLeDevice() {
		mDeviceAddressAndName.clear();
		mDeviceAddress = null;
		new Thread() {

			@Override
			public void run() {
				mBluetoothAdapter.startLeScan(mLeScanCallback);

				try {
					Thread.sleep(SCAN_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		}.start();
	}

	private void ShowBleNotSupportDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_confirm, null);
		final Dialog dialog = new Dialog(this, R.style.DialogTheme);
		TextView title = (TextView) dialogView.findViewById(R.id.layout_content);
		title.setText(getResources().getString(R.string.ble_not_supported_tips));
		dialog.setContentView(dialogView);
		dialog.setTitle(R.string.app_name);
		Button positiveBtn = (Button) dialogView.findViewById(R.id.positive);
		positiveBtn.setText(R.string.OK);
		positiveBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ImageView line = (ImageView) dialogView.findViewById(R.id.line_btn);
		line.setVisibility(View.GONE);
		Button negativeBtn = (Button) dialogView.findViewById(R.id.negative);
		negativeBtn.setVisibility(View.GONE);
		dialog.show();
	}

}
