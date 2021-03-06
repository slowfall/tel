package com.tranway.Oband_Fitnessband;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tranway.Oband_Fitnessband.model.ActivityInfo;
import com.tranway.Oband_Fitnessband.model.BLEPacket;
import com.tranway.Oband_Fitnessband.model.MyApplication;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest;
import com.tranway.Oband_Fitnessband.model.TLEHttpRequest.OnHttpRequestListener;
import com.tranway.Oband_Fitnessband.model.ToastHelper;
import com.tranway.Oband_Fitnessband.model.UserInfo;
import com.tranway.Oband_Fitnessband.model.UserInfoKeeper;
import com.tranway.Oband_Fitnessband.model.Util;
import com.tranway.Oband_Fitnessband.viewMainTabs.MainActivity;
import com.tranway.tleshine.bluetooth.RBLService;
import com.tranway.tleshine.database.DBManager;

@SuppressLint("NewApi")
public class BLEConnectActivity extends Activity implements OnClickListener {
	private final static String TAG = BLEConnectActivity.class.getSimpleName();
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 20000;

	private static final String CHECK_DEVICE_END_URL = "/CheckDevice";
	private static final String GET_INFO_END_URL = "/Get";
	private static final String ADD_SPORT_POINT_END_URL = "/AddSportPoint";

	private Button mConnectBtn = null;
	private boolean connState = false;

	private BluetoothGattCharacteristic characteristicTx = null;
	private RBLService mBluetoothLeService;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mDevice = null;
	private String mDeviceAddress;
	private List<byte[]> mPacketForEvery15Min = new ArrayList<byte[]>();

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((RBLService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
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
				Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
				// setButtonDisable();
			} else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bleconnect);

		setup();

		checkUserId();

		checkBLE();

		initTitleView();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(mGattUpdateReceiver);
		if (mServiceConnection != null)
			unbindService(mServiceConnection);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setup() {
		mConnectBtn = (Button) findViewById(R.id.btn_ble_connect);
		mConnectBtn.setOnClickListener(this);
	}

	private void checkUserId() {
		// TODO Auto-generated method stub
		UserInfo info = UserInfoKeeper.readUserInfo(this);
		if (info.getId() < 0) {
			getUserInfoFromServer(info.getEmail());
		}
	}

	private void getUserInfoFromServer(String email) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					UserInfoKeeper.writeUserInfo(getApplicationContext(), data);
				} catch (JSONException e) {
					e.printStackTrace();
					ToastHelper.showToast(R.string.get_register_info_failed);
				}
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}
		}, this);
		httpRequest.get(GET_INFO_END_URL + "/" + email, null);
	}

	private void checkBLE() {
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT).show();
			finish();
		}

		final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		Intent gattServiceIntent = new Intent(this, RBLService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
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

	private void handleBLEData(byte[] data) {
		BLEPacket packet = new BLEPacket();
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
				long utcTime = System.currentTimeMillis() / 1000;
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
				saveActivityInfo(packet.resolveCurrentActivityInfo(data));
				ack = packet.makeReplyACK(sequenceNumber);
				characteristicTx.setValue(ack);
				mBluetoothLeService.writeCharacteristic(characteristicTx);
			}
			break;
		case 0x04:
			if (packet.checkChecksum(data)) {
				mPacketForEvery15Min.add(data);
				if (data[1] == (byte) 0x03) {
					savePacketForEvery15Min(mPacketForEvery15Min);
					ack = packet.makeReplyACK(sequenceNumber);
					characteristicTx.setValue(ack);
					mBluetoothLeService.writeCharacteristic(characteristicTx);
					Intent intent = new Intent(MyApplication.getAppContext(),
							MainActivity.class);
					startActivity(intent);
				}
			}
			break;
		case 0x05:
			if (packet.checkChecksum(data)) {
				ack = packet.makeReplyACK(sequenceNumber);
				characteristicTx.setValue(ack);
				mBluetoothLeService.writeCharacteristic(characteristicTx);
			}
			break;
		case 0x06:
			ack = packet.makeReplyACK(sequenceNumber);
			characteristicTx.setValue(ack);
			mBluetoothLeService.writeCharacteristic(characteristicTx);
			Intent intent = new Intent(MyApplication.getAppContext(), MainActivity.class);
			startActivity(intent);
			break;
		default:
			ack = packet.makeReplyACK(sequenceNumber);
			characteristicTx.setValue(ack);
			mBluetoothLeService.writeCharacteristic(characteristicTx);
			Intent bIntent = new Intent(MyApplication.getAppContext(), MainActivity.class);
			startActivity(bIntent);
			break;
		}
	}

	private void saveActivityInfo(ActivityInfo currentActivityInfo) {
		long utcTime = System.currentTimeMillis() / 1000;

		long todayUTC = utcTime / (3600 * 24);
		currentActivityInfo.setUtcTime(todayUTC);
		if (currentActivityInfo.getSteps() > 0) {
			long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1l);
			DBManager.addActivityInfo(userId, currentActivityInfo);
			TLEHttpRequest request = TLEHttpRequest.instance();
			Map<String, String> data = new TreeMap<String, String>();
			data.put("StepCount", String.valueOf(currentActivityInfo.getSteps()));
			data.put("UserID", String.valueOf(UserInfoKeeper.readUserInfo(getApplicationContext(),
					UserInfoKeeper.KEY_ID, 0l)));
			data.put("CreateDate", String.valueOf(System.currentTimeMillis() / 1000));
			request.post(ADD_SPORT_POINT_END_URL, data);
		}
	}

	private void savePacketForEvery15Min(List<byte[]> packetForEvery15Min) {
		BLEPacket blePacket = new BLEPacket();
		List<Map<String, Object>> every15MinDatas = blePacket
				.resolveEvery15MinPacket(packetForEvery15Min);
		long userId = UserInfoKeeper.readUserInfo(this, UserInfoKeeper.KEY_ID, -1l);
		DBManager.addEvery15MinData(userId, every15MinDatas);
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

	private void initTitleView() {
		Button mPreBtn = (Button) findViewById(R.id.btn_title_left);
		mPreBtn.setText(R.string.pre_step);
		mPreBtn.setVisibility(View.VISIBLE);
		mPreBtn.setOnClickListener(this);
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(R.string.connect_device);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_ble_connect:
			scanAndConnectDevice();
			mConnectBtn.setText(R.string.scanning);
			mConnectBtn.setEnabled(false);
			break;

		default:
			break;
		}
	}

	private void scanAndConnectDevice() {
		scanLeDevice();

		Timer mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (mDevice == null) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast toast = Toast.makeText(BLEConnectActivity.this,
									R.string.couldnot_search_ble_device, Toast.LENGTH_SHORT);
							toast.show();
							mConnectBtn.setText(R.string.connect_fitband);
							mConnectBtn.setEnabled(true);
						}
					});
				}
			}
		}, SCAN_PERIOD);

		if (connState == false) {
			mBluetoothLeService.connect(mDeviceAddress);
		} else {
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
		}
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			Log.i(TAG,
					"device address:" + device.getAddress() + ", scanRecord="
							+ Util.bytesToHex(scanRecord));
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					checkDevice(device);
				}
			});
		}
	};

	private void checkDevice(final BluetoothDevice device) {
		TLEHttpRequest httpRequest = TLEHttpRequest.instance();
		httpRequest.setOnHttpRequestListener(new OnHttpRequestListener() {

			@Override
			public void onSuccess(String url, String result) {
				try {
					JSONObject data = new JSONObject(result);
					if (data.has(TLEHttpRequest.STATUS_CODE)) {
						int statusCode = data.getInt(TLEHttpRequest.STATUS_CODE);
						if (statusCode == TLEHttpRequest.STATE_SUCCESS) {
							String msg = getString(R.string.error_device_address);
							if (data.has(TLEHttpRequest.MSG)) {
								msg = data.getString(TLEHttpRequest.MSG);
							}
							ToastHelper.showToast(msg, Toast.LENGTH_LONG);
						} else {
							mDevice = device;
							mDeviceAddress = mDevice.getAddress();
							mBluetoothLeService.connect(mDeviceAddress);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mConnectBtn.setText(R.string.connect_fitband);
				mConnectBtn.setEnabled(true);
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}

			@Override
			public void onFailure(String url, int errorNo, String errorMsg) {
				ToastHelper.showToast(R.string.error_server_return, Toast.LENGTH_SHORT);
			}
		}, null);
		Map<String, String> data = new TreeMap<String, String>();
		data.put("DeviceID", device.getAddress());
		httpRequest.post(CHECK_DEVICE_END_URL, data);
	}

	private void scanLeDevice() {
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

}
