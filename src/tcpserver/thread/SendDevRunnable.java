package tcpserver.thread;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import tcpserver.server.TcpServer;

/**
 * �����豸���߳�
 * @author YangKuan
 *
 */
public class SendDevRunnable implements Runnable {
	
	/**
	 * ���͵�ʱ��
	 */
	static long sendTime;
	
	/**
	 * ���ճɹ���ʱ��
	 */
	static long successTime;
	
	/**
	 * ����ʧ�ܵ�ʱ��
	 */
	static long falseTime;
	
	/**
	 * �Ƿ���ճɹ�
	 */
	public static boolean acceptSuccess = false;
	
	/**
	 * �����жϵ�ǰ���͵����ݣ��ȴ������յ�״̬
	 * 0����ʼֵ
	 * 1�����ڷ���
	 * 2�����ճɹ�
	 * 3������3�ζ�δ���ճɹ����豸���ߵı�־��
	 */
	public static Map<String, Integer> acceptMap = new HashMap<>();
	
	/**
	 * ���͵��ͻ��˵�ͨ��
	 */
	private SocketChannel socketChannel;
	
	//��ʼ��
	public SendDevRunnable(SocketChannel socketChannel, String sendDevStrArray[]) {
		this.socketChannel = socketChannel;
		
		//��ʼ��acceptArray
		for(int i = 0; i < sendDevStrArray.length; i++) {
			acceptMap.put(sendDevStrArray[i], 0);
		}
	}
	
	/**
	 * ���������飨����������ݣ���ÿ�����ݷ���֮�󣬵ȴ����գ������5����δ�����ٴη��ͣ�
	 * ��������δ�������ж��豸���ߣ���һ�����ݵȴ���һ���������֮��ʱ��5����ٷ��ͣ�
	 * ��һ�������鷢�����֮��ʱ��30���ٴη���������������
	 */
	@Override
	public void run() {
		TcpServer tcpServer = new TcpServer();
		try {
			for(int count = 0;count < 5; count++) {
				//����map��һ��һ����������
				for(Map.Entry<String, Integer> mEntry : acceptMap.entrySet()) {
					//��¼���͵�ʱ��
					sendTime = System.currentTimeMillis();
					//���͵�һ�����ݣ�ÿ�������ڽ��ճɹ�֮ǰ����3��
					for(int i = 0; i < 3; i++) {
						//1����������
						tcpServer.sendDataClient(socketChannel, mEntry.getKey());
						//2�����õ�ǰ�������ݵ�valueֵΪ1(���ڷ���)
						acceptMap.put(mEntry.getKey(), 1);
						//3���жϣ�5��֮�����û�лظ��ٴη���
						for(int j = 0; j < 1000; j++) {
							//������ճɹ�
							if(acceptSuccess) {
								//���õ�ǰ���ݵı�־Ϊ2�����ճɹ���
								acceptMap.put(mEntry.getKey(), 2);
								break;
							}else {
								//ÿ0.5����һ���Ƿ���ճɹ�
								Thread.sleep(3);
							}
						}
						//���ѭ�����κ�i=2����acceptSuccess��Ϊfalse���ж�Ϊ����ʧ��
						if(!acceptSuccess && (i == 2)) {
							//��¼����ʧ�ܵ�ʱ��
							falseTime = System.currentTimeMillis();
							System.err.println(mEntry.getKey() + ":����ʧ�ܣ��豸���ߣ���ʱ:" + (falseTime - sendTime));
							acceptMap.put(mEntry.getKey(), 3);
							System.out.println();
						}
						//�����3���ڽ��ճɹ�
						if(acceptSuccess) {
							//��¼���ճɹ���ʱ��
							successTime = System.currentTimeMillis();
							System.out.println(mEntry.getKey() + ":���ճɹ����豸���ߣ���ʱ:" + (successTime - sendTime));
							break;
						}
					}
					//�������һ�����ݺ�
					//1������acceptSuccess=false
					acceptSuccess = false;
					//2���ȴ�5�뷢�͵�2������
					Thread.sleep(3000);
				}
				//��һ�η�����������֮��
				//1������acceptMapΪ0
				for(Map.Entry<String, Integer> mEntry : acceptMap.entrySet()) {
					acceptMap.put(mEntry.getKey(), 0);
				}
				//2���ȴ�30���ٴη�����һ��������
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
