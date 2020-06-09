package com.spordniar.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ExecutorMain implements Watcher{
	
	
	
	public static void main(String[] args) throws IOException {
		//ZooKeeper server = new ZooKeeper("192.168.24.3:2181,192.168.24.3:2182,192.168.24.3:2183", 5000, new ExecutorMain());
		//System.out.println(server.getState());
		System.out.println(Double.MIN_VALUE);
		System.out.println(Double.MAX_VALUE);
		System.out.println(Integer.MIN_VALUE);
		System.out.println(Integer.MAX_VALUE);
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println(event.getPath());
		System.out.println(event.getState());
		System.out.println(event.getType());
	}
	
}
