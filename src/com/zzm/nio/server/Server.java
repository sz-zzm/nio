package com.zzm.nio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/*
 * @Description Nio服务器端
 * @Author zhanmin.zheng
 * @CreateDate 2016/02/08
 * @Version 1.0
 */
public class Server {
	
	private final int BLOCK_SIZE = 4096;//缓冲区大小
	
	private ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK_SIZE);//发送缓冲区
	
	private ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK_SIZE);//接受缓冲区
	
	private Selector selector;//选择器
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();//服务端channel开启
			serverSocketChannel.configureBlocking(false);//设置为非堵塞状态
			ServerSocket serverSocket = serverSocketChannel.socket();//服务端获取socket
			serverSocket.bind(endpoint);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * @Description construct method
	 * @param
	 * int 
	 */
	public Server(int port) {
		
	}
}
