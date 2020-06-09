package com.spordniar.nio.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MainNettyClinet01 {

	public static void main(String[] args) {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap bs = new Bootstrap();
			bs.group(workerGroup);
			bs.channel(NioSocketChannel.class);
			bs.option(ChannelOption.AUTO_READ, true);
			bs.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
						@Override
						public void channelActive(ChannelHandlerContext ctx) throws Exception {
							System.out.println("connect...");
						}
					});
				}
			});
			ChannelFuture f = bs.connect("localhost", 8888).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			workerGroup.shutdownGracefully();
		}
	}
	
}
