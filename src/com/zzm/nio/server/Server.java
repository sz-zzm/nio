package com.zzm.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/*
 * @ClassName Nio服务器端
 * @Author zhanmin.zheng
 * @CreateDate 2016/02/08
 * @Version 1.0
 */
public class Server {
	
	private final int BLOCK_SIZE = 4096;//缓冲区大小
	
	private ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK_SIZE);//发送缓冲区
	
	private ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK_SIZE);//接受缓冲区
	
	private Selector selector;//选择器
	
	private int flag = 1;//发送数据条数
	
	/*
	 * @Description construct method
	 * @Param port 端口
	 * @Return IOException 
	 */
	public Server(int port) throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();//服务端channel开启
		serverSocketChannel.configureBlocking(false);//设置为非堵塞状态
		ServerSocket serverSocket = serverSocketChannel.socket();//服务端获取socket
		serverSocket.bind(new InetSocketAddress(port));//设置socket端口
		selector = Selector.open();//打开选择器
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//将监听注册至选择器 *现在开始已经监听端口了
		System.out.println("Server start listen "+port);
		System.out.println(selector.selectedKeys());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 7080;
		try {
			Server server = new Server(port);
			server.listen();//开始监听
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * @Description 监听
	 * @Throws IOEception
	 */
	public void listen() throws IOException {
		while(true) {
			selector.select();//获取事件列表
			Set<SelectionKey> selectionKeys = selector.selectedKeys();//获取selectkey的集合
			Iterator<SelectionKey> iterator = selectionKeys.iterator();//set集合的迭代
			while (iterator.hasNext()) {//遍历SelectionKey
				SelectionKey selectionKey = iterator.next();
				iterator.remove();
				this.handleKey(selectionKey);
			}
		}
	}
	
	/*
	 * @Description 业务逻辑
	 * @Param selectionKey 选择器key
	 * @Throws IOException
	 */
	public void handleKey(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel server = null;//服务端通道
		SocketChannel client = null;//客户端通道
		String receiveText;//接收文字
		String sendText;//发送文字
		int count = 0;
		if (selectionKey.isAcceptable()) {
			server = (ServerSocketChannel) selectionKey.channel();//服务端channel
			client = server.accept();//客户端channel
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);//注册读事件
		} else if (selectionKey.isReadable()) {//读事件
			client = (SocketChannel) selectionKey.channel();
			count = client.read(receiveBuffer);
			if (count > 0){
				receiveText = new String(receiveBuffer.array(), 0, count);
				System.out.println("服务端接收到客户端信息："+receiveText);
				client.register(selector, SelectionKey.OP_WRITE);//客户端注册写事件
			}
		} else if(selectionKey.isWritable()) {//写事件
			sendBuffer.clear();//清空发送缓冲区
			client = (SocketChannel) selectionKey.channel();
			sendText = "msg send to client"+flag++;//发送的信息
			sendBuffer.put(sendText.getBytes());//将信息以字节方式放入缓冲区
			sendBuffer.flip();//反转缓冲区
			client.write(sendBuffer);//发送
			System.out.println("服务端发送数据给客户端："+sendText);
		}
	}
}
