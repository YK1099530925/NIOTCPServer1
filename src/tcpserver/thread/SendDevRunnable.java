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
		for(;;) {
			try {
				count++;
				for(String sendString : sendDevStrArray) {
					System.out.println("发送数据：" + sendString);
					tcpServer.sendDataClient(socketChannel, sendString);
				}
				//休眠10秒
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//发送5次之后推出线程
			if(count == 5) {
				return;
			}
		}
	}

}
