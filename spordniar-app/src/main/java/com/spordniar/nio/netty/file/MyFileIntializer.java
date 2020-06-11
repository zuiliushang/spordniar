package com.spordniar.nio.netty.file;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dyuproject.protostuff.runtime.MappedSchema.Field;
import com.google.gson.Gson;
import com.spordniar.nio.netty.obj.ObjDecoder;
import com.spordniar.nio.netty.obj.ObjEncoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class MyFileIntializer extends ChannelInitializer<SocketChannel>{
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new ObjEncoder(FileTransferProtocol.class));
		ch.pipeline().addLast(new ObjDecoder(FileTransferProtocol.class));
		ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				SocketChannel channel = (SocketChannel) ctx.channel();
				System.out.println("receive channel Id " + channel.id());
				System.out.println("recerve channel addr " + channel.remoteAddress().toString());
				System.out.println("connect success");
			}
			@Override
			public void channelInactive(ChannelHandlerContext ctx) throws Exception {
				System.out.println("disconnect " + ctx.channel().remoteAddress().toString() );
			}
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				Gson gson = new Gson();
				//数据格式
				if (!(msg instanceof FileTransferProtocol)) {
					return;
				}
				// 传输类型 
				FileTransferProtocol fileTransferProtocol = (FileTransferProtocol) msg;
				switch (fileTransferProtocol.getTransferType()) {
				case 0: // 0 请求 1传输 2数据
					// 传输文件 请求
					FileDescInfo fileDescInfo = (FileDescInfo) fileTransferProtocol.getTransferObj();
					// 获取之前传的状态
					FileBurstInstruct fileBurstInstructold = FileCacheUtils.burstInstructCacheMap.get(fileDescInfo.getFileName());
					if (fileBurstInstructold != null) { // 不为空
						// 完成删除
						if (fileBurstInstructold.getStatus() == Constants.FileStatus.COMPLETE) {
							FileCacheUtils.burstInstructCacheMap.remove(fileDescInfo.getFileName());
						}
						System.out.println(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "断点完成" + gson.toJson(fileBurstInstructold));
						// 构建协议
						FileTransferProtocol protocol = buildTransferProtocol(fileBurstInstructold);
						ctx.writeAndFlush(protocol);
						return;
					}
					//构建
					FileTransferProtocol protocol = buildTransferInstruct(Constants.FileStatus.BEGIN, fileDescInfo.getFileUrl(), 0);
					ctx.writeAndFlush(protocol);
					System.out.println(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "接收客户端文件请求" + gson.toJson(protocol));
					break;
				case 2:
					FileBurstData fileBurstData = (FileBurstData) fileTransferProtocol.getTransferObj();
					FileBurstInstruct fileBurstInstruct = FileUtils.writeFile("F://", fileBurstData);
					FileCacheUtils.burstInstructCacheMap.put(fileBurstData.getFileName(), fileBurstInstruct);
					ctx.writeAndFlush(buildTransferProtocol(fileBurstInstruct));
					System.out.println(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "数据" + gson.toJson(fileBurstData));
					// 完成删除
					if (fileBurstInstruct.getStatus() == Constants.FileStatus.COMPLETE) {
						FileCacheUtils.burstInstructCacheMap.remove(fileBurstData.getFileName());
					}
				default:
					break;
				}
			}
			private FileTransferProtocol buildTransferProtocol(FileBurstInstruct fileBurstInstructold) {
				FileTransferProtocol protocol = new FileTransferProtocol();
				protocol.setTransferObj(fileBurstInstructold);
				protocol.setTransferType(Constants.TransferType.INSTRUCT);//指令 可以传
				return protocol;
			}
			// 构建请求
			private FileTransferProtocol buildTransferInstruct(int status, String clientFileUrl, int position) {
				FileBurstInstruct fileBurstInstruct = new FileBurstInstruct();
				fileBurstInstruct.setClientFileUrl(clientFileUrl);
				fileBurstInstruct.setStatus(status);
				fileBurstInstruct.setReadPosition(position);
				FileTransferProtocol protocol = new FileTransferProtocol();
				protocol.setTransferObj(fileBurstInstruct);
				protocol.setTransferType(Constants.TransferType.INSTRUCT);
				return protocol;
			}
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				super.exceptionCaught(ctx, cause);
			}
		});
	}

}
