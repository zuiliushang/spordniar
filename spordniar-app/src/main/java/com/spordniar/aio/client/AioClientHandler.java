package com.spordniar.aio.client;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.Date;

import com.spordniar.aio.ChannelAdapter;
import com.spordniar.aio.ChannelHandler;

public class AioClientHandler extends ChannelAdapter{

	public AioClientHandler(AsynchronousSocketChannel channel, Charset charset) {
		super(channel, charset);
	}

	@Override
	protected void channelRead(ChannelHandler channelHandler, Object msg) {
		System.out.println("client receive: " + new Date() + "  " + msg);
		channelHandler.writeAndFlush("client process success!");
		channelHandler.writeAndFlush("client process success!");
		channelHandler.writeAndFlush("client process success!");
		channelHandler.writeAndFlush("client process success!");
	}

	@Override
	protected void channelInactive(ChannelHandler ctx) {
		
	}

	@Override
	protected void channelActive(ChannelHandler ctx) {
		try {
			System.out.println("client connect: " + ctx.getChannel().getRemoteAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
