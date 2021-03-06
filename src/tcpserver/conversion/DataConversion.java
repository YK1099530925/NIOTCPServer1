package tcpserver.conversion;

import java.nio.ByteBuffer;

/**
 * 数据转换 
 * byteBufferToByteArray：buffer转byte数组
 * byteArraytoHexString ：byte数组转string（16进制字符串（如：fe12bd...））
 * hexStringtoByteArray ：string（16进制字符串（如：fe12bd...））转byte数组
 * 
 * @author YangKuan
 *
 */
public class DataConversion {
	
	/**
	 * 将bytebuffer转16进制字符串
	 * @param buffer
	 * @return
	 */
	public String byteBufferToHexstring(ByteBuffer buffer) {
		byte[] byteBufferToByteArray = byteBufferToByteArray(buffer);
		String byteArraytoHexString = byteArraytoHexString(byteBufferToByteArray);
		return byteArraytoHexString;
	}
	
	/**
	 * 将ByteBuffer转换成byte[]数组
	 * 
	 * @param buffer
	 * @return
	 */
	public byte[] byteBufferToByteArray(ByteBuffer buffer) {
		byte[] dataByte = new byte[buffer.limit()];
		buffer.get(dataByte, 0, buffer.limit());
		return dataByte;
	}
	
	/**
	 * 字节数组转成16进制表示格式的字符串
	 * 
	 * @param byteArray
	 *            需要转换的字节数组
	 * @return 16进制表示格式的字符串
	 **/
	public String byteArraytoHexString(byte[] byteArray) {
		if (byteArray == null || byteArray.length < 1)
			throw new IllegalArgumentException("this byteArray must not be null or empty");

		final StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] & 0xff) < 0x10)// 0~F前面不零
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return hexString.toString().toLowerCase();
	}

	/**
	 * 16进制的字符串表示转成字节数组
	 * 
	 * @param hexString
	 *            16进制格式的字符串
	 * @return 转换后的字节数组
	 **/
	public byte[] hexStringtoByteArray(String hexString) {
		// if (hexString.isEmpty())
		// throw new IllegalArgumentException("this hexString must not be empty");

		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}
}
