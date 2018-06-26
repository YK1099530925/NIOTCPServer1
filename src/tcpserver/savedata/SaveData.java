package tcpserver.savedata;

import tcpserver.sql.SqlHelper;

public class SaveData {	
	private SqlHelper sqlHelper = new SqlHelper();
	/**
	 * ����ͬ���ݱ�������ͬ��
	 * �������ִ�������Ӧ��   ��invade_data ���豸���ͣ�0x10
	 * ������ʪ�ȴ�������Ӧ��air_data    ���豸���ͣ�0xB3
	 * ����ǿ�ȴ�������Ӧ��    ��beam_data   ���豸���ͣ�0xC0
	 * ������̼��������Ӧ��    ��co2_data    ���豸���ͣ�0xD0
	 * ������ʪ�ȴ�������Ӧ��soil_data   ���豸���ͣ�0xA5
	 * �����豸������Сд
	 * @param dataStr
	 */
	public void saveData(String[] dataStr) {
		String sql = null;
		switch (dataStr[2]) {
		case "10":
			//invade_data
			sql = "insert into invade_data (wifiid,shebeibiaozhi,shebeileixing,invade) values(?,?,?,?)";
			break;
		case "b3":
			//air_data
			sql = "insert into air_data (wifiid,shebeibiaozhi,shebeileixing,temperature,humidity) values(?,?,?,?,?)";
			break;
		case "c0":
			//beam_data
			sql = "insert into beam_data (wifiid,shebeibiaozhi,shebeileixing,beam) values(?,?,?,?)";
			break;
		case "d0":
			//co2_data
			sql = "insert into co2_data (wifiid,shebeibiaozhi,shebeileixing,co2) values(?,?,?,?)";
			break;
		case "a5":
			//soil_data
			sql = "insert into soil_data (wifiid,shebeibiaozhi,shebeileixing,temperature,humidity) values(?,?,?,?,?)";
			break;
		default:
			break;
		}
		if(sql != null) {
			sqlHelper.insert(dataStr, sql);
		}
	}

	
}
