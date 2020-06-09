package com.spordniar.aio;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.spordniar.aio.server.AioServer;

public abstract class ChannelInitializer implements CompletionHandler<AsynchronousSocketChannel, AioServer>{

	@Override
	public void completed(AsynchronousSocketChannel channel, AioServer attachment) {
		try {
			initChannel(channel);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			attachment.serverSocketChannel().accept(attachment, this);
		}
	}

	protected abstract void initChannel(AsynchronousSocketChannel channel);

	@Override
	public void failed(Throwable exc, AioServer attachment) {
		exc.getStackTrace();
	}
	
}
