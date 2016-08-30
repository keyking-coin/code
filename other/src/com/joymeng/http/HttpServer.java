package com.joymeng.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.joymeng.http.codec.HttpRequestDecoder;
import com.joymeng.http.codec.HttpResponseEncoder;
import com.joymeng.http.codec.HttpServerProtocolCodecFactory;
import com.joymeng.http.handler.HttpServerHandler;
import com.joymeng.log.GameLog;

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
				GameLog.info("HttpServer is already running.");
				return;
			}
			setEncoding("UTF-8");
			acceptor = new NioSocketAcceptor();;
			acceptor.getFilterChain().addLast("protocolFilter",new ProtocolCodecFilter(new HttpServerProtocolCodecFactory()));
			acceptor.setHandler(new HttpServerHandler());
			acceptor.bind(new InetSocketAddress("0.0.0.0",port));
			SocketSessionConfig config = acceptor.getSessionConfig();  
            config.setReadBufferSize(1024*1024);//1M
            config.setIdleTime(IdleStatus.BOTH_IDLE,30*60*1000);//限制时间30分钟
			isRunning = true;
			GameLog.info("HttpServer now listening on port " + port);
		}
	}

	/**
	 * 停止监听HTTP服务
	 */
	public void stop() {
		synchronized (this) {
			if (!isRunning) {
				GameLog.info("HttpServer is already stoped.");
				return;
			}
			isRunning = false;
			try {
				acceptor.unbind();
				acceptor.dispose();
				GameLog.info("HttpServer is stoped.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
