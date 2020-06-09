package com.spordniar.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NioSimpleClient {

	public static void main(String[] args) throws IOException, InterruptedException {
		ExecutorService executorService = Executors.newCachedThreadPool();
		Callable<Integer> r = ()->{
			Selector selector = Selector.open();
			SocketChannel channel = SocketChannel.open(new InetSocketAddress("localhost", 8888));
			Charset charset = Charset.forName("gbk");
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_WRITE);
			channel.write(ByteBuffer.wrap("你好啊世界".getBytes(charset)));
			channel.register(selector, SelectionKey.OP_READ);
			channel.close();
			return 1;
		};
		executorService.invokeAll(Arrays.asList(r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r,r));
		executorService.awaitTermination(10, TimeUnit.SECONDS);
		executorService.shutdown();
//		if (channel.isConnected()) {
//			channel.register(selector, SelectionKey.OP_READ);
//		}else {
//			channel.register(selector, SelectionKey.OP_CONNECT);
//		}
//		new Thread(()->{
//			while (true) {
//				try {
//					selector.select(1000);
//					Set<SelectionKey> keys = selector.keys();
//					Iterator<SelectionKey> iterator = keys.iterator();
//					while (iterator.hasNext()) {
//						SelectionKey selectionKey = (SelectionKey) iterator.next();
//						iterator.remove();
//						if (selectionKey.isValid()) {
//							Class<?> superClass = selectionKey.channel().getClass().getSuperclass();
//							if (superClass == SocketChannel.class) {
//								SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
//								if (selectionKey.isConnectable()) {
//									socketChannel.write(ByteBuffer.wrap("hello".getBytes(charset)));
//									socketChannel.register(selector, SelectionKey.OP_CONNECT);
//								}else {
//									System.exit(1);
//								}
//							}
//						}
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
	}
	
}
