package tcpserver.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import tcpserver.utils.ConnectionMysql;

public class SqlHelper {
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rSet = null;

	public void selectAirAll(String sql) {
		try {
			conn = ConnectionMysql.getConnction();
			pstmt = conn.prepareStatement(sql);
			rSet = pstmt.executeQuery();
			ResultSetMetaData metaData = rSet.getMetaData();
			// 可以得到有多少列
			int columnCount = metaData.getColumnCount();
			while (rSet.next()) {
				for (int i = 0; i < columnCount; i++) {
					System.out.print(rSet.getObject(i + 1) + " ");
				}
				System.out.println("\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn, pstmt, rSet);
		}
	}

	/**
	 * 保存数据
	 */
	public void insert(String[] dataStr, String sql) {
		try {
			conn = ConnectionMysql.getConnction();
			pstmt = conn.prepareStatement(sql);
			for(int i = 0; i < dataStr.length; i++) {
				pstmt.setString(i+1, dataStr[i]);
			}
			if(pstmt.execute()) {
				System.out.println("保存成功");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn, pstmt, rSet);
		}
	}
	
	/**
	 * 关闭连接
	 */
	public void close(Connection conn,PreparedStatement pstmt, ResultSet rSet) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}				
		}
		if(rSet != null) {
			try {
				rSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
