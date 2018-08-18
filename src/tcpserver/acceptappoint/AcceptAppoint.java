package tcpserver.acceptappoint;

/**
 * 接收指定的数据
 * @author YangKuan
 *
 */
public class AcceptAppoint {
	
	/** 废除
	 * 接收指定的数据组
	 * @return
	 */
	public String[] acceptData(String array[]) {
		String acceptAppointStrArray[] = new String[array.length];
		for(int i = 0; i < array.length; i++) {
			acceptAppointStrArray[i] = array[i].substring((7 - 1) * 2, (7 - 1 + 12) * 2);
		}
		return acceptAppointStrArray;
	}
	
	/** 废除
	 * 拦截指定的数据，是我们要的数据则返回true，否则返回false
	 * @param handle
	 * @param acceptStr
	 * @return
	 */
	public boolean interceptData(String handle[], String acceptStr[]) {
		for(String str : acceptStr) {
			if(handle[1].toUpperCase().equals(str.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 拦截当前发送数据的响应
	 * @param handle
	 * @param acceptStr
	 * @return
	 */
	public boolean interceptData(String handle[], String acceptStr) {
		String aString = acceptStr.substring((7 - 1) * 2, (7 - 1 + 12) * 2);
		if(handle[1].toUpperCase().equals(aString.toUpperCase())) {
			return true;
		}
		return false;
	}
	
}
