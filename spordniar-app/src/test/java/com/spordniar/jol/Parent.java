package com.spordniar.jol;

import sun.misc.Unsafe;
import java.lang.reflect.Field;


public class Parent {
	
	public static Integer i = 0;
	
	static {
		System.out.println("parent static");
	}
	
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafeInstance.get(Unsafe.class);
        
        //unsafe.compareAndSwapInt(arg0, arg1, arg2, arg3);
	}
}
