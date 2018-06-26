package tcpserver.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import com.mchange.v2.c3p0.DataSources;

//获取c3p0连接
public class ConnectionMysql {
	private static final String JDBC_DRIVER = "driverClass";
	private static final String JDBC_URL = "jdbcUrl";
	
	private static DataSource ds;
	
	/**
	 * 初始化连接池代码块
	 */
	static {
		initDataSource();
	}
	/**
	 * 初始化c3p0连接池
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
				//加载驱动
				Class.forName(driverClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		//读取c3p0配置文件
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
		
		//建立连接池
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
