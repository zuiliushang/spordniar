package com.spordniar;

public class Ddd {

	public static void main(String[] args) {
		int i = 0 ;
		
		while (i++ < 10) {
			ha(i);
		}
	}
	
	private static void ha(int i ) {
		if (i == 5) {
			return ;
		}
		System.out.println(i);
	}
}
