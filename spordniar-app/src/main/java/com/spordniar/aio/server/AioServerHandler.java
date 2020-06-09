package com.spordniar.aio.server;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;

import com.spordniar.aio.ChannelAdapter;
import com.spordniar.aio.ChannelHandler;

public class AioServerHandler extends ChannelAdapter{

	public AioServerHandler(AsynchronousSocketChannel channel, Charset charset) {
		super(channel, charset);
	}

	@Override
	protected void channelRead(ChannelHandler channelHandler, Object msg) {
		System.out.println("read!!!!!");
		System.out.println(msg);
		channelHandler.writeAndFlush("msg success");
	}

	@Override
	protected void channelInactive(ChannelHandler ctx) {
		
	}

	@Override
	protected void channelActive(ChannelHandler ctx) {
		ctx.writeAndFlush("server: connect succeed");
	}

}
