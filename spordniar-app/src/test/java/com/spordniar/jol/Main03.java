package com.spordniar.jol;

public class Main03 {
	int a,b=0;
	int x,y=1;
	public static void main(String[] args) {
		Main03 main03 = new Main03();
		for (;;) {
			Thread t1 = new Thread(()->{
				main03.a = 1;
				main03.x = main03.b;
			}) ;
			Thread t2 = new Thread(()->{
				main03.b = 1;
				main03.y = main03.a;
			});
			t1.start();
			t2.start();
			if (main03.x == 0 && main03.y == 0) {
				break;
			}
		}
	}
	
}
