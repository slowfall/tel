package com.tranway.tleshine.model;

public class BLEPacket {
	private static final String TAG = BLEPacket.class.getSimpleName();

	public BLEPacket() {
		// TODO Auto-generated constructor stub
	}

	public byte[] makeUserInfoForWrite(boolean isNeedUpdate, UserInfo userInfo) {
		byte[] buf;
		if (isNeedUpdate) {
			if (userInfo == null) {
				Util.logE(TAG, "need update, user info can not be null.");
				return new byte[1];
			}
			buf = new byte[UserInfo.USER_INFO_BYTES_LENGTH_NEED_UPDATE];
			buf[0] = UserInfo.NEED_UPDATE_FLAG;
			buf[1] = (byte) 0x01; // Sequence number
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
			buf[1] = (byte) 0x01; // Sequence number
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
	 * @return UTC time byte array
	 */
	public byte[] makeUTCForWrite(boolean isNeedUpdate, long utcTime) {
		byte[] buf = new byte[7];
		if (isNeedUpdate) {
			buf[0] = (byte) 0xE1;
		} else {
			buf[0] = (byte) 0xE0;
		}
		buf[1] = (byte) 0x01;// Sequence number
		byte[] utcBytes = toBytes(utcTime, 4);
		for (int i = 0; i < utcBytes.length; i++) {
			buf[2 + i] = utcBytes[i];
		}
		buf[6] = checksum(buf, 6);
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

	public byte checksum(byte[] bytes, int length) {
		int sum = 0;
		for (int i = 0; i < length; i++) {
			sum += bytes[i];
		}
		int checksum = (sum % 0xFF);
		return (byte) checksum;
	}

	final private static char[] hexArray = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

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
