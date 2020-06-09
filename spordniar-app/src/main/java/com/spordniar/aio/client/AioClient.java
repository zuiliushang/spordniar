package com.spordniar.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AioClient {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
		Future<Void> future = socketChannel.connect(new InetSocketAddress("127.0.0.1", 8888));
		future.get();
		socketChannel.read(ByteBuffer.allocate(1024), null, new AioClientHandler(socketChannel, Charset.forName("utf-8")));
		Thread.sleep(100000);
	}
	
}
