package com.spordniar.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioSimpleServer {

	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(8888),1024);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("server init...");
		Charset charset = Charset.forName("gbk");
		new Thread( () -> {
			while (true) {
				SelectionKey key = null;
				try {
					selector.select(1000);
					Set<SelectionKey> selectionKeys = selector.selectedKeys();
					Iterator<SelectionKey> it = selectionKeys.iterator();
					while (it.hasNext()) {
						key = it.next();
						it.remove();
						if (key.isValid()) {
							Class<?> superClass=  key.channel().getClass().getSuperclass();
							// 客户端socket
							if (superClass == SocketChannel.class) {
								SocketChannel socketChannel = (SocketChannel)key.channel();
								if (key.isConnectable()) {
									if (socketChannel.finishConnect()) {
										System.out.println("client connect " + socketChannel.getLocalAddress());
										socketChannel.write(ByteBuffer.wrap("SPORDNIAR : Hello , ?".getBytes(charset)));
										socketChannel.register(selector, SelectionKey.OP_READ);
									}else {
										System.exit(1);
									}
								}
							}
							if (superClass == ServerSocketChannel.class) {
								if (key.isAcceptable()) {
									ServerSocketChannel serverSocketChannel2 = (ServerSocketChannel) key.channel();
									System.out.println("server connect " + serverSocketChannel2.getLocalAddress());
									SocketChannel socketChannel = serverSocketChannel2.accept();
									socketChannel.configureBlocking(false);
									socketChannel.write(ByteBuffer.wrap("SPORDNIAR : Hello , ?".getBytes(charset)));
									socketChannel.register(selector, SelectionKey.OP_READ);
								}
							}
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
						         } else if (readBytes < 0) {
						            key.cancel();
						            socketChannel.close();
						         }
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					key.cancel();
					try {
						key.channel().close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		}).start();
	}
	
}
