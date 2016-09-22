package com.keyking.coin.service.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.keyking.coin.service.Service;
import com.keyking.coin.service.http.codec.HttpRequestDecoder;
import com.keyking.coin.service.http.codec.HttpResponseEncoder;
import com.keyking.coin.service.http.codec.HttpServerProtocolCodecFactory;
import com.keyking.coin.service.http.handler.HttpServerHandler;
import com.keyking.coin.util.ServerLog;

public class HttpServer {
	
	private static HttpServer instance = new HttpServer();
	
	public static HttpServer getInstance(){
		return instance;
	}
	
	private SocketAcceptor acceptor;
	
	private boolean isRunning;

	private String encoding;

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
		HttpRequestDecoder.defaultEncoding = encoding;
		HttpResponseEncoder.defaultEncoding = encoding;
	}

	/**
	 * 启动HTTP服务端箭筒HTTP请求
	 * 
	 * @param port要监听的端口号
	 * @throws IOException
	 */
	public void run(int port) throws IOException {
		synchronized (this) {
			if (isRunning) {
				ServerLog.info("HttpServer is already running.");
				return;
			}
			setEncoding("UTF-8");
			acceptor = new NioSocketAcceptor();;
			acceptor.getFilterChain().addLast("protocolFilter",new ProtocolCodecFilter(new HttpServerProtocolCodecFactory()));
			acceptor.getFilterChain().addLast("threadPool",new ExecutorFilter(Service.configureExecutorService()));
			acceptor.setHandler(new HttpServerHandler());
			acceptor.bind(new InetSocketAddress("0.0.0.0",port));
			isRunning = true;
			ServerLog.info("HttpServer now listening on port " + port);
		}
	}

	/**
	 * 停止监听HTTP服务
	 */
	public void stop() {
		synchronized (this) {
			if (!isRunning) {
				ServerLog.info("HttpServer is already stoped.");
				return;
			}
			isRunning = false;
			try {
				acceptor.unbind();
				acceptor.dispose();
				ServerLog.info("HttpServer is stoped.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
