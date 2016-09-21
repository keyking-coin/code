package com.joymeng.gm2.net;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joymeng.gm2.GMServer;
import com.joymeng.gm2.net.codec.DefaultGMCodecFactory;
import com.joymeng.gm2.net.handler.IProtocolHandler;
import com.joymeng.gm2.net.handler.MessageHandler;
import com.joymeng.gm2.net.handler.TestHandler;
import com.joymeng.gm2.net.message.AbstractGMProtocol;
import com.joymeng.gm2.net.message.GMResponse;
import com.joymeng.gm2.net.message.request.MessageRequest;
import com.joymeng.gm2.net.message.request.TestRequest;
import com.joymeng.gm2.net.message.response.MessageResponse;
import com.joymeng.gm2.net.message.response.TestResponse;



/**
 * @author ShaoLong Wang
 * 
 */
public class NetManager implements Runnable
{
	public static final Logger logger = LoggerFactory.getLogger(NetManager.class);
	
	public static int CONN_PERIOD = 10000;
	
//	private IoSession session;
	
	private List<IoSession> sessions = new CopyOnWriteArrayList<>();
	

	public void addSession(IoSession session)
	{
		sessions.add(session);
	}
	
	public void removeSession(IoSession session)
	{
		sessions.remove(session);
	}
	
	/**
	 * 广播消息
	 * @param message
	 */
	public void boardcast(GMResponse message)
	{
		for(IoSession session:sessions) 
		{
			session.write(message);
		}
	
	}
	

	public static ExecutorService configureExecutorService()
	{
		String str1 = System.getProperty("core.thread.num");
		if (str1 == null)
		      return Executors.newCachedThreadPool();
		    
		int i = Integer.parseInt(str1);
		int j = i;
		String str2 = System.getProperty("max.thread.num");
		if (str2 != null)
		      j = Integer.parseInt(str2);
		if (j <= i)
		      return Executors.newFixedThreadPool(i);
		String str3 = System.getProperty("task.queue.size");
		if (str3 == null)
		   return new ThreadPoolExecutor(i, j, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(100));
		int k = Integer.parseInt(str3);
		    
		return new ThreadPoolExecutor(i, j, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(k));
	}

	NioSocketAcceptor acceptor;
	
	
	public void start()throws Exception
	{
		SocketListener listener = new SocketListener();	
		
		acceptor = new NioSocketAcceptor();

		
//		connector.setConnectTimeoutMillis(CONNECTOR_TIME_OUT);
	
    	acceptor.setHandler(listener);
    
    	acceptor.getFilterChain().addLast("byte", 
    			new ProtocolCodecFilter(new DefaultGMCodecFactory(gmServer)));
    
    	acceptor.getFilterChain().addLast("threadPool", 
				new ExecutorFilter(configureExecutorService()));
    	
    	acceptor.bind(new InetSocketAddress(gmServer.getPort()));
    	logger.info("listening port:" + gmServer.getPort());
    	

	}
	GMServer gmServer;
	
	public NetManager(GMServer gmServer)
	{
		this.gmServer = gmServer;
		regist();
	}
	

	public void regist()
	{
		
		registProtocol(ProtocolList.REQ_TEST, TestRequest.class, new TestHandler());
		registProtocol(ProtocolList.RESP_TEST, TestResponse.class, null);
		registProtocol(ProtocolList.REQ_MESSAGE,MessageRequest.class,new MessageHandler());
		registProtocol(ProtocolList.RESP_MESSAGE,MessageResponse.class,null);
		
	}
	
	private Map<Integer, Class<? extends AbstractGMProtocol>> protocolClazzs = new ConcurrentHashMap<Integer, Class<? extends AbstractGMProtocol>>();

	private Map<Integer, IProtocolHandler> protocolHandlers = new ConcurrentHashMap<Integer, IProtocolHandler>();

	public  AbstractGMProtocol getProtocol(int messageId) {
		Class<? extends AbstractGMProtocol> clazz = protocolClazzs
				.get(messageId);
		if (clazz == null) {
			return null;
		}

		try {
			AbstractGMProtocol protocol = (AbstractGMProtocol) clazz
					.newInstance();
			return protocol;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public IProtocolHandler getProtocolHandler(
			int messageId) {
		return protocolHandlers.get(messageId);
	}

	public void registProtocol(int messageId,
			Class<? extends AbstractGMProtocol> clazz,
			IProtocolHandler handler) {
		protocolClazzs.put(messageId, clazz);
		if (handler != null) {
			protocolHandlers.put(messageId, handler);
		}
	}


	
	
	public static final int LOGIN_TIME_OUT = 10; // 注锟斤拷锟斤拷锟�0s锟斤拷时
	
	@Override
	public void run()
	{
//		while (true)
//		{
//			try
//			{
//				
//				RemoteServices remoteServices = context.getRemoteServices();
//				Collection<RemoteService> services = remoteServices.listServicesUnlogined();
//				logger.info("unlogined services num:" + services.size());
//				for(RemoteService service:services)
//				{
//					login(context, service);
//				}
//
//	
//				Thread.sleep(CONN_PERIOD);
//				
//				services = remoteServices.listServicesUnlogined();
//				if(services.size() <= 0)
//				{
//					synchronized (lock)
//					{
//						lock.wait();
//					}
//					
//				}
//				
//
//				
//			}
//			catch (InterruptedException e)
//			{
//				e.printStackTrace();
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}

	}


}