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
 * @ClassName Nio��������
 * @Author zhanmin.zheng
 * @CreateDate 2016/02/08
 * @Version 1.0
 */
public class Server {
	
	private final int BLOCK_SIZE = 4096;//��������С
	
	private ByteBuffer sendBuffer = ByteBuffer.allocate(BLOCK_SIZE);//���ͻ�����
	
	private ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK_SIZE);//���ܻ�����
	
	private Selector selector;//ѡ����
	
	private int flag = 1;//������������
	
	/*
	 * @Description construct method
	 * @Param port �˿�
	 * @Return IOException 
	 */
	public Server(int port) throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();//�����channel����
		serverSocketChannel.configureBlocking(false);//����Ϊ�Ƕ���״̬
		ServerSocket serverSocket = serverSocketChannel.socket();//����˻�ȡsocket
		serverSocket.bind(new InetSocketAddress(port));//����socket�˿�
		selector = Selector.open();//��ѡ����
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//������ע����ѡ���� *���ڿ�ʼ�Ѿ������˿���
		System.out.println("Server start listen "+port);
		System.out.println(selector.selectedKeys());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 7080;
		try {
			Server server = new Server(port);
			server.listen();//��ʼ����
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * @Description ����
	 * @Throws IOEception
	 */
	public void listen() throws IOException {
		while(true) {
			selector.select();//��ȡ�¼��б�
			Set<SelectionKey> selectionKeys = selector.selectedKeys();//��ȡselectkey�ļ���
			Iterator<SelectionKey> iterator = selectionKeys.iterator();//set���ϵĵ���
			while (iterator.hasNext()) {//����SelectionKey
				SelectionKey selectionKey = iterator.next();
				iterator.remove();
				this.handleKey(selectionKey);
			}
		}
	}
	
	/*
	 * @Description ҵ���߼�
	 * @Param selectionKey ѡ����key
	 * @Throws IOException
	 */
	public void handleKey(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel server = null;//�����ͨ��
		SocketChannel client = null;//�ͻ���ͨ��
		String receiveText;//��������
		String sendText;//��������
		int count = 0;
		if (selectionKey.isAcceptable()) {
			server = (ServerSocketChannel) selectionKey.channel();//�����channel
			client = server.accept();//�ͻ���channel
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);//ע����¼�
		} else if (selectionKey.isReadable()) {//���¼�
			client = (SocketChannel) selectionKey.channel();
			count = client.read(receiveBuffer);
			if (count > 0){
				receiveText = new String(receiveBuffer.array(), 0, count);
				System.out.println("����˽��յ��ͻ�����Ϣ��"+receiveText);
				client.register(selector, SelectionKey.OP_WRITE);//�ͻ���ע��д�¼�
			}
		} else if(selectionKey.isWritable()) {//д�¼�
			sendBuffer.clear();//��շ��ͻ�����
			client = (SocketChannel) selectionKey.channel();
			sendText = "msg send to client"+flag++;//���͵���Ϣ
			sendBuffer.put(sendText.getBytes());//����Ϣ���ֽڷ�ʽ���뻺����
			sendBuffer.flip();//��ת������
			client.write(sendBuffer);//����
			System.out.println("����˷������ݸ��ͻ��ˣ�"+sendText);
		}
	}
}
