package com.spordniar.nio.netty.obj;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new ObjDecoder(GreetingEntity.class));
		ch.pipeline().addLast(new ObjEncoder(GreetingEntity.class));
		ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				System.out.println("connect :" + ctx.channel().remoteAddress().toString());
			}
			@Override
			public void channelInactive(ChannelHandlerContext ctx) throws Exception {
				System.out.println("disconnect : " + ctx.channel().remoteAddress().toString());
			}
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				System.out.println("recerve" + msg.toString());
			}
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				System.out.println(cause.getMessage());
			}
			
		});
	}
}