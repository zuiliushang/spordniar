package com.spordniar.jol;

import java.lang.ref.SoftReference;
import java.util.List;

public class Main02 extends Thread{

	volatile boolean key = true;
	boolean key1 = true;
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName());
		System.out.println(key);
		while (key1) {
		}
		System.out.println(key);
	}
	
	 static class N{
		int a,b = 0;
		int x,y = 1;

		@Override
		public String toString() {
			return "N [a=" + a + ", b=" + b + ", x=" + x + ", y=" + y + "]";
		}
	}
	 // 分配内存 生成对象默认值(不为null)->构造函数 指针引用
	 // 不为空拿来用 0
	 // 
	 
	public static void main(String[] args) throws InterruptedException {
//		System.out.println(Thread.currentThread().getName());
//		Main02 main02 = new Main02();
//		main02.start();
//		System.out.println(1);
//		Thread.sleep(1000);
//		main02.key1 = false;
//		main02.key = false;
		N n = new N();
		Thread t1 = new Thread(()->{
			n.a=1;
			n.x=n.b;
		});
		Thread t2 = new Thread(()->{
			n.b=1;
			n.y=n.a;
		});
		int i=0;
		while (true) {
			t1.start();
			t2.start();
			if (n.y == 0 && n.x ==0) {
				System.out.println(++i +"  "+ n.toString());
				break;
			}
		}
		
	}
	// a=1 x=b
	// b=1 y=a
	
	
}
