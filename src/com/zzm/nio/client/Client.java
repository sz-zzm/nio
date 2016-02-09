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
 * @ClassName Nio�ͻ���
 * @Author zhanmin.zheng
 * @CreateDate 2016/02/09
 * @Version 1.0
 */
public class Client {
	
	private static final int BLOCK_SIZE = 4096;//��������С
	
	private static int flag = 1;//������������
	
	/** ����˵�ַ */
	private final static InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 7080);
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SocketChannel sockChannel = SocketChannel.open();
		sockChannel.configureBlocking(false);//���÷�����״̬
		Selector selector = Selector.open();//��ѡ����
		sockChannel.register(selector, SelectionKey.OP_CONNECT);//ע�������¼�
		sockChannel.connect(serverAddress);//���ӷ�����
		
		ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK_SIZE);//���ͻ�����
		ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK_SIZE);//���ܻ�����
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
						System.out.println("�ͻ���������Ӳ���");
						sendBuffer.clear();
						sendText = "���";
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
						System.out.println("�ͻ��˽��ܷ�������ݣ�"+receiveText);
						client.register(selector, SelectionKey.OP_WRITE);
					}
				} else if (selectionKey.isWritable()) {
					sendBuffer.clear();
					client = (SocketChannel) selectionKey.channel();
					sendText = "Msg send to Server -> "+flag++;
					sendBuffer.put(sendText.getBytes());
					sendBuffer.flip();
					client.write(sendBuffer);
					System.out.println("�ͻ��˷������ݸ������"+sendText);
					client.register(selector, SelectionKey.OP_READ);
				}
			}
			selectionKeys.clear();
		}
	}

}
