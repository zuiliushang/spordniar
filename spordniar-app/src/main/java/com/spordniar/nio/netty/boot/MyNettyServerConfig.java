package com.spordniar.nio.netty.boot;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.netty.channel.ChannelFuture;

@Configuration
public class MyNettyServerConfig {

	@Value("${netty.host}")
	String host;
	
	@Value("${netty.port}")
	int port;
	
	@Bean
	public NettyServer nettyServer() {
		NettyServer nettyServer = new NettyServer();
		nettyServer.bing(new InetSocketAddress(host, port));
		Runtime.getRuntime().addShutdownHook(new Thread(()-> nettyServer.destory()));
		//channelFuture.channel().closeFuture().syncUninterruptibly();
		return nettyServer;
	}
	
}
