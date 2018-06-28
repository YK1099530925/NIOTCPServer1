package tcpserver.handledata;

import java.text.SimpleDateFormat;
import java.util.Date;

//处理数据
public class HandleData {
	
	/**
	 * 处理数据
	 * 
	 * @param dataString
	 */
	public String[] handle(String dataString) {
		// 截取第三个16进制
		String str = dataString.substring((3 - 1) * 2, (4 - 1) * 2);
		// 判断是否是44，如果是，就进行存储，如果不是，就直接返回
		if (!str.equals("44")) {
			return null;
		}
		// 1、拆分数据
		String[] dataStr = splitData(dataString);
		// 2、解析数据
		if (dataStr.length == 5) {// 解析其他的数据
			dataStr = resolveOtherData(dataStr);
		} else if (dataStr.length == 6) {// 解析空气和土壤的数据
			dataStr = resolveAirAndSoilData(dataStr);
		} else {
			System.err.println("错误");
		}
		//处理完之后打印一下数据
		printData(dataStr);
		return dataStr;
	}
	
	//打印转换后的数据
	public void printData(String[] dataStr) {
		for (String str : dataStr) {
			System.err.println(str);
		}
	}
	
	/**
	 * 拆分数据(前三个数据是不变的，因此可以将前个数据提取出来) 第0个数据：无线id 第1个数据：设备标识 第2个数据：设备类型
	 * 
	 * @return
	 */
	public String[] splitData(String dataString) {
		// 判断（因为有温湿度的需要5个空间，co2和光照只需要4个空间）
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

	// 拆分前三个数据
	public String[] splitData1(String[] dataStr, String dataString) {
		dataStr[0] = dataString.substring((5 - 1) * 2, (5 - 1 + 2) * 2);
		dataStr[1] = dataString.substring((7 - 1) * 2, (7 - 1 + 12) * 2);
		dataStr[2] = dataString.substring((21 - 1) * 2, (21 - 1 + 1) * 2);
		return dataStr;
	}

	// 拆分后面的数据
	public String[] splitData2(String[] dataStr, String dataString) {
		Date dateNow = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int len = dataString.length() / 2;
		if (dataStr.length == 5) {
			// 判断，因为字段为4个的数据域的位也有不同
			if (len == 24) {// 长度是24个16进制数（截取倒数第2个）
				dataStr[3] = dataString.substring((len - 2) * 2, (len - 1) * 2);
			} else if (len == 25) {// 长度为25个16进制数(所以截取倒数第2和第3位)
				dataStr[3] = dataString.substring((len - 3) * 2, (len - 1) * 2);
			}
			dataStr[4] = df.format(dateNow);
		} else if (dataStr.length == 6) {
			if (dataStr[2].equals("b3")) {// b3：空气温湿度（前两位表示温度，后一位表示湿度）
				dataStr[3] = dataString.substring((len - 4) * 2, (len - 2) * 2);
				dataStr[4] = dataString.substring((len - 2) * 2, (len - 1) * 2);
			} else if (dataStr[2].equals("a5")) {// a5：土壤温湿度（前一位表示温度，后两位表示湿度），将后两位截取到dataStr[3]中，与b3保持一致
				dataStr[3] = dataString.substring((len - 3) * 2, (len - 1) * 2);
				dataStr[4] = dataString.substring((len - 4) * 2, (len - 3) * 2);
			}
			dataStr[5] = df.format(dateNow);
		} else {
			System.out.println("异常");
		}

		return dataStr;
	}
	
	/**
	 * 空气或土壤数据的解析
	 * 解析最后一个数据
	 * 温湿度（包含三个byte）
	 * 示例：31 02 1C
	 * 31 02: 
	 * 温度=  (0x0231/10)-40 = (561/10)-40 = 16.1C
	 * 1C: 湿度 = 0x1C% = 28%
	 * 
	 * @param dataStr
	 * @return
	 */
	public String[] resolveAirAndSoilData(String[] dataStr) {
		//温度换算
		String temperature = dataStr[3].substring(2, 4) + dataStr[3].substring(0, 2);
		double temFormatDouble = ((double) Integer.parseInt(temperature,16) - 400) / 10;
		//湿度转换
		String humidity = dataStr[4].substring(0, 2);
		dataStr[3] = temFormatDouble + "°C";
		dataStr[4] = Integer.parseInt(humidity, 16) + "";
		return dataStr;
	}
	
	/**
	 * 解析其他数据(红外入侵，光照强度，二氧化碳，数据转换成16进制)
	 * 红外入侵传感器对应表   ：invade_data ：设备类型：0x10
	 * 光照强度传感器对应表    ：beam_data   ：设备类型：0xC0
	 * 二氧化碳传感器对应表    ：co2_data    ：设备类型：0xD0
	 * @param dataStr
	 * @return
	 */
	public String[] resolveOtherData(String[] dataStr) {
		switch (dataStr[2]) {
		case "10"://红外入侵
			dataStr[3] = Integer.parseInt(dataStr[3], 16) + "";
			break;
		case "c0"://光照强度
			String beamStr = dataStr[3].substring(2, 4) + dataStr[3].substring(0, 2);
			dataStr[3] = Integer.parseInt(beamStr, 16) + "Lux";
			break;
		case "d0"://二氧化碳
			String co2 = dataStr[3].substring(2, 4) + dataStr[3].substring(0, 2);
			dataStr[3] = Integer.parseInt(co2, 16) + "ppm";
			break;
		default:
			break;
		}
		return dataStr;
	}
}
