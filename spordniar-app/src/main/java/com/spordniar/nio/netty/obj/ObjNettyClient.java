package com.spordniar.nio.netty.obj;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ObjNettyClient {

	public static void main(String[] args) {
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.AUTO_READ, true)
				.handler(new MyChannelInitializer());
			ChannelFuture f = b.connect("localhost", 8888).sync();
			f.channel().writeAndFlush(new GreetingEntity("你好 Spordniar"));
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workGroup.shutdownGracefully();
		}
	}
	
}
