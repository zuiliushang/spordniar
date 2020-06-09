package com.spordniar.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ReactorDemo {
    private Selector selector;
    public ReactorDemo() throws IOException {
        initServer();
    }
    private void initServer() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(8888));
        selector = Selector.open();
        SelectionKey selectionKey = serverChannel.
            register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
        System.out.println("server 启动");
    }
    public void start() throws IOException {
        while (selector.select() > 0) {
            Set<SelectionKey> set = selector.selectedKeys();
            Iterator<SelectionKey> iterator = set.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                dispater(selectionKey);
                iterator.remove();
            }
        }
    }
    public void dispater(SelectionKey selectionKey) {
        Hander hander = (Hander) selectionKey.attachment();
        if (hander != null) {
            hander.process(selectionKey);
        }
    }
    
    interface Hander {
        void process(SelectionKey selectionKey);
    }
     class Acceptor implements Hander {
        @Override
        public void process(SelectionKey selectionKey) {
            try {
                ServerSocketChannel serverSocketChannel = 
                    (ServerSocketChannel) selectionKey.channel();
                SocketChannel channel = serverSocketChannel.accept();
                System.out.println("建立链接：" + channel.getRemoteAddress());
                channel.configureBlocking(false);
                selectionKey.attach(new ProcessHandler());
                channel.register(selector, SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
     class ProcessHandler implements Hander {
        @Override
        public void process(SelectionKey selectionKey) {
        	SocketChannel channel = (SocketChannel) selectionKey.channel();
        	try {
				channel.write(ByteBuffer.wrap("你好".getBytes()));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    public static void main(String[] args) throws IOException {
        new ReactorDemo().start();
    }
}
