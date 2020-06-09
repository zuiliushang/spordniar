package com.spordniar.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;

public class ChannelHandler {
	
	private AsynchronousSocketChannel channel;
	
	private Charset charset;
	
	public ChannelHandler(AsynchronousSocketChannel channel, Charset charset) {
		super();
		this.channel = channel;
		this.charset = charset;
	}

	public void writeAndFlush(Object msg) {
		byte[] bytes = msg.toString().getBytes(charset);
		ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		writeBuffer.put(bytes);
		writeBuffer.flip();
		channel.write(writeBuffer);
	}

	public AsynchronousSocketChannel getChannel() {
		return channel;
	}

	public void setChannel(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}
	
}
