package com.spordniar.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SimpleClient {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
		Future<Void> future = socketChannel.connect(new InetSocketAddress("localhost", 8888));
		future.get();
		socketChannel.read(ByteBuffer.allocate(1024), 10, TimeUnit.SECONDS, null, new CompletionHandler<Integer, Object>() {
			final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			final long timeout = 60*60l;
			final Charset charset = Charset.forName("gbk");
			@Override
			public void completed(Integer result, Object attachment) {
				if (result == -1) {
					System.out.println("final");
					try {
						socketChannel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else {
					byteBuffer.flip();
					CharBuffer charBuffer = charset.decode(byteBuffer);
					byteBuffer.clear();
					charBuffer.flip();
					socketChannel.write(ByteBuffer.wrap(charBuffer.toString().getBytes(charset)));
					socketChannel.read(byteBuffer, timeout, TimeUnit.SECONDS, attachment, this);
				}
			}
			@Override
			public void failed(Throwable exc, Object attachment) {
				exc.getStackTrace();
			}
		});
		System.out.println("client init");
		Thread.sleep(1000000);
	}
	
}
