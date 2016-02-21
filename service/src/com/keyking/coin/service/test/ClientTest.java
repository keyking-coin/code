package com.keyking.coin.service.test;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class ClientTest {
	
	public static void main(String[] args) {
		NioSocketConnector connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(30000L);
		connector.setHandler(new ClientHandler());
		InetSocketAddress address = new InetSocketAddress("sh-yxwlkj.com",32105);
		ConnectFuture future = connector.connect(address);
		future.awaitUninterruptibly();
		if (future.isConnected()) {
			System.out.println(1);
		}
	}
}
