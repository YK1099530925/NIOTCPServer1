package tcpserver.main;

import tcpserver.server.TcpServer;

public class ServerMain {

	public static void main(String[] args) {
		TcpServer tcpServer = new TcpServer();
		tcpServer.tcpServer();
	}

}
