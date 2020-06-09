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

public class SimpleReactorServer01 {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		new Reactor().run();
	}
	
	private static interface Handler{
		void process(SelectionKey key, Charset charset);
	}
	
	private static class AcceptHandler implements Handler{
		@Override
		public void process( SelectionKey key, Charset charset) {
			try {
				if (key.channel().getClass().getSuperclass() == ServerSocketChannel.class) {
					ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
					SocketChannel socketChannel = serverSocketChannel.accept();
					if (socketChannel.finishConnect()) {
						System.out.println("connect done:" + socketChannel.getRemoteAddress());
						socketChannel.configureBlocking(false);
						socketChannel.write(ByteBuffer.wrap("hello spordniar 服务".getBytes(charset)));
						key.attach(new ReadHandler());
						socketChannel.register(key.selector(), SelectionKey.OP_READ);
					}else {
						System.exit(1);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private static class ReadHandler implements Handler{
		@Override
		public void process( SelectionKey key, Charset charset) {
			try {
				if (key.channel().getClass().getSuperclass() == SocketChannel.class) {
					SocketChannel socketChannel = (SocketChannel) key.channel();
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					int read = socketChannel.read(readBuffer);
					if (read > 0) {
						readBuffer.flip();
						byte[] bs = new byte[readBuffer.remaining()];
						readBuffer.get(bs);
						System.out.println(new String(bs, charset));
						socketChannel.write(ByteBuffer.wrap("处理完成".getBytes()));
					}else {
						key.cancel();
						key.channel().close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	private static class Reactor{
		Selector selector;
		Charset charset;
		public Reactor() {
			super();
			try {
				Selector selector = Selector.open();
				this.charset = Charset.forName("gbk");
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
				serverSocketChannel.configureBlocking(false);
				serverSocketChannel.socket().bind(new InetSocketAddress(8888));
				SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				selectionKey.attach(new AcceptHandler());
				this.selector = selector;
				System.out.println("init....");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void run() {
			try {
				while (true) {
					SelectionKey key = null;
					selector.select();
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> it = keys.iterator();
					while (it.hasNext()) {
						key = it.next();
						if (key.isValid()) {
							dispatcher(key);
						}
						it.remove();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		private void dispatcher(SelectionKey key) {
			//TODO bugfix
			Handler handler = (Handler) key.attachment();
			if (handler != null) {
				handler.process( key, charset);
			}	
		}
	}
}
