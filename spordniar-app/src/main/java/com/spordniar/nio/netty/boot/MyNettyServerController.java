package com.spordniar.nio.netty.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyNettyServerController {

	@Autowired
	NettyServer nettyServer;
	
	@GetMapping("/netty/address")
	public String nettyAddress() {
		return nettyServer.getChannel().localAddress().toString();
	}
	
	@GetMapping("/netty/open")
	public String open() {
		return nettyServer.getChannel().isOpen() + "";
	}
	
}
