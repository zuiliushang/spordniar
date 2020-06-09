package com.spordniar.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class BioSimpleClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 8888);
		while (socket.isConnected()) {
			System.out.println("connect....");
			OutputStream out = socket.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charset.forName("gbk")));
			writer.write("haha");
			writer.newLine();
			writer.write("ooo");
			writer.newLine();
			writer.flush();
			out.flush();
			writer.close();
			out.close();
		}
	}
	
}
