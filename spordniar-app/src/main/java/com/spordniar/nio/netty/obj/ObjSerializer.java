package com.spordniar.nio.netty.obj;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;


public class ObjSerializer {

	private static Map<Class<?>, Schema<?>> cacheSchema = new ConcurrentHashMap<>();
	
	private static Objenesis objenesis = new ObjenesisStd();
	
	private ObjSerializer(){}
	
	public static <T> T deSerialize(byte[] bs, Class<T> clazz) {
		try {
            T message = objenesis.newInstance(clazz);
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(bs, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
	}
	
	public static<T> byte[] serialize(T obj) {
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) obj.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		 try {
	            Schema<T> schema = getSchema(clazz);
	            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
	        } catch (Exception e) {
	            throw new IllegalStateException(e.getMessage(), e);
	        } finally {
	            buffer.clear();
	        }
	}
	
	private static <T> Schema<T> getSchema(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		Schema<T> schema = (Schema<T>) cacheSchema.get(clazz);
		if (schema == null) {
			schema = RuntimeSchema.createFrom(clazz);
			cacheSchema.put(clazz, schema);
		}
		return schema;
	}
	
}
