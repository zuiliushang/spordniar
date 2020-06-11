package com.spordniar.nio.netty.file01;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.spordniar.nio.netty.obj.ObjDecoder;
import com.spordniar.nio.netty.obj.ObjEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class FileNettyServer {
	// cache 用于保存断线重连？
	private static Map<String, TransferBody> cacheMap = new ConcurrentHashMap<String, TransferBody>();
	
	public static void main(String[] args) {
		EventLoopGroup parentGroup = new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		Gson gson = new Gson();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.channel(NioServerSocketChannel.class)
				.group(parentGroup, childGroup)
				.option(ChannelOption.SO_BACKLOG,  128)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new ObjEncoder(TransferProtocol.class));
						ch.pipeline().addLast(new ObjDecoder(TransferProtocol.class));
						ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
							@Override
							public void channelActive(ChannelHandlerContext ctx) throws Exception {
								System.out.println("connect success " + ctx.channel().remoteAddress().toString());
							}
							@Override
							public void channelInactive(ChannelHandlerContext ctx) throws Exception {
								System.out.println("disconnect success " + ctx.channel().remoteAddress().toString());
							}
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								if (!(msg instanceof TransferProtocol)) {
									return;
								}
								TransferProtocol protocol = (TransferProtocol) msg;
								System.out.println("RECERVE: " + gson.toJson(msg));
								switch (protocol.getTransferType()) {
								case ACCPECT:
									// 获取到请求创建文件
									// 创建一个请求 
									protocol.setTransferType(TransferType.PROCESS);
									TransferBody body = protocol.getTransferBody();
									body.setBeginPos(0);
									protocol.setTransferBody(body);
									ctx.writeAndFlush(protocol);
									System.out.println("ACCEPT " + gson.toJson(protocol));
									break;
								case PROCESS:
									// 获取处理中
									body =protocol.getTransferBody();
									String filename = body.getFileName();
									byte[] data = body.getData();
									File file = new File("G://" + "/" + filename);
									RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
									randomAccessFile.seek(body.getBeginPos());
									randomAccessFile.write(data);
									randomAccessFile.close();
									body.setBeginPos(body.getEndPos() + 1);
									body.setData(null);//clear
									protocol.setTransferBody(body);
									ctx.writeAndFlush(protocol);
									System.out.println("PROCESS " + gson.toJson(protocol));
									break;
								case COMPLETE:
									body = protocol.getTransferBody();
									if (body.getData() != null && body.getData().length > 0) {
										filename = body.getFileName();
										data = body.getData();
										file = new File("G://" + "/" + filename);
										randomAccessFile = new RandomAccessFile(file, "rw");
										randomAccessFile.seek(body.getBeginPos());
										randomAccessFile.write(data);
										randomAccessFile.close();
										body.setData(null);
										protocol.setTransferBody(body);
										protocol.setTransferType(TransferType.COMPLETE);
										ctx.writeAndFlush(protocol);
										System.out.println("COMPLETE " + gson.toJson(protocol));
										break;
									}
								default:
									throw new RuntimeException("err errr default err");
								}
							}
							@Override
							public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
								super.exceptionCaught(ctx, cause);
							}
						});
					}
				});
			ChannelFuture f = b.bind(8888).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			childGroup.shutdownGracefully();
			parentGroup.shutdownGracefully();
		}
	}
	
}
