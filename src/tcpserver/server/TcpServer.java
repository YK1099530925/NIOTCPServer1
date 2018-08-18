package tcpserver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

import tcpserver.acceptappoint.AcceptAppoint;
import tcpserver.conversion.DataConversion;
import tcpserver.handledata.HandleData;
import tcpserver.savedata.SaveData;
import tcpserver.thread.SendDevRunnable;

public class TcpServer {
	
	//private static final int BUFSIZE = 1024;
	
	DataConversion dataConversion = new DataConversion();
	
	HandleData handleData = new HandleData();
	
	SaveData saveData = new SaveData();
	
	AcceptAppoint acceptAppoint = new AcceptAppoint();
	
	Thread sendThread;
	
	/**
	 * 1��������ʪ�ȣ�FE12245FCD0B004C001A13363136343239480231B3002C
	 * 
	 * 2��������ʪ�ȣ�FE12245FAC0F003F005410473435323236370231A50060
	 * 3��������ʪ�ȣ�FE12245FCA0B0048004813363136343239480231B3007D
	 * 4������ǿ��    ��FE12245F5A1B002D004E02473331383430380231C000FB
	 * 5��������̼    ��FE12245F9C130041005301473331383430380231D00057
	 */
	
	/**
	 * ���͵�������
	 * 
	 */
	private String sendDevStrArray[] = {
			"FE12245FCF0B002A003A0A473132323435310231B30071",
			"FE12245F671B0033003C0D473132323737350231C000A7"
			/*"FE12245FAC0F003F005410473435323236370231A50060",
			"FE12245FCA0B0048004813363136343239480231B3007D",
			"FE12245F5A1B002D004E02473331383430380231C000FB",
			"FE12245F9C130041005301473331383430380231D00057"*/
	};

	/**
	 * ��6789�˿ڣ������豸����
	 */
	public void tcpServer() {
		ByteBuffer byteBuffer;
		System.out.println("����������");
		try {
			// ����һ��ѡ����
			Selector selector = Selector.open();
			// ʵ����һ��ͨ��
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			// ��ͨ���󶨵�ָ���˿�(6789)
			serverSocketChannel.socket().bind(new InetSocketAddress(6789));
			// ����ͨ��Ϊ������ģʽ
			serverSocketChannel.configureBlocking(false);
			// ��ѡ����ע�ᵽͨ����
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			// ��ʼ���������Ĵ�С
			byteBuffer = ByteBuffer.allocateDirect(60);
			// ������ѯselect��������ȡ׼���õ�ͨ��������key��
			while (true) {
				// һֱ�ȴ���ֱ����ͨ��׼���������ݵĴ��䣬�ڴ˴��첽ִ����������3000Ϊselect�����ȴ��ŵ�׼���õ��ʱ�䣩
				if (selector.select(3000) == 0) {
					// �첽ִ����������
					continue;
				}
				// ��ȡ׼���õ�ͨ���й�����Key���ϵ�Iterator
				Iterator<SelectionKey> selectionKeyIter = selector.selectedKeys().iterator();
				// ѭ����ȡ�����еļ�ֵ
				while (selectionKeyIter.hasNext()) {
					SelectionKey key = selectionKeyIter.next();
					// ����˶������źŸ���Ȥ��ִ�����ֲ���
					if (key.isAcceptable()) {
						System.out.println("accept");
						// ���Ӻ��ˣ�Ȼ�󽫶�ע�ᵽѡ������
						readRegister(selector, key);
					}
					// ��һ������ע�ᵽѡ������֮������ͻ��˷������ݣ��Ϳ��Զ�ȡ�����ݣ������Խ����͵��ͻ���
					if (key.isReadable()) {
						// ��ȡ�ͻ��˵�����
						readDataFromSocket(key, byteBuffer);
					}
					if (key.isValid() && key.isWritable()) {
						System.out.println("write");
					}
					// ��Ҫ�ֶ��Ӽ������Ƴ���ǰkey
					selectionKeyIter.remove();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ����ע�ᵽѡ������
	public void readRegister(Selector selector, SelectionKey key) throws IOException {
		// ��key�л�ȡ������ͨ�����˴���ServerSocketChannel����Ϊ��Ҫ���������ļ��ģʽע�ᵽѡ�����У�
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		// ��ȡͨ��ʵ��
		SocketChannel channel = serverSocketChannel.accept();
		// ����Ϊ������ģʽ
		channel.configureBlocking(false);
		// ����ע�ᵽѡ������
		channel.register(selector, SelectionKey.OP_READ);
		
		//����һ���߳����ڷ���
		sendThread = new Thread(new SendDevRunnable(channel, sendDevStrArray));
		//sendThread.start();
	}

	//��������
	public void readDataFromSocket(SelectionKey key, ByteBuffer byteBuffer) throws Exception {
		// ����key������ͨ���л�ȡ���ݣ����Ȼ�ȡ������ͨ�����˴���SocketChannel����Ϊ��ͻ���ͨ����ͨ��SocketChannel�����ݶ��������У�
		SocketChannel socketChannel = (SocketChannel) key.channel();
		// ��ſͻ��˷��͹���������
		String dataString = "";
		int count;
		// ��ͨ���ж�ȡ���ݵ��������У��������û�������򷵻�-1
		while ((count = socketChannel.read(byteBuffer)) > 0) {
			// ��ģʽת��Ϊ��ģʽ
			byteBuffer.flip();
			// hasRemaining��֪��ǰλ�ú�����֮���Ƿ�����κ�Ԫ��
			while (byteBuffer.hasRemaining()) {
				// 1��ת���ͻ��˷��͹�����16��������ת��hexstring
				dataString = dataConversion.byteBufferToHexstring(byteBuffer);
				// 2���������ݣ�ֻ�����������������߼��жϣ�
				String[] handle = null;
				if(dataString != null) {
					handle = handleData.handle(dataString);
				}
				// 3����������ָ�����ݣ��߼��жϣ��жϵ�ǰ���ڷ��͵������Ƿ���������Ӧ���������Ӧ���򱣴棩
				//���ڷ��͵�����
				String sendingValueStr = "";
				if(handle != null) {
					//����acceptMap����ȡ���ڷ��͵�����(���������֮��û��һ�����ݴ��ڷ��ͣ����sendingValueStr��Ϊ��)
					for(Map.Entry<String, Integer> mEntry : SendDevRunnable.acceptMap.entrySet()) {
						if(mEntry.getValue() == 1) {
							sendingValueStr = mEntry.getKey();
						}
					}
					
					//������Ҫ���ܵ�����
					if(sendingValueStr.length() != 0 && acceptAppoint.interceptData(handle, sendingValueStr)) {
						//��ǰ��������Ӧ�������÷��ͳɹ���־
						SendDevRunnable.acceptSuccess = true;
						//��������
						saveData.saveData(handle);
					}
				}
			}
			// ������������˴�������ǲ���buffer�е����ݣ����ǻع������־λ������������һ����byteBuffer.hasRemaining()��Ϊfalse
			byteBuffer.clear();
		}
		if (count < 0) {
			socketChannel.close();
		}
	}

	// ��ͻ��˷�������
	public void sendDataClient(SocketChannel socketChannel, String str) throws IOException {
		ByteBuffer sentBuffer = ByteBuffer.allocateDirect(str.length());
		byte[] b = dataConversion.hexStringtoByteArray(str);
		sentBuffer.put(ByteBuffer.wrap(b));
		sentBuffer.flip();
		// ����ͨ��д���ݵ�ʱ����Ҫ��buffer��flip()
		socketChannel.write(sentBuffer);
	}
}
