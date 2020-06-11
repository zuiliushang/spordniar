package com.spordniar.nio.netty.file;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import io.netty.channel.socket.nio.NioSocketChannel;

public class FileNettyClient {

	public EventLoopGroup workGroup = new NioEventLoopGroup();
	public Channel channel;
	
	public ChannelFuture connect(String host, int port) {
		Bootstrap b = new Bootstrap();
		ChannelFuture channelFuture = null;
		try {
			b.channel(NioSocketChannel.class)
				.group(workGroup)
				.option(ChannelOption.AUTO_READ, true)
				.handler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(new ObjDecoder(FileTransferProtocol.class));
						ch.pipeline().addLast(new ObjEncoder(FileTransferProtocol.class));
						ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
							@Override
							public void channelActive(ChannelHandlerContext ctx) throws Exception {
								System.out.println("connect ");
							}
							@Override
							public void channelInactive(ChannelHandlerContext ctx) throws Exception {
								System.out.println("disconnect");
							}
							@Override
							public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
								 //数据格式验证
						        if (!(msg instanceof FileTransferProtocol)) return;

						        FileTransferProtocol fileTransferProtocol = (FileTransferProtocol) msg;
						        //0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
						        switch (fileTransferProtocol.getTransferType()) {
						            case 1:
						                FileBurstInstruct fileBurstInstruct = (FileBurstInstruct) fileTransferProtocol.getTransferObj();
						                //Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
						                if (Constants.FileStatus.COMPLETE == fileBurstInstruct.getStatus()) {
						                    ctx.flush();
						                    ctx.close();
						                    System.exit(-1);
						                    return;
						                }
						                FileBurstData fileBurstData = FileUtils.readFile(fileBurstInstruct.getClientFileUrl(), fileBurstInstruct.getReadPosition());
						                ctx.writeAndFlush(buildTransferData(fileBurstData));
						                System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " bugstack虫洞栈客户端传输文件信息。 FILE：" + fileBurstData.getFileName() + " SIZE(byte)：" + (fileBurstData.getEndPos() - fileBurstData.getBeginPos()));
						                break;
						            default:
						                break;
						        }

						        /**模拟传输过程中断，场景测试可以注释掉
						         *
						        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " bugstack虫洞栈客户端传输文件信息[主动断开链接，模拟断点续传]");
						        ctx.flush();
						        ctx.close();
						        System.exit(-1);*/
							}
							@Override
							public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
								cause.printStackTrace();
							}
							 public FileTransferProtocol buildTransferData(FileBurstData fileBurstData) {
							        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
							        fileTransferProtocol.setTransferType(Constants.TransferType.DATA); //0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
							        fileTransferProtocol.setTransferObj(fileBurstData);
							        return fileTransferProtocol;
							    }
						});
					}
				});
			channelFuture = b.connect(host, port).syncUninterruptibly();
			this.channel = channelFuture.channel();
		} finally {
			if (channelFuture != null && channelFuture.isSuccess()) {
				System.out.println("start.... done");
			} else {
				System.out.println("start.... error");
			}
		}
		return channelFuture;
	}
	
	public void destroy() {
		if (channel == null) {
			return ;
		}
		channel.close();
		workGroup.shutdownGracefully();
	}
	public EventLoopGroup getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(EventLoopGroup workGroup) {
		this.workGroup = workGroup;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public static void main(String[] args) {
		FileNettyClient client = new FileNettyClient();
		ChannelFuture channelFuture = client.connect("localhost", 8888);
		File file = new File("G:\\curl-7.56.0.zip");
	    FileTransferProtocol fileTransferProtocol = FileNettyClient.buildRequestTransferFile(file.getAbsolutePath(), file.getName(), file.length());
	    //发送信息；请求传输文件
	    channelFuture.channel().writeAndFlush(fileTransferProtocol);

	}
	public static FileTransferProtocol buildRequestTransferFile(String fileUrl, String fileName, Long fileSize) {

        FileDescInfo fileDescInfo = new FileDescInfo();
        fileDescInfo.setFileUrl(fileUrl);
        fileDescInfo.setFileName(fileName);
        fileDescInfo.setFileSize(fileSize);

        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransferType(0);//0请求传输文件、1文件传输指令、2文件传输数据
        fileTransferProtocol.setTransferObj(fileDescInfo);

        return fileTransferProtocol;

    }
}
