package com.zzm.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/*
 * @ClassName Nio客户端
 * @Author zhanmin.zheng
 * @CreateDate 2016/02/09
 * @Version 1.0
 */
public class Client {
	
	private static final int BLOCK_SIZE = 4096;//缓冲区大小
	
	private static int flag = 1;//发送数据条数
	
	/** 服务端地址 */
	private final static InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 7080);
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SocketChannel sockChannel = SocketChannel.open();
		sockChannel.configureBlocking(false);//设置非阻塞状态
		Selector selector = Selector.open();//打开选择器
		sockChannel.register(selector, SelectionKey.OP_CONNECT);//注册连接事件
		sockChannel.connect(serverAddress);//连接服务器
		
		ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK_SIZE);//发送缓冲区
		ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK_SIZE);//接受缓冲区
		Set<SelectionKey> selectionKeys;
		Iterator<SelectionKey> iterator;
		SelectionKey selectionKey;
		SocketChannel client;
		String receiveText;
		String sendText;
		int count = 0;
		
		while (true) {
			selectionKeys = selector.selectedKeys();
			System.out.println(selectionKeys);
			iterator = selectionKeys.iterator();
			while (iterator.hasNext()) {
				selectionKey = iterator.next();
				if (selectionKey.isConnectable()) {
					System.out.println("client start connect");
					client = (SocketChannel) selectionKey.channel();
					if (client.isConnectionPending()) {
						client.finishConnect();
						System.out.println("客户端完成连接操作");
						sendBuffer.clear();
						sendText = "你好";
						sendBuffer.put(sendText.getBytes());
						sendBuffer.flip();
						client.write(sendBuffer);
					}
					client.register(selector, SelectionKey.OP_READ);
				} else if (selectionKey.isReadable()) {
					client = (SocketChannel) selectionKey.channel();
					receiveBuffer.clear();
					count = client.read(receiveBuffer);
					if (count > 0) {
						receiveText = new String(receiveBuffer.array(), 0, count);
						System.out.println("客户端接受服务端数据："+receiveText);
						client.register(selector, SelectionKey.OP_WRITE);
					}
				} else if (selectionKey.isWritable()) {
					sendBuffer.clear();
					client = (SocketChannel) selectionKey.channel();
					sendText = "Msg send to Server -> "+flag++;
					sendBuffer.put(sendText.getBytes());
					sendBuffer.flip();
					client.write(sendBuffer);
					System.out.println("客户端发送数据给服务端"+sendText);
					client.register(selector, SelectionKey.OP_READ);
				}
			}
			selectionKeys.clear();
		}
	}

}
