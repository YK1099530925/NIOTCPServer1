package tcpserver.main;

import tcpserver.server.TcpServer;

public class Main {

	public static void main(String[] args) {
		TcpServer tcpServer = new TcpServer();
		tcpServer.tcpServer();
	}

}
