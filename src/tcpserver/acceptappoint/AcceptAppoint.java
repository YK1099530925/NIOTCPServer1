package tcpserver.acceptappoint;

/**
 * ����ָ��������
 * @author YangKuan
 *
 */
public class AcceptAppoint {
	
	/**
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
	
	/**
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
}
