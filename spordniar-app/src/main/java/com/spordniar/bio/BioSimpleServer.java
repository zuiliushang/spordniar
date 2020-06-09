package com.spordniar.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class BioSimpleServer {

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(8888);
		while (true) {
			Socket sk = serverSocket.accept();
			if (sk.isConnected()) {
				System.out.println("connect....");
				Thread t1 = new Thread(() -> {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(sk.getInputStream(),Charset.forName("gbk")));
						String res = null;
						while ((res = reader.readLine()) != null) {
							System.out.println("receive readline " + res);
							OutputStream out = sk.getOutputStream();
							out.write("完毕。。。。。。。".getBytes(Charset.forName("gbk")));
							out.flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				t1.start();
			}
		}
	}
	
}
