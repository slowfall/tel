package com.tranway.tleshine.model;

public class BLEPacket {
	private static final String TAG = BLEPacket.class.getSimpleName();

	public BLEPacket() {
		// TODO Auto-generated constructor stub
	}

	public byte[] makeUserInfoForWrite(boolean isNeedUpdate, UserInfo userInfo) {
		byte[] buf;
		if (isNeedUpdate) {
			buf = new byte[UserInfo.USER_INFO_BYTES_LENGTH_NEED_UPDATE];
			buf[0] = UserInfo.NOT_NEED_UPDATE_FLAG;
			buf[1] = (byte) 0x01; // Sequence number
			buf[2] = (byte) 0x00; // 0x00:Succeed 0x01:Failed
		} else {
			if (userInfo == null) {
				Util.logE(TAG, "need update, user info can not be null.");
				return null;
			}
			buf = new byte[UserInfo.USER_INFO_BYTES_LENGTH_NOT_NEED_UPDATE];
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
			buf[11] = checksum(buf, 11);
		}
		return buf;
	}

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
		int checksum = (sum % 0xFF) ^ 0xFF;
		return (byte) checksum;
	}
}
