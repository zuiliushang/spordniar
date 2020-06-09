package com.spordniar.nio.netty;

import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;

public class SimpleNettyHttpServer {

	public static void main(String[] args) {
		NioEventLoopGroup parentGroup = new NioEventLoopGroup();
		NioEventLoopGroup childGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(parentGroup, childGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 128)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new HttpResponseEncoder());
						ch.pipeline().addLast(new HttpRequestDecoder());
						ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								if (msg instanceof HttpRequest) {
						            DefaultHttpRequest request = (DefaultHttpRequest) msg;
						            System.out.println("URI:" + request.getUri());
						            System.err.println(msg);
						        }

						        if (msg instanceof HttpContent) {
						            LastHttpContent httpContent = (LastHttpContent) msg;
						            ByteBuf byteData = httpContent.content();
						            if (!(byteData instanceof EmptyByteBuf)) {
						                //接收msg消息
						                byte[] msgByte = new byte[byteData.readableBytes()];
						                byteData.readBytes(msgByte);
						                System.out.println(new String(msgByte, Charset.forName("UTF-8")));
						            }
						        }

						        String sendMsg = "<h1>hello</h1>";

						        FullHttpResponse response = new DefaultFullHttpResponse(
						                HttpVersion.HTTP_1_1,
						                HttpResponseStatus.OK,
						                Unpooled.wrappedBuffer(sendMsg.getBytes(Charset.forName("UTF-8"))));
						        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
						        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
						        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
						        ctx.write(response);
						        ctx.flush();
							}
							
						});
					}
				});
			ChannelFuture f = sb.bind(8888).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			childGroup.shutdownGracefully();
			parentGroup.shutdownGracefully();
		}
	}
	
}
