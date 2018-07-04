package tcpserver.thread;

import java.nio.channels.SocketChannel;

import tcpserver.server.TcpServer;

/**
 * �����豸���߳�
 * @author YangKuan
 *
 */
public class SendDevRunnable implements Runnable {
	
	private String sendDevStrArray[];
	
	private SocketChannel socketChannel;
	
	public SendDevRunnable(SocketChannel socketChannel, String sendDevStrArray[]) {
		this.sendDevStrArray = sendDevStrArray;
		this.socketChannel = socketChannel;
	}

	@Override
	public void run() {
		TcpServer tcpServer = new TcpServer();
		int count = 0;
		for (;;) {
			try {
				count++;
				System.out.println("��" + count + "�η�������");
				for (String sendString : sendDevStrArray) {
					Thread.sleep(1000);
					tcpServer.sendDataClient(socketChannel, sendString);
				}
				// ����5��
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ����5��֮���˳��߳�
			if (count == 100) {
				System.out.println("�߳��˳�");
				return;
			}
		}
	}

}
