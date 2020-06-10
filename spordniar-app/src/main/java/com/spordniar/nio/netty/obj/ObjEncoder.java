package com.spordniar.nio.netty.obj;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ObjEncoder extends MessageToByteEncoder<Object>{

	private Class<?> genericClass;
	
	public ObjEncoder(Class<?> genericClass) {
		super();
		this.genericClass = genericClass;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
		if (genericClass.isInstance(in)) {
			byte[] bs = ObjSerializer.serialize(in);
			out.writeInt(bs.length);
			out.writeBytes(bs);
		}
	}

}
