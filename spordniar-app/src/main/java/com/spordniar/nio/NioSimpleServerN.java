package com.spordniar.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class NioSimpleServerN {

	public static void main(String[] args) throws IOException {
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.bind(new InetSocketAddress(8888));
		channel.configureBlocking(false);
		ByteBuffer dst = ByteBuffer.allocateDirect(1024);
		while (true) {
			SocketChannel socketChannel = channel.accept();
			if (socketChannel == null) {
				continue;
			}
			System.out.println("connect..... " + socketChannel.getRemoteAddress());
			int c = 0;
			while ((c = socketChannel.read(dst)) != -1) {
				dst.flip();
				System.out.println(Charset.forName("gbk").decode(dst).toString());
				dst.clear();
			};
		}
	}
	
}
