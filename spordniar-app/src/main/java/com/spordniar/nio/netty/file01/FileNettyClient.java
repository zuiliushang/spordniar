package com.spordniar.nio.netty.file01;

import java.io.File;
import java.io.RandomAccessFile;

import com.google.gson.Gson;
import com.spordniar.nio.netty.file.Constants;
import com.spordniar.nio.netty.obj.ObjDecoder;
import com.spordniar.nio.netty.obj.ObjEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class FileNettyClient {

	public EventLoopGroup workGroup = new NioEventLoopGroup();
	public Channel channel;
	
	public ChannelFuture connect(String host, int port) {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ChannelFuture channelFuture = null;
		Gson gson = new Gson();
		try {
			Bootstrap b = new Bootstrap();
			b.channel(NioSocketChannel.class)
				.group(workerGroup)
				.option(ChannelOption.AUTO_READ, true)
				.handler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(new ObjDecoder(TransferProtocol.class));
						ch.pipeline().addLast(new ObjEncoder(TransferProtocol.class));
						ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
							@Override
							public void channelActive(ChannelHandlerContext ctx) throws Exception {
								System.out.println("client connect");
							}
							@Override
							public void channelInactive(ChannelHandlerContext ctx) throws Exception {
								System.out.println("client disconnect");
							}

							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								System.out.println(gson.toJson(msg));
								if (!(msg instanceof TransferProtocol)) {
									return;
								}
								TransferProtocol protocol = (TransferProtocol) msg;
								System.out.println("CLIENT " + gson.toJson(protocol));
								switch (protocol.getTransferType()) {
								case PROCESS:
									TransferBody body = protocol.getTransferBody();
									String fileUrl = body.getFileUrl();
									File file = new File(fileUrl);
									byte[] bs = new byte[1024 * 100];
									RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");//only read
									randomAccessFile.seek(body.getBeginPos());
									int readPos = randomAccessFile.read(bs);
									if (readPos < 0 ) {
										protocol.setTransferType(TransferType.COMPLETE);
										body.setData(null);
										body.setEndPos(body.getBeginPos());
										protocol.setTransferBody(body);
										ctx.writeAndFlush(protocol);
									}else {
										if (readPos < bs.length) {
											byte[] data = new byte[readPos];
											System.arraycopy(bs, 0, data, 0, readPos);
											body.setData(data);
											body.setEndPos(body.getBeginPos() + data.length);
											protocol.setTransferType(TransferType.COMPLETE);
											ctx.writeAndFlush(protocol);
										}else {
											body.setData(bs);
											body.setEndPos(body.getBeginPos() + bs.length);
											protocol.setTransferType(TransferType.PROCESS);
											ctx.writeAndFlush(protocol);
										}
									}
									randomAccessFile.close();
									break;
								case COMPLETE:
					                ctx.flush();
					                ctx.close();
					                System.exit(-1);
					                return;
								default:
									break;
								}
							}

							@Override
							public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
								cause.getMessage();
							}
						});
					}
				});
			channelFuture = b.connect(host, port).syncUninterruptibly();
			return channelFuture;
		}  finally {
			if (channelFuture != null && channelFuture.isSuccess()) {
				System.out.println("start.... done");
			} else {
				System.out.println("start.... error");
			}
		}
		
	}
	
	public static void main(String[] args) {
		FileNettyClient client = new FileNettyClient();
		TransferProtocol protocol = new TransferProtocol();
		protocol.setTransferType(TransferType.ACCPECT);
		TransferBody body = new TransferBody();
		File file = new File("G:\\1\\curl-7.56.0.zip");
		body.setFileUrl(file.getAbsolutePath());
		body.setFileName(file.getName());
		protocol.setTransferBody(body);
		ChannelFuture f = client.connect("localhost", 8888);
		f.channel().writeAndFlush(protocol);
	}
	
}
