package com.spordniar.nio.netty.obj;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ObjDecoder extends ByteToMessageDecoder{

	private Class<?> genericClass;
	
	public ObjDecoder(Class<?> genericClass) {
		super();
		this.genericClass = genericClass;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return ;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			return;
		}
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		out.add(ObjSerializer.deSerialize(data, genericClass));
//		 if (in.readableBytes() < 4) {
//	            return;
//	        }
//	        in.markReaderIndex();
//	        int dataLength = in.readInt();
//	        if (in.readableBytes() < dataLength) {
//	            in.resetReaderIndex();
//	            return;
//	        }
//	        byte[] data = new byte[dataLength];
//	        in.readBytes(data);
//	        out.add(SerializationUtil.deserialize(data, genericClass));
		
	}

}