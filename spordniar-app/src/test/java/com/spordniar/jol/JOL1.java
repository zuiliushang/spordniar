package com.spordniar.jol;

import java.lang.ref.PhantomReference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

public class JOL1 {

	public static void main(String[] args) {
		SoftReference<long[]> s = new SoftReference<long[]>(new long[1024]);
		Object o = new Object();
		System.out.println(ClassLayout.parseInstance(o).toPrintable());
		int i = 1;
		System.out.println(ClassLayout.parseInstance(i).toPrintable());
		System.out.println(GraphLayout.parseInstance(i).totalSize());
		Map<String, String> map = new HashMap<String, String>();
		map.put("haloe", "haloe");
		map.put("haloe", "haloe");
		map.put("haloe", "haloe");
		map.put("haloe", "haloe");
		System.out.println(ClassLayout.parseInstance(map).toPrintable());
		User user = new User();
		System.out.println(ClassLayout.parseInstance(user).toPrintable());
		System.out.println(ClassLayout.parseInstance(new Apple()).toPrintable());
		System.out.println(ClassLayout.parseInstance(s).toPrintable());
		WeakReference<Integer> weakReference = new WeakReference<Integer>(1);
//		for (;;) {
//			Integer a = weakReference.get();
//			System.out.println(a);
//		}
		PhantomReference<Integer> pr = new PhantomReference<Integer>(2, null);
		//堆外内存 0拷贝 direct
		//垃圾线程 -> 堆外内存的 direcbuffer对象没有了 C++删除对应内存
		//回收器回收时 队列ReferenceQueue有东西
		for (;;) {
			System.out.println(pr.get());
		}
	}
	
	 static class User{
		private Integer age;
		private Short e;
		public synchronized Integer getAge() {
			synchronized (age) {
				age++;
			}
			return age;
		}
	}
	
	 static class Apple{
		 long long1 = 5555555555555l;
		 long long2;
	 }
	 
}
