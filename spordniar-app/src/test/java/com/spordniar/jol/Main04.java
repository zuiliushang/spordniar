package com.spordniar.jol;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class Main04 {
	
	static ReentrantLock lock = new ReentrantLock();
	
	static int i = 0;
	
	public static LinkedList<Integer> list = new LinkedList<Integer>();
	
	
	public static void main(String[] args) throws InterruptedException {
		Runnable r1 = () -> {
			lock.lock();
				list.add(i++);
			lock.unlock();
		};
		Runnable r2 = () -> {
			lock.lock();
				System.out.println(list.getLast());
			lock.unlock();
		};
		for(;;) {
			Thread t1 = new Thread(r1);
			Thread t2 = new Thread(r2);
			Thread t3 = new Thread(r1);
			t1.start();t2.start();t3.start();
		}
		//new runnable[ready < yield >running] block[ synchronous ] wait[ wait notify ] timeWaiting [ sleep await join] terminated
		
	}
	
}
