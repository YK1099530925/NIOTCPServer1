package tcpserver.acceptappoint;

/**
 * ����ָ��������
 * @author YangKuan
 *
 */
public class AcceptAppoint {
	
	/** �ϳ�
	 * ����ָ����������
	 * @return
	 */
	public String[] acceptData(String array[]) {
		String acceptAppointStrArray[] = new String[array.length];
		for(int i = 0; i < array.length; i++) {
			acceptAppointStrArray[i] = array[i].substring((7 - 1) * 2, (7 - 1 + 12) * 2);
		}
		return acceptAppointStrArray;
	}
	
	/** �ϳ�
	 * ����ָ�������ݣ�������Ҫ�������򷵻�true�����򷵻�false
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
	 * ���ص�ǰ�������ݵ���Ӧ
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
