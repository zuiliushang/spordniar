package com.spordniar.nio.netty;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
public class MainNetty01 {
	

	public static void main(String[] args) throws InterruptedException {
		Charset charset = Charset.forName("gbk");
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(parentGroup, childGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128).childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							//ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
							ch.pipeline().addLast(new StringEncoder(charset));
							ch.pipeline().addLast(new StringDecoder(charset));
							ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
								@Override
								public void channelActive(ChannelHandlerContext ctx) throws Exception {
									 SocketChannel channel = (SocketChannel) ctx.channel();
									System.out.println("hel");
									System.out.println("hel init");
									System.out.println("hel " + channel.localAddress().getHostString());
									System.out.println("hel " + channel.localAddress().getPort());
									String str = "hi spordniar";
									byte[] bs = str.getBytes(charset);
									ByteBuf byteBuf = Unpooled.buffer(bs.length);
									byteBuf.writeBytes(bs);
									ctx.writeAndFlush(byteBuf);
								}
								@Override
								public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
									System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息：" + msg);
									ByteBuf byteBuf = Unpooled.wrappedBuffer("处理完毕".getBytes(charset));
									ctx.writeAndFlush(byteBuf);
								}
							});
						}
					});
			ChannelFuture f = sb.bind(8888).sync();
			System.out.println("first netty server starting.........");
			f.channel().closeFuture().sync();
		} finally {
			childGroup.shutdownGracefully();
			parentGroup.shutdownGracefully();
		}
	}
}
