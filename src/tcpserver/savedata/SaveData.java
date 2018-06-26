package tcpserver.savedata;

import tcpserver.sql.SqlHelper;

public class SaveData {	
	private SqlHelper sqlHelper = new SqlHelper();
	/**
	 * 将不同数据保存至不同表
	 * 红外入侵传感器对应表   ：invade_data ：设备类型：0x10
	 * 空气温湿度传感器对应表：air_data    ：设备类型：0xB3
	 * 光照强度传感器对应表    ：beam_data   ：设备类型：0xC0
	 * 二氧化碳传感器对应表    ：co2_data    ：设备类型：0xD0
	 * 土壤温湿度传感器对应表：soil_data   ：设备类型：0xA5
	 * 所有设备都会变成小写
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
