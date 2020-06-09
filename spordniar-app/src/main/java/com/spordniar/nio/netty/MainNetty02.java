package com.spordniar.nio.netty;

import java.nio.charset.Charset;
import java.util.Arrays;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class MainNetty02 {
	public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	public static void main(String[] args) {
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		try {
			Charset charset = Charset.forName("gbk");
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(parentGroup, childGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 128)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
							@Override
							public void channelActive(ChannelHandlerContext ctx) throws Exception {
								MainNetty02.channelGroup.add(ctx.channel());
								SocketChannel socketChannel = (SocketChannel) ctx.channel();
								System.out.println(socketChannel.localAddress().getAddress().toString());
							}
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								ByteBuf buf = (ByteBuf)msg;
								System.out.println("receive: " + buf.toString(charset));
								ctx.writeAndFlush(Unpooled.wrappedBuffer("你好啊".getBytes(charset)));
								Thread.sleep(100);
								MainNetty02.channelGroup.writeAndFlush(Unpooled.wrappedBuffer("群发你好啊".getBytes(charset)));
							}
						});
					}
				});
			ChannelFuture f = sb.bind(8888).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			childGroup.shutdownGracefully();
			parentGroup.shutdownGracefully();
		}
	}
	
}
