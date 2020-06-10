package com.spordniar.nio.netty.boot;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);
	
	private final EventLoopGroup parentGroup = new NioEventLoopGroup();
	private final EventLoopGroup childGroup = new NioEventLoopGroup();
	private Channel channel;
	
	public ChannelFuture bing(InetSocketAddress address) {
		ChannelFuture channelFuture = null;
		Charset charset = Charset.forName("gbk");
		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(parentGroup, childGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 128)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
						ch.pipeline().addLast(new StringDecoder(charset));
						ch.pipeline().addLast(new StringEncoder(charset));
						ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
							@Override
							public void channelActive(ChannelHandlerContext ctx) throws Exception {
								SocketChannel socketChannel = (SocketChannel) ctx.channel();
								LOGGER.info("connect success");
								LOGGER.info("connect info " + socketChannel.remoteAddress().getHostString());
								LOGGER.info("connect info " + socketChannel.remoteAddress().getPort());
								ctx.writeAndFlush(Unpooled.wrappedBuffer("你好, Spordniar Spring Boot Netty Server。连接成功".getBytes(charset)));
							}
							@Override
							public void channelInactive(ChannelHandlerContext ctx) throws Exception {
								LOGGER.info("客户端断开链接：" + ctx.channel().localAddress().toString());
							}
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ":" + msg);
								ctx.writeAndFlush(Unpooled.wrappedBuffer("服务器收到".getBytes(charset)));
							}
							@Override
							public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
								ctx.close();
								LOGGER.info("异常: " + cause.getMessage());
							}
						});
					}
				});
			channelFuture = sb.bind(address).syncUninterruptibly();
			channel = channelFuture.channel();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (channelFuture != null && channelFuture.isSuccess()) {
				LOGGER.info("netty start " + address.toString());
			} else {
				LOGGER.error("netty error");
			}
		}
		return channelFuture;
	}
	
	public void destory() {
		if (channel == null) {
			return ;
		}
		channel.close();
		childGroup.shutdownGracefully();
		parentGroup.shutdownGracefully();
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
}
