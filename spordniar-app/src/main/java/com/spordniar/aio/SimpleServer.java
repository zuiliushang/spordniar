package com.spordniar.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleServer extends Thread{
	AsynchronousServerSocketChannel serverSocketChannel;
	@Override
	public void run() {
		try {
			serverSocketChannel = AsynchronousServerSocketChannel
					.open(AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 10));
			serverSocketChannel.bind(new InetSocketAddress(8888));
			CountDownLatch latch = new CountDownLatch(1);
			serverSocketChannel.accept(this,
					new CompletionHandler<AsynchronousSocketChannel, SimpleServer>() {
						final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
						final long timeout = 60 * 60L;
						final Charset charset = Charset.forName("GBK");
						@Override
						public void completed(AsynchronousSocketChannel result, SimpleServer attachment) {
							try {
								result.read(byteBuffer, timeout, TimeUnit.SECONDS, attachment,
											new MyHandler(result, charset)
										);
							} finally {
								attachment.serverSocketChannel.accept(attachment, this);
							}
						}
						@Override
						public void failed(Throwable exc, SimpleServer attachment) {
							exc.getStackTrace();
						}
					});
			latch.await();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private static class MyHandler implements CompletionHandler<Integer, Object> {
		AsynchronousSocketChannel socketChannel;
		Charset charset;
		public MyHandler(AsynchronousSocketChannel socketChannel, Charset charset) {
			super();
			this.socketChannel = socketChannel;
			this.charset = charset;
			if (socketChannel.isOpen()) {
				try {
					System.out.println("receive connect :" + socketChannel.getRemoteAddress());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		final ByteBuffer buffer = ByteBuffer.allocate(1024);
		final long timeout = 60 * 60l;
		@Override
		public void completed(Integer res, Object attachment) {
			if (res == -1) {
				System.out.println("server final");
				try {
					socketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
			buffer.flip();
			CharBuffer charBuffer = charset.decode(buffer);
			buffer.clear();
			System.out.print(charBuffer);
			socketChannel.write(ByteBuffer.wrap("已经处理完成".getBytes(charset)));
			socketChannel.read(buffer, timeout, TimeUnit.SECONDS, attachment, this);
		}
		@Override
		public void failed(Throwable exc, Object attachment) {
			exc.getStackTrace();
		}
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		new SimpleServer().start();
	}
	
}
