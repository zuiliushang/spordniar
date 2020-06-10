package com.spordniar.nio.netty.file;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class MyFileIntializer extends ChannelInitializer<SocketChannel>{
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("http-codec", new HttpServerCodec());
		ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
		ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
		ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				System.out.println("connect " + ctx.channel().remoteAddress().toString());
			}
			@Override
			public void channelInactive(ChannelHandlerContext ctx) throws Exception {
				System.out.println("disconnect " + ctx.channel().remoteAddress().toString());
			}
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				
			}
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				System.out.println("exception " + cause.getMessage());
			}
		});
	}

}
