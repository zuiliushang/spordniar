package com.spordniar.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import com.spordniar.aio.ChannelInitializer;

public class AioServerChannelInitializer extends ChannelInitializer {

	@Override
	protected void initChannel(AsynchronousSocketChannel channel) {
		channel.read(ByteBuffer.allocate(1024), 10, TimeUnit.SECONDS, null, new AioServerHandler(channel, Charset.forName("utf-8")));
	}

}
