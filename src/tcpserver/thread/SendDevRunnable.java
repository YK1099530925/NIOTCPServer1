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
		for(;;) {
			try {
				count++;
				for(String sendString : sendDevStrArray) {
					System.out.println("�������ݣ�" + sendString);
					tcpServer.sendDataClient(socketChannel, sendString);
				}
				//����10��
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//����5��֮���Ƴ��߳�
			if(count == 5) {
				return;
			}
		}
	}

}
