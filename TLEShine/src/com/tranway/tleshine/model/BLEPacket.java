package com.tranway.tleshine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.tranway.telshine.database.DBInfo;

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

	private void copyBytesArrayToList(byte[] fromArray, int fromPos, int length, List<Byte> toList) {
		for (int i = fromPos; i < length; i++) {
			toList.add(fromArray[i]);
		}
	}

	public static final int CMD_AND_PACKET_INDEX_BYTES_LENGTH = 2;
	public static final int PACKET_UTC_TIME_BYTES_LENGHT = 4;
	public static final int CHECKSUM_BYTE_LENGTH = 1;

	public List<Map<String, Object>> resolveEvery15MinPacket(List<byte[]> every15MinPackets) {
		List<Map<String, Object>> every15MinDatas = new ArrayList<Map<String, Object>>();
		List<Byte> every15MinBytes = new ArrayList<Byte>();
		for (int i = 0; i < every15MinPackets.size(); i++) {
			byte[] packetData = every15MinPackets.get(i);
			byte packetIndex = packetData[1];
			switch (packetIndex) {
			case 0x01:
				if (every15MinBytes.size() > 0) {
					every15MinDatas.add(resolveTimeStepAndCalorie(every15MinBytes));
					every15MinBytes.clear();
				}

				copyBytesArrayToList(packetData, CMD_AND_PACKET_INDEX_BYTES_LENGTH,
						packetData.length - CMD_AND_PACKET_INDEX_BYTES_LENGTH
								- CHECKSUM_BYTE_LENGTH, every15MinBytes);
				break;
			case 0x02:
				copyBytesArrayToList(packetData, CMD_AND_PACKET_INDEX_BYTES_LENGTH,
						packetData.length - CMD_AND_PACKET_INDEX_BYTES_LENGTH
								- CHECKSUM_BYTE_LENGTH, every15MinBytes);
				break;
			case 0x03:
				if (every15MinBytes.size() > 0) {
					every15MinDatas.add(resolveTimeStepAndCalorie(every15MinBytes));
					every15MinBytes.clear();
				}
				// check total packets number
				break;
			default:
				break;
			}
		}
		return every15MinDatas;
	}

	private Map<String, Object> resolveTimeStepAndCalorie(List<Byte> every15MinBytes) {
		Map<String, Object> every15MinData = new TreeMap<String, Object>();
		long utcTime = 0;
		int steps = 0;
		int calories = 0;
		byte[] utcTimeBytes = new byte[PACKET_UTC_TIME_BYTES_LENGHT];
		for (int i = 0; i < PACKET_UTC_TIME_BYTES_LENGHT; i++) {
			utcTimeBytes[i] = every15MinBytes.get(i);
		}
		utcTime = bytesToInt(utcTimeBytes);
		for (int i = PACKET_UTC_TIME_BYTES_LENGHT; i < every15MinBytes.size(); i++) {
			// even is step, odd is calorie
			if ((i % 2) == 0) {
				steps += bytesToInt(new byte[] { every15MinBytes.get(i) });
			} else {
				calories += bytesToInt(new byte[] { every15MinBytes.get(i) });
			}
		}
		every15MinData.put(DBInfo.KEY_UTC_TIME, utcTime);
		every15MinData.put(DBInfo.KEY_STEPS, steps);
		every15MinData.put(DBInfo.KEY_CALORIE, calories);
		Util.logD(TAG, "in resovleEvery15MinPacket: utcTime:" + utcTime + ", steps:" + steps
				+ ", calories:" + calories);
		return every15MinData;
	}

	public Map<String, Object> resolveSleepPacket(List<byte[]> sleepPacket) {
		Map<String, Object> sleepMap = new TreeMap<String, Object>();
		List<Byte> sleepBytes = new ArrayList<Byte>();
		long startTime = 0;
		boolean isSetStartTime = false;
		for (int i = 0; i < sleepPacket.size(); i++) {
			byte[] packetData = sleepPacket.get(i);
			byte packetIndex = packetData[1];
			switch (packetIndex) {
			case 0x01:
				if (!isSetStartTime) {
					isSetStartTime = true;
					byte[] utcTimeBytes = new byte[4];
					System.arraycopy(packetData, CMD_AND_PACKET_INDEX_BYTES_LENGTH, utcTimeBytes,
							0, utcTimeBytes.length);
					startTime = bytesToInt(utcTimeBytes);
				}
				copyBytesArrayToList(packetData, CMD_AND_PACKET_INDEX_BYTES_LENGTH,
						packetData.length - CMD_AND_PACKET_INDEX_BYTES_LENGTH
								- CHECKSUM_BYTE_LENGTH, sleepBytes);
				break;
			case 0x02:
				copyBytesArrayToList(packetData, CMD_AND_PACKET_INDEX_BYTES_LENGTH,
						packetData.length - 1, sleepBytes);
				break;
			case 0x03:
				// check total packets number
				if (sleepBytes.size() > 0) {
					sleepMap = resolveSleepBytes(startTime, sleepBytes);
				}
				break;
			default:
				break;
			}
		}

		return sleepMap;
	}

	private static final int SECONDS_OF_5_MINS = 5 * 60;

	private Map<String, Object> resolveSleepBytes(long startUtcTime, List<Byte> sleepBytes) {
		Map<String, Object> sleepMap = new TreeMap<String, Object>();
		long sleepedTime = 0;
		long sleepDeepTime = 0;
		long sleepShallowTime = 0;
		int sleepCount = 0;
		int startSleepIndex = 0;
		boolean isSetStartIndex = false;
		for (int i = 0; i < sleepBytes.size(); i++) {
			byte b = sleepBytes.get(i);
			if (bytesToInt(new byte[] { b }) <= 1) {
				sleepCount += 1;
			} else {
				sleepCount = 0;
			}

			// 四个连续的5分钟的数据小于等于1的时候表明已经开始进入深度睡眠，深度睡眠的时间
			// 是错开的，因此，将这些时间累加起来就是整个晚上的深度睡眠时间，其余都是浅睡眠
			// 时间；
			if (sleepCount > 4) {
				sleepDeepTime += SECONDS_OF_5_MINS;
			} else if (sleepCount == 4) {
				sleepDeepTime += SECONDS_OF_5_MINS * 4;
			}
			// 两个连续的5分钟的数据小于等于1的时候表明已经开始进入睡眠了
			else if (sleepCount == 2 && !isSetStartIndex) {
				isSetStartIndex = true;
				startSleepIndex = i;
			}
		}

		sleepedTime = (sleepBytes.size() - startSleepIndex) * SECONDS_OF_5_MINS;
		long mod = startUtcTime % SECONDS_OF_5_MINS;
		sleepedTime -= mod;
		sleepShallowTime = sleepedTime - sleepDeepTime;
		sleepMap.put(DBInfo.KEY_UTC_TIME, sleepedTime);
		sleepMap.put(DBInfo.KEY_SLEEP_DEEP_TIME, sleepDeepTime);
		sleepMap.put(DBInfo.KEY_SLEEP_SHALLOW_TIME, sleepShallowTime);

		return sleepMap;
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
