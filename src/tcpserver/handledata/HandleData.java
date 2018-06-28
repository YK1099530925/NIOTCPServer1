package tcpserver.handledata;

import java.text.SimpleDateFormat;
import java.util.Date;

//��������
public class HandleData {
	
	/**
	 * ��������
	 * 
	 * @param dataString
	 */
	public String[] handle(String dataString) {
		// ��ȡ������16����
		String str = dataString.substring((3 - 1) * 2, (4 - 1) * 2);
		// �ж��Ƿ���44������ǣ��ͽ��д洢��������ǣ���ֱ�ӷ���
		if (!str.equals("44")) {
			return null;
		}
		// 1���������
		String[] dataStr = splitData(dataString);
		// 2����������
		if (dataStr.length == 5) {// ��������������
			dataStr = resolveOtherData(dataStr);
		} else if (dataStr.length == 6) {// ��������������������
			dataStr = resolveAirAndSoilData(dataStr);
		} else {
			System.err.println("����");
		}
		//������֮���ӡһ������
		printData(dataStr);
		return dataStr;
	}
	
	//��ӡת���������
	public void printData(String[] dataStr) {
		for (String str : dataStr) {
			System.err.println(str);
		}
	}
	
	/**
	 * �������(ǰ���������ǲ���ģ���˿��Խ�ǰ��������ȡ����) ��0�����ݣ�����id ��1�����ݣ��豸��ʶ ��2�����ݣ��豸����
	 * 
	 * @return
	 */
	public String[] splitData(String dataString) {
		// �жϣ���Ϊ����ʪ�ȵ���Ҫ5���ռ䣬co2�͹���ֻ��Ҫ4���ռ䣩
		String[] dataStr = null;
		if (dataString.length() <= 50) {
			dataStr = new String[5];
		} else {
			dataStr = new String[6];
		}
		splitData1(dataStr, dataString);
		splitData2(dataStr, dataString);

		return dataStr;
	}

	// ���ǰ��������
	public String[] splitData1(String[] dataStr, String dataString) {
		dataStr[0] = dataString.substring((5 - 1) * 2, (5 - 1 + 2) * 2);
		dataStr[1] = dataString.substring((7 - 1) * 2, (7 - 1 + 12) * 2);
		dataStr[2] = dataString.substring((21 - 1) * 2, (21 - 1 + 1) * 2);
		return dataStr;
	}

	// ��ֺ��������
	public String[] splitData2(String[] dataStr, String dataString) {
		Date dateNow = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int len = dataString.length() / 2;
		if (dataStr.length == 5) {
			// �жϣ���Ϊ�ֶ�Ϊ4�����������λҲ�в�ͬ
			if (len == 24) {// ������24��16����������ȡ������2����
				dataStr[3] = dataString.substring((len - 2) * 2, (len - 1) * 2);
			} else if (len == 25) {// ����Ϊ25��16������(���Խ�ȡ������2�͵�3λ)
				dataStr[3] = dataString.substring((len - 3) * 2, (len - 1) * 2);
			}
			dataStr[4] = df.format(dateNow);
		} else if (dataStr.length == 6) {
			if (dataStr[2].equals("b3")) {// b3��������ʪ�ȣ�ǰ��λ��ʾ�¶ȣ���һλ��ʾʪ�ȣ�
				dataStr[3] = dataString.substring((len - 4) * 2, (len - 2) * 2);
				dataStr[4] = dataString.substring((len - 2) * 2, (len - 1) * 2);
			} else if (dataStr[2].equals("a5")) {// a5��������ʪ�ȣ�ǰһλ��ʾ�¶ȣ�����λ��ʾʪ�ȣ���������λ��ȡ��dataStr[3]�У���b3����һ��
				dataStr[3] = dataString.substring((len - 3) * 2, (len - 1) * 2);
				dataStr[4] = dataString.substring((len - 4) * 2, (len - 3) * 2);
			}
			dataStr[5] = df.format(dateNow);
		} else {
			System.out.println("�쳣");
		}

		return dataStr;
	}
	
	/**
	 * �������������ݵĽ���
	 * �������һ������
	 * ��ʪ�ȣ���������byte��
	 * ʾ����31 02 1C
	 * 31 02: 
	 * �¶�=  (0x0231/10)-40 = (561/10)-40 = 16.1C
	 * 1C: ʪ�� = 0x1C% = 28%
	 * 
	 * @param dataStr
	 * @return
	 */
	public String[] resolveAirAndSoilData(String[] dataStr) {
		//�¶Ȼ���
		String temperature = dataStr[3].substring(2, 4) + dataStr[3].substring(0, 2);
		double temFormatDouble = ((double) Integer.parseInt(temperature,16) - 400) / 10;
		//ʪ��ת��
		String humidity = dataStr[4].substring(0, 2);
		dataStr[3] = temFormatDouble + "��C";
		dataStr[4] = Integer.parseInt(humidity, 16) + "";
		return dataStr;
	}
	
	/**
	 * ������������(�������֣�����ǿ�ȣ�������̼������ת����16����)
	 * �������ִ�������Ӧ��   ��invade_data ���豸���ͣ�0x10
	 * ����ǿ�ȴ�������Ӧ��    ��beam_data   ���豸���ͣ�0xC0
	 * ������̼��������Ӧ��    ��co2_data    ���豸���ͣ�0xD0
	 * @param dataStr
	 * @return
	 */
	public String[] resolveOtherData(String[] dataStr) {
		switch (dataStr[2]) {
		case "10"://��������
			dataStr[3] = Integer.parseInt(dataStr[3], 16) + "";
			break;
		case "c0"://����ǿ��
			String beamStr = dataStr[3].substring(2, 4) + dataStr[3].substring(0, 2);
			dataStr[3] = Integer.parseInt(beamStr, 16) + "Lux";
			break;
		case "d0"://������̼
			String co2 = dataStr[3].substring(2, 4) + dataStr[3].substring(0, 2);
			dataStr[3] = Integer.parseInt(co2, 16) + "ppm";
			break;
		default:
			break;
		}
		return dataStr;
	}
}
