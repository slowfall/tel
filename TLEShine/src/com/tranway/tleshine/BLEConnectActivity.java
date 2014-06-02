package com.tranway.tleshine;

import java.util.Timer;
import java.util.TimerTask;

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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.tranway.tleshine.bluetooth.RBLService;
import com.tranway.tleshine.model.BLEPacket;
import com.tranway.tleshine.model.MyApplication;
import com.tranway.tleshine.model.UserInfo;
import com.tranway.tleshine.model.Util;
import com.tranway.tleshine.viewMainTabs.MainTabsActivity;

public class BLEConnectActivity extends Activity implements OnClickListener {
	private final static String TAG = BLEConnectActivity.class.getSimpleName();
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 10000;

	private Button mConnectBtn = null;
	private boolean connState = false;

	private BluetoothGattCharacteristic characteristicTx = null;
	private RBLService mBluetoothLeService;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mDevice = null;
	private String mDeviceAddress;

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((RBLService.LocalBinder) service)
					.getService();
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
				Toast.makeText(getApplicationContext(), "Disconnected",
						Toast.LENGTH_SHORT).show();
				// setButtonDisable();
			} else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				Toast.makeText(getApplicationContext(), "Connected",
						Toast.LENGTH_SHORT).show();

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
		checkBLE();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onStop() {
		super.onStop();

		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mServiceConnection != null)
			unbindService(mServiceConnection);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setup() {
		mConnectBtn = (Button) findViewById(R.id.btn_ble_connect);
		mConnectBtn.setOnClickListener(this);
	}

	private void checkBLE() {
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
					.show();
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

		characteristicTx = gattService
				.getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);

		BluetoothGattCharacteristic characteristicRx = gattService
				.getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
		mBluetoothLeService.setCharacteristicNotification(characteristicRx,
				true);
		mBluetoothLeService.readCharacteristic(characteristicRx);
	}

	private void handleBLEData(byte[] data) {
		BLEPacket packet = new BLEPacket();
		byte mask = (byte)0x0f;
		int type = data[0] & mask;
		switch (type) {
		//User Info command
		case 0x01:
			UserInfo userInfo = new UserInfo();
			userInfo.setWeight(665);
			userInfo.setAge(10);
			userInfo.setHeight(170);
			userInfo.setStride(90);
			userInfo.setSex(0);
			userInfo.setStepsTarget(1000);
			byte[] info = packet.makeUserInfoForWrite(false, null);
			characteristicTx.setValue(info);
			mBluetoothLeService.writeCharacteristic(characteristicTx);
			break;
		//UTC command
		case 0x02:
			long utcTime = System.currentTimeMillis() / 1000;
			byte[] utc = packet.makeUTCForWrite(true, utcTime);
			characteristicTx.setValue(utc);
			mBluetoothLeService.writeCharacteristic(characteristicTx);
			break;
		default:
			break;
		}
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

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_ble_connect:
			scanAndConnectDevice();
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
				if (mDevice != null) {
					mDeviceAddress = mDevice.getAddress();
					if (mBluetoothLeService.connect(mDeviceAddress)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mConnectBtn.setEnabled(true);
//								 Intent intent = new
//								 Intent(MyApplication.getAppContext(),
//								 MainTabsActivity.class);
//								 startActivity(intent);
							}
						});
					}
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast toast = Toast.makeText(
									BLEConnectActivity.this,
									"Couldn't search Ble Shiled device!",
									Toast.LENGTH_SHORT);
							toast.setGravity(0, 0, Gravity.CENTER);
							toast.show();
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
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			Log.i(TAG, "device address:" + device.getAddress()
					+ ", scanRecord=" + Util.bytesToHex(scanRecord));
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// byte[] serviceUuidBytes = new byte[16];
					// String serviceUuid = "";
					// for (int i = 32, j = 0; i >= 17; i--, j++) {
					// serviceUuidBytes[j] = scanRecord[i];
					// }
					// serviceUuid = bytesToHex(serviceUuidBytes);
					// if (stringToUuidString(serviceUuid).equals(
					// RBLGattAttributes.BLE_SHIELD_SERVICE.toUpperCase(Locale.ENGLISH)))
					// {
					// mDevice = device;
					// }
					// TODO check device address
					mDevice = device;
				}
			});
		}
	};

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
