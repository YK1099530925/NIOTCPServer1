package tcpserver.thread;

import java.nio.channels.SocketChannel;

import tcpserver.server.TcpServer;

/**
 * 调用设备的线程
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
				System.out.println("第" + count + "次发送数据");
				for (String sendString : sendDevStrArray) {
					Thread.sleep(1000);
					tcpServer.sendDataClient(socketChannel, sendString);
				}
				// 休眠5秒
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 发送5次之后退出线程
			if (count == 100) {
				System.out.println("线程退出");
				return;
			}
		}
	}

}
