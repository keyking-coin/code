package com.joymeng.gm2.net;


import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joymeng.gm2.GMServer;
import com.joymeng.gm2.net.handler.IProtocolHandler;
import com.joymeng.gm2.net.message.AbstractGMProtocol;
import com.joymeng.services.core.exception.JoyException;

/**
 * 与消息路由服务器交互的监听处理
 * @author ShaoLong Wang
 */
class SocketListener extends IoHandlerAdapter
{
	static Logger logger = LoggerFactory.getLogger(SocketListener.class);
	
	
	public SocketListener()
	{

	}
	
	/**
	 * 与消息路由服务器创建连接时，初始化一些全局变量
	 */
	@Override 
	public void sessionCreated(IoSession session) throws Exception 
	{
		
//		InetSocketAddress adr = (InetSocketAddress)session.getRemoteAddress();

		
		logger.info("*****************************************************************");
		logger.info("gm:" + (InetSocketAddress)session.getRemoteAddress() + " open[sessionId:" + session.getId() + "]");
		logger.info("*****************************************************************");
		GMServer.getInstance().getNetMgr().addSession(session);
		
	}
	
	
	/**
	 * 与消息路由服务器创建连接关闭时，尝试重联
	 */
	@Override 
	public void sessionClosed(IoSession session) throws Exception 
	{
		logger.info("*****************************************************************");
		logger.info("gm:" + (InetSocketAddress)session.getRemoteAddress() + " closed[sessionId:" + session.getId() + "]");
		logger.info("*****************************************************************");
		GMServer.getInstance().getNetMgr().removeSession(session);
    }

	/**
	 * 接收到消息路由服务器的信息时，对消息进行纷发，路由到本地处理的每一个Service上
	 */
	@Override
	public final void messageReceived(IoSession session, Object message)
	{
		try
		{
			NetManager netMgr =  GMServer.getInstance().getNetMgr();
			AbstractGMProtocol protocol = (AbstractGMProtocol)message;
			IProtocolHandler handler = netMgr.getProtocolHandler(protocol.getMessageID());
			if(handler == null)
			{	
				throw new JoyException("unknown message handler:" + protocol.getMessageID());
			}
			
			handler.handle(session, protocol);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}


    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception 
    {
    	
    	cause.printStackTrace();
    }
}
