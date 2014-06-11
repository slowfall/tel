package com.tranway.tleshine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.tranway.telshine.database.DBEvery15MinPacketHelper;

public class BLEPacket {
	private static final String TAG = BLEPacket.class.getSimpleName();

	public BLEPacket() {
		// TODO Auto-generated constructor stub
	}

	public byte[] makeUserInfoForWrite(boolean isNeedUpdate, byte sequenceNumber, UserInfo userInfo) {
		byte[] buf;
		if (isNeedUpdate) {
			if (userInfo == null) {
				Util.logE(TAG, "need update, user info can not be null.");
				return new byte[1];
			}
			buf = new byte[UserInfo.USER_INFO_BYTES_LENGTH_NEED_UPDATE];
			buf[0] = UserInfo.NEED_UPDATE_FLAG;
			buf[1] = sequenceNumber; // Sequence number
			byte[] weight = toBytes(userInfo.getWeight(), 2);
			buf[2] = weight[0];
			buf[3] = weight[1];
			buf[4] = (byte) userInfo.getAge();
			buf[5] = (byte) userInfo.getHeight();
			buf[6] = (byte) userInfo.getStride();
			buf[7] = (byte) userInfo.getSex();
			byte[] steps = toBytes(userInfo.getStepsTarget(), 3);
			buf[8] = steps[0];
			buf[9] = steps[1];
			buf[10] = steps[2];
			buf[11] = checksum(buf, buf.length - 1);
		} else {
			buf = new byte[UserInfo.USER_INFO_BYTES_LENGTH_NOT_NEED_UPDATE];
			buf[0] = UserInfo.NOT_NEED_UPDATE_FLAG;
			buf[1] = sequenceNumber; // Sequence number
			buf[2] = (byte) 0x00; // 0x00:Succeed 0x01:Failed
			buf[3] = checksum(buf, buf.length - 2);
		}
		return buf;
	}

	/**
	 * @param isNeedUpdate
	 *            flag for update time
	 * @param utcTime
	 *            UTC time in second
	 * @param sequenceNumber
	 *            sequence number
	 * @return UTC time byte array
	 */
	public byte[] makeUTCForWrite(boolean isNeedUpdate, byte sequenceNumber, long utcTime) {
		byte[] buf = new byte[7];
		if (isNeedUpdate) {
			buf[0] = (byte) 0xE2;
		} else {
			buf[0] = (byte) 0xE0;
		}
		buf[1] = (byte) 0x01;// Sequence number
		byte[] utcBytes = toBytes(utcTime, 4);
		for (int i = 0; i < utcBytes.length; i++) {
			buf[2 + i] = utcBytes[i];
		}
		buf[6] = checksum(buf, buf.length - 1);
		return buf;
	}

	public int resolveUTCTime(byte[] utcSyncBytes) {
		int utcTime = 0;
		byte[] utcBytes = new byte[4];
		System.arraycopy(utcSyncBytes, 1, utcBytes, 0, utcBytes.length);
		utcTime = bytesToInt(utcBytes);
		return utcTime;
	}

	public ActivityInfo resolveCurrentActivityInfo(byte[] byteActivityInfo) {
		ActivityInfo activityInfo = new ActivityInfo();

		byte[] utcBytes = new byte[4];
		System.arraycopy(byteActivityInfo, 1, utcBytes, 0, utcBytes.length);
		activityInfo.setUtcTime(bytesToInt(utcBytes));

		byte[] stepsBytes = new byte[3];
		System.arraycopy(byteActivityInfo, 5, stepsBytes, 0, stepsBytes.length);
		activityInfo.setSteps(bytesToInt(stepsBytes));

		byte[] distanceBytes = new byte[3];
		System.arraycopy(byteActivityInfo, 8, distanceBytes, 0, distanceBytes.length);
		activityInfo.setDistance(bytesToInt(distanceBytes));

		byte[] calorieBytes = new byte[3];
		System.arraycopy(byteActivityInfo, 11, calorieBytes, 0, calorieBytes.length);
		activityInfo.setCalorie(bytesToInt(calorieBytes));
		Util.logD(TAG, "byte activity info:" + bytesToHex(byteActivityInfo));
		Util.logD(TAG, activityInfo.toString());
		return activityInfo;
	}

	public static final int EVERY_15_MIN_PACKET_UTC_TIME_LENGHT = 4;
	public static final int EVERY_15_MIN_PACKET_FRIST_DATA_LENGHT = 13;
	public static final int EVERY_15_MIN_PACKET_SECONDE_DATA_LENGHT = 17;

	public List<Map<String, Object>> resovleEvery15MinPacket(List<byte[]> every15MinPacket) {
		List<Map<String, Object>> every15MinDatas = new ArrayList<Map<String, Object>>();
		byte[] timeAndData = new byte[EVERY_15_MIN_PACKET_UTC_TIME_LENGHT
				+ EVERY_15_MIN_PACKET_FRIST_DATA_LENGHT + EVERY_15_MIN_PACKET_SECONDE_DATA_LENGHT];
		for (int i = 0; i < 3; i++) {
			byte[] packetData = every15MinPacket.get(i);
			byte packetIndex = packetData[1];
			switch (packetIndex) {
			case 0x01:
				if (packetData.length != 20) {
					return every15MinDatas;
				}
				System.arraycopy(packetData, 2, timeAndData, 0, EVERY_15_MIN_PACKET_UTC_TIME_LENGHT);
				// first packet data length is 13
				System.arraycopy(packetData, 2 + EVERY_15_MIN_PACKET_UTC_TIME_LENGHT, timeAndData,
						EVERY_15_MIN_PACKET_UTC_TIME_LENGHT, EVERY_15_MIN_PACKET_FRIST_DATA_LENGHT);
				break;
			case 0x02:
				if (packetData.length != 20) {
					return every15MinDatas;
				}
				// second packet data length is 17
				System.arraycopy(packetData, 2, timeAndData, EVERY_15_MIN_PACKET_UTC_TIME_LENGHT
						+ EVERY_15_MIN_PACKET_FRIST_DATA_LENGHT,
						EVERY_15_MIN_PACKET_SECONDE_DATA_LENGHT);
				every15MinDatas.add(resovleEvery15MinByteArray(timeAndData));
				break;
			case 0x03:
				// check total packets number
				break;
			default:
				break;
			}
		}
		return every15MinDatas;
	}

	private Map<String, Object> resovleEvery15MinByteArray(byte[] byteArray) {
		Map<String, Object> every15MinData = new TreeMap<String, Object>();
		int steps = 0;
		int calories = 0;
		byte[] utcTimeBytes = new byte[4];
		System.arraycopy(byteArray, 0, utcTimeBytes, 0, EVERY_15_MIN_PACKET_UTC_TIME_LENGHT);
		int utcTime = bytesToInt(utcTimeBytes);
		for (int i = EVERY_15_MIN_PACKET_UTC_TIME_LENGHT; i < byteArray.length; i++) {
			// even is step, odd is calorie
			if ((i % 2) == 0) {
				steps += bytesToInt(new byte[] { byteArray[i] });
			} else {
				calories += bytesToInt(new byte[] { byteArray[i] });
			}
		}
		every15MinData.put(DBEvery15MinPacketHelper.KEY_UTC_TIME, utcTime);
		every15MinData.put(DBEvery15MinPacketHelper.KEY_STEPS, steps);
		every15MinData.put(DBEvery15MinPacketHelper.KEY_CAOLRIE, calories);
		Util.logD(TAG, "in resovleEvery15MinPacket: utcTime:" + utcTime + ", steps:" + steps
				+ ", calories:" + calories);
		return every15MinData;
	}

	public byte[] makeReplyACK(byte sequenceNumber) {
		byte[] buf = new byte[4];
		buf[0] = (byte) 0xE0;
		buf[1] = sequenceNumber;
		buf[2] = (byte) 0x00;
		buf[3] = checksum(buf, 2);
		return buf;
	}

	private byte[] toBytes(int i, int length) {
		byte[] result = new byte[length];

		for (int j = 0; j < length; j++) {
			result[j] = (byte) (i >> 8 * (length - j - 1));
		}

		return result;
	}

	public byte[] toBytes(long i, int length) {
		byte[] result = new byte[length];

		for (int j = 0; j < length; j++) {
			result[j] = (byte) (i >> 8 * (length - j - 1));
		}

		return result;
	}

	public int bytesToInt(byte[] buf) {
		int result = 0;
		byte[] newBuf = new byte[] { 0x00, 0x00, 0x00, 0x00 };
		if (buf.length < 4) {
			for (int i = 0; i < buf.length; i++) {
				newBuf[newBuf.length - buf.length + i] = buf[i];
			}
		} else {
			newBuf = buf;
		}
		result = newBuf[3] & 0xFF | (newBuf[2] & 0xFF) << 8 | (newBuf[1] & 0xFF) << 16
				| (newBuf[0] & 0xFF) << 24;
		return result;
	}

	public byte checksum(byte[] bytes, int length) {
		int sum = 0;
		for (int i = 0; i < length; i++) {
			sum += bytesToInt(new byte[] { bytes[i] });
		}
		int checksum = (sum % 256);
		return (byte) checksum;
	}

	public boolean checkChecksum(byte[] bytes) {
		byte checksum = checksum(bytes, bytes.length - 1);

		return checksum == bytes[bytes.length - 1];
	}

	final private static char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
			'b', 'c', 'd', 'e', 'f' };

	public String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
