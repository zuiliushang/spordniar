package com.spordniar.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class SimpleReactorServer {

	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(8888));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		Charset charset = Charset.forName("gbk");
		ReactorServer reactorServer = new ReactorServer(selector, charset);
		System.out.println("hello spordniar");
		reactorServer.run();
	}
	private static class ReactorServer{
		private Selector selector;
		private Charset charset;
		public ReactorServer(Selector selector, Charset charset) {
			super();
			this.selector = selector;
			this.charset = charset;
		}
		public void run() {
			try {
				while (selector.select() > 0) {
					SelectionKey key = null;
					//selector.select(1000);
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> it = keys.iterator();
					while (it.hasNext()) {
						key = it.next();
						it.remove();
						if (key.isValid()) {
							dispatcher(key);
						}	
					}	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private void dispatcher(SelectionKey key) throws IOException {
			if (key.isConnectable()) {
				handlerConnect(key);
			}else if (key.isAcceptable()) {
				// 只处理链接
				handlerAccept(key);
			}else if (key.isReadable()) {
				// read
				handlerRead(key);
			}
		}
		private void handlerConnect(SelectionKey key) throws IOException {
			System.out.println("qiiiiiii");
//			if (key.channel().getClass().getSuperclass() == SocketChannel.class) {
//				SocketChannel socketChannel = (SocketChannel) key.channel();
//				if (socketChannel.finishConnect()) {
//					System.out.println("connect finishing : " + socketChannel.getRemoteAddress());
//					socketChannel.write(ByteBuffer.wrap("connect finish, heel".getBytes(charset)));
//					ByteBuffer buffer = ByteBuffer.allocate(1024);
//					socketChannel.register(selector, SelectionKey.OP_READ, buffer);
//				}else {
//					System.exit(1);
//				}
//			}
		}
		private void handlerRead(SelectionKey key) throws IOException {
			if (key.isReadable()) {
				SocketChannel socketChannel = (SocketChannel) key.channel();
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = socketChannel.read(readBuffer);
				 if (readBytes > 0) {
		            readBuffer.flip();
		            byte[] bytes = new byte[readBuffer.remaining()];
		            readBuffer.get(bytes);
		            System.out.println(new String(bytes, charset));
		        	socketChannel.write(ByteBuffer.wrap("处理好了".getBytes(charset)));
		         } else if (readBytes < 0 ) {
		            key.cancel();
		            socketChannel.close();
		         }
			}
//			if (key.channel().getClass() == SocketChannel.class) {
//				SocketChannel socketChannel = (SocketChannel) key.channel();
//				socketChannel.configureBlocking(false);
//				ByteBuffer buffer = ByteBuffer.allocate(1024);
//				buffer.clear();
//				while (socketChannel.read(buffer) != -1) {
//					buffer.flip();
//					String msg = charset.decode(buffer).toString();
//					buffer.clear();
//					System.out.println("msg ====== " + msg);
//				}
//			}
		}
		private void handlerAccept(SelectionKey key) {
			if (key.channel().getClass().getSuperclass() == ServerSocketChannel.class) {
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
				try {
					SocketChannel socketChannel = serverSocketChannel.accept();
					socketChannel.configureBlocking(false);
					System.out.println("建立链接: " + socketChannel.getRemoteAddress());
					socketChannel.write(ByteBuffer.wrap("connect finish, heel".getBytes(charset)));
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					socketChannel.register(selector, SelectionKey.OP_READ,buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
