package tcpserver.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import com.mchange.v2.c3p0.DataSources;

//��ȡc3p0����
public class ConnectionMysql {
	private static final String JDBC_DRIVER = "driverClass";
	private static final String JDBC_URL = "jdbcUrl";
	
	private static DataSource ds;
	
	/**
	 * ��ʼ�����ӳش����
	 */
	static {
		initDataSource();
	}
	/**
	 * ��ʼ��c3p0���ӳ�
	 */
	public static final void initDataSource() {
		Properties c3p0Properties = new Properties();
		try {
			c3p0Properties.load(ConnectionMysql.class.getResourceAsStream("/c3p0.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String driverClass = c3p0Properties.getProperty(JDBC_DRIVER);
		if(driverClass != null) {
			try {
				//��������
				Class.forName(driverClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		//��ȡc3p0�����ļ�
		Properties c3p0Pro = new Properties();
		Properties jdbcPro = new Properties();
		for(Object key : c3p0Properties.keySet()) {
			String skey = (String)key;
			if(skey.startsWith("c3p0.")) {
				c3p0Pro.put(skey, c3p0Properties.getProperty(skey));
			}else {
				jdbcPro.put(skey, c3p0Properties.getProperty(skey));
			}
		}
		
		//�������ӳ�
		try {
			DataSource unPool = DataSources.unpooledDataSource(c3p0Properties.getProperty(JDBC_URL), jdbcPro);
			ds = DataSources.pooledDataSource(unPool, c3p0Pro);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized Connection getConnction() throws SQLException {
		Connection conn = ds.getConnection();
		return conn;
	}
}
