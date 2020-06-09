package com.spordniar;

import java.util.HashMap;
import java.util.Map;

public class MainMap {

	public static void main(String[] args) {
		String c9 = "C9";
		String aW = "Aw";
		System.out.println(c9.hashCode());
		System.out.println(aW.hashCode());
		Map<String, String> maps = new HashMap<String, String>();
		maps.put(c9, "c9");
		maps.put(aW, "aw");
		System.out.println(maps.get(c9));
		System.out.println(maps.get(aW));
		
		Integer n1 = 123;
		Integer n2 = 123;
		Integer n3 = 145;
		Integer n4 = 145;
		System.out.println(n1 == n2);
		System.out.println(n3.equals(n4));
	}
	
}
