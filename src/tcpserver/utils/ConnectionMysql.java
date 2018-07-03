package tcpserver.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import com.mchange.v2.c3p0.DataSources;

/**
 * c3p0连接池，获取数据库连接
 * @author YangKuan
 *
 */
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
		//第一个问题：出现An attempt by a client to checkout a Connection has timed out.连接超时的现象(原因是因为在连接完数据库之后，没有进行close操作，导致连接一直被占用，然后导致一直打开新的连接，最后连接超出最大连接数)
		Connection conn = ds.getConnection();
		return conn;
	}
}
