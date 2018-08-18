package tcpserver.thread;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import tcpserver.server.TcpServer;

/**
 * 调用设备的线程
 * @author YangKuan
 *
 */
public class SendDevRunnable implements Runnable {
	
	/**
	 * 发送的时间
	 */
	static long sendTime;
	
	/**
	 * 接收成功的时间
	 */
	static long successTime;
	
	/**
	 * 接收失败的时间
	 */
	static long falseTime;
	
	/**
	 * 是否接收成功
	 */
	public static boolean acceptSuccess = false;
	
	/**
	 * 用于判断当前发送的数据，等待被接收的状态
	 * 0：初始值
	 * 1：正在发送
	 * 2：接收成功
	 * 3：发送3次都未接收成功（设备掉线的标志）
	 */
	public static Map<String, Integer> acceptMap = new HashMap<>();
	
	/**
	 * 发送到客户端的通道
	 */
	private SocketChannel socketChannel;
	
	//初始化
	public SendDevRunnable(SocketChannel socketChannel, String sendDevStrArray[]) {
		this.socketChannel = socketChannel;
		
		//初始化acceptArray
		for(int i = 0; i < sendDevStrArray.length; i++) {
			acceptMap.put(sendDevStrArray[i], 0);
		}
	}
	
	/**
	 * 发送数据组（包含多个数据），每个数据发送之后，等待接收，如果在5秒内未接收再次发送，
	 * 连续三次未接受则判断设备掉线；下一个数据等待第一个数据完成之后时隔5秒后再发送，
	 * 当一个数据组发送完成之后，时隔30秒再次发送数据组数据组
	 */
	@Override
	public void run() {
		TcpServer tcpServer = new TcpServer();
		try {
			for(int count = 0;count < 5; count++) {
				//遍历map，一个一个发送数据
				for(Map.Entry<String, Integer> mEntry : acceptMap.entrySet()) {
					//记录发送的时间
					sendTime = System.currentTimeMillis();
					//发送第一个数据，每个数据在接收成功之前发送3次
					for(int i = 0; i < 3; i++) {
						//1、发送数据
						tcpServer.sendDataClient(socketChannel, mEntry.getKey());
						//2、设置当前发送数据的value值为1(正在发送)
						acceptMap.put(mEntry.getKey(), 1);
						//3、判断：5秒之内如果没有回复再次发送
						for(int j = 0; j < 1000; j++) {
							//如果接收成功
							if(acceptSuccess) {
								//设置当前数据的标志为2（接收成功）
								acceptMap.put(mEntry.getKey(), 2);
								break;
							}else {
								//每0.5秒检测一次是否接收成功
								Thread.sleep(3);
							}
						}
						//如果循环三次后（i=2），acceptSuccess还为false，判断为接收失败
						if(!acceptSuccess && (i == 2)) {
							//记录接收失败的时间
							falseTime = System.currentTimeMillis();
							System.err.println(mEntry.getKey() + ":接收失败，设备掉线，用时:" + (falseTime - sendTime));
							acceptMap.put(mEntry.getKey(), 3);
							System.out.println();
						}
						//如果在3次内接收成功
						if(acceptSuccess) {
							//记录接收成功的时间
							successTime = System.currentTimeMillis();
							System.out.println(mEntry.getKey() + ":接收成功，设备在线，用时:" + (successTime - sendTime));
							break;
						}
					}
					//发送完第一个数据后
					//1、设置acceptSuccess=false
					acceptSuccess = false;
					//2、等待5秒发送第2个数据
					Thread.sleep(3000);
				}
				//第一次发送完数据组之后
				//1、重置acceptMap为0
				for(Map.Entry<String, Integer> mEntry : acceptMap.entrySet()) {
					acceptMap.put(mEntry.getKey(), 0);
				}
				//2、等待30秒再次发送下一个数据组
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
