package com.keyking.admin.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.keyking.admin.net.codec.MessageCodecFactory;
import com.keyking.admin.net.handler.NetConnectHandler;
import com.keyking.admin.net.request.Request;
import com.keyking.admin.net.resp.ResultCallBack;

public class NetUtil {
	static final String CONNECT_URL = "keyking-ty.xicp.net";
	static final int CONNECT_PORT = 12213;
	//static final String CONNECT_URL = "139.196.30.53";
	//static final int CONNECT_PORT = 32105;
	private static NetUtil instance = null;
	NioSocketConnector connector;
	IoSession writer = null;
	Map<String,ResultCallBack> callBacks = new HashMap<String,ResultCallBack>();
	private static final Logger logger = LoggerFactory.getLogger(NetUtil.class); 
	
	public static NetUtil getInstance(){
		if (instance == null){
			instance = new NetUtil();
			instance.initConnector();
		}
		return instance;
	}
	
	public void initConnector(){
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(30000L);
		connector.setHandler(new NetConnectHandler());
		//connector.getSessionConfig().setReadBufferSize(1024*1024);
		connector.getFilterChain().addLast("byte",new ProtocolCodecFilter(new MessageCodecFactory()));
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (writer != null && writer.isConnected()){
					return;
				}
				logger.info("try to connect " + CONNECT_URL + ":" + CONNECT_PORT);
				SocketAddress address = new InetSocketAddress(CONNECT_URL,CONNECT_PORT);
				ConnectFuture future = connector.connect(address);
				future.addListener(new IoFutureListener<IoFuture>() {
					@Override
					public void operationComplete(IoFuture arg0) {
						if (arg0.isDone()){
							writer = arg0.getSession();
						}
					}
				});
			}
		},0L,10,TimeUnit.SECONDS);
	}
	
	public boolean send(Request request,ResultCallBack callBack){
		if (writer == null || !writer.isConnected()){
			logger.info("send error because the connect is out of line!");
			return false;
		}
		if (callBack != null){
			callBacks.put(request.getLogicName(),callBack);
		}
		writer.write(request);
		return true;
	}
	
	public ResultCallBack getResultCallBack(String logicName){
		if (callBacks.containsKey(logicName)){
			return callBacks.get(logicName);
		}
		return null;
	}
}
