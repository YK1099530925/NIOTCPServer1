package tcpserver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import tcpserver.acceptappoint.AcceptAppoint;
import tcpserver.conversion.DataConversion;
import tcpserver.handledata.HandleData;
import tcpserver.savedata.SaveData;
import tcpserver.thread.SendDevRunnable;

public class TcpServer {
	
	private static final int BUFSIZE = 1024;
	
	DataConversion dataConversion = new DataConversion();
	
	HandleData handleData = new HandleData();
	
	SaveData saveData = new SaveData();
	
	AcceptAppoint acceptAppoint = new AcceptAppoint();
	
	/**
	 * 1：空气温湿度：FE12245FCD0B004C001A13363136343239480231B3002C
	 * 2：空气温湿度：FE12245FCA0B0048004813363136343239480231B3007D
	 * 3：二氧化碳    ：FE12245F9C130041005301473331383430380231D00057
	 * 4：土壤温湿度：FE12245FAC0F003F005410473435323236370231A50060
	 * 5：光照强度    ：FE12245F5A1B002D004E02473331383430380231C000FB
	 */
	
	/**
	 * 调用设备组 "FE12245FCD0B004C001A13363136343239480231B3002C",
	 * 
	 */
	private String sendDevStrArray[] = {
			
			"FE12245FCA0B0048004813363136343239480231B3007D",
			"FE12245F9C130041005301473331383430380231D00057",
			"FE12245FAC0F003F005410473435323236370231A50060",
			"FE12245F5A1B002D004E02473331383430380231C000FB"
	};
	
	/**
	 * 接收数据组（存放设备标志），用于判断是否是我们调用的设备发送的数据
	 */
	private String acceptAppointStrArray[] = new String[sendDevStrArray.length];

	public void tcpServer() {
		ByteBuffer byteBuffer;
		System.out.println("服务器启动");
		try {
			// 创建一个选择器
			Selector selector = Selector.open();
			// 实例化一个通道
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			// 将通道绑定到指定端口(6789)
			serverSocketChannel.socket().bind(new InetSocketAddress(6789));
			// 配置通道为非阻塞模式
			serverSocketChannel.configureBlocking(false);
			// 将选择器注册到通道上
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			// 初始化缓冲区的大小
			byteBuffer = ByteBuffer.allocateDirect(60);
			// 不断轮询select方法，获取准备好的通道关联的key集
			while (true) {
				// 一直等待，直到有通道准备好了数据的传输，在此处异步执行其他任务（3000为select方法等待信道准备好的最长时间）
				if (selector.select(3000) == 0) {
					// 异步执行其他任务
					continue;
				}
				// 获取准备好的通道中关联的Key集合的Iterator
				Iterator<SelectionKey> selectionKeyIter = selector.selectedKeys().iterator();
				// 循环获取集合中的键值
				while (selectionKeyIter.hasNext()) {
					SelectionKey key = selectionKeyIter.next();
					// 服务端对哪种信号感兴趣就执行那种操作
					if (key.isAcceptable()) {
						System.out.println("accept");
						// 连接好了，然后将读注册到选择器中
						readRegister(selector, key);
					}
					// 上一部将读注册到选择器中之后，如果客户端发送数据，就可以读取到数据，还可以将发送到客户端
					if (key.isReadable()) {
						// 读取客户端的数据
						readDataFromSocket(key, byteBuffer);
					}
					if (key.isValid() && key.isWritable()) {
						System.out.println("write");
					}
					// 需要手动从键集中移除当前key
					selectionKeyIter.remove();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 将读注册到选择器中
	public void readRegister(Selector selector, SelectionKey key) throws IOException {
		// 从key中获取关联的通道（此处是ServerSocketChannel，因为需要将服务器的检测模式注册到选择器中）
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		// 获取通道实例
		SocketChannel channel = serverSocketChannel.accept();
		// 设置为非阻塞模式
		channel.configureBlocking(false);
		// 将读注册到选择器中
		channel.register(selector, SelectionKey.OP_READ);
		
		//获取接收组
		acceptAppointStrArray = acceptAppoint.acceptData(sendDevStrArray);
		
		//开启一个线程用于发送
		Thread sendThread = new Thread(new SendDevRunnable(channel, sendDevStrArray));
		sendThread.start();
	}

	//处理数据
	public void readDataFromSocket(SelectionKey key, ByteBuffer byteBuffer) throws Exception {
		// 从与key关联的通道中获取数据，首先获取关联的通道（此处是SocketChannel，因为与客户端通信是通过SocketChannel，数据都放在其中）
		SocketChannel socketChannel = (SocketChannel) key.channel();
		// 存放客户端发送过来的数据
		String dataString = "";
		int count;
		// 清除缓冲区（此处清除不能实际擦出buffer中的数据，而是回归各个标志位）
		byteBuffer.clear();
		// 从通道中读取数据到缓冲区中，读到最后没有数据则返回-1
		while ((count = socketChannel.read(byteBuffer)) > 0) {
			// 将模式转换为读模式
			byteBuffer.flip();
			// hasRemaining告知当前位置和限制之间是否存在任何元素
			while (byteBuffer.hasRemaining()) {
				// 1、解析客户端发送过来的16进制数据(返回的是String])
				// ①将byteBuffer转换为byte[]数组
				byte[] dataByte = dataConversion.byteBufferToByteArray(byteBuffer);
				// ②将byte[]转换成String
				dataString = dataConversion.byteArraytoHexString(dataByte);
				// 2、处理数据
				String[] handle = null;
				if(dataString != null) {
					handle = handleData.handle(dataString);
				}
				// 3、保存数据指定数据
				if(handle != null) {
					//System.out.println("wifiid:" + handle[0]);
					//保存我们指定接收的数据
					if(acceptAppoint.interceptData(handle, acceptAppointStrArray)) {
						//打印将保存数据
						handleData.printData(handle);
						saveData.saveData(handle);
					}
				}
			}
			byteBuffer.clear();
		}
		if (count < 0) {
			socketChannel.close();
		}
	}

	// 向客户端发送数据
	public void sendDataClient(SocketChannel socketChannel, String str) throws IOException {
		ByteBuffer sentBuffer = ByteBuffer.allocateDirect(str.length());
		byte[] b = dataConversion.hexStringtoByteArray(str);
		sentBuffer.put(ByteBuffer.wrap(b));
		sentBuffer.flip();
		// 在向通道写数据的时候，需要将buffer给flip()
		socketChannel.write(sentBuffer);
	}
}
