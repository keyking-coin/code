package com.keyking.coin.service.http.handler;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.ServerLog;

public class HttpServerHandler extends IoHandlerAdapter implements Instances {
	
	static final String HTTP_BASE_LOGIC_PACKAGE = "com.keyking.coin.service.http.handler.impl";
	
    @Override  
    public void sessionOpened(IoSession session) {  
        // set idle time to 60 seconds  
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);  
    }  
  
    @SuppressWarnings("unchecked")
	@Override  
    public void messageReceived(IoSession session, Object message) {  
        HttpRequestMessage request = (HttpRequestMessage) message;
        String logicName = request.getContext();
        Class<? extends HttpHandler> clazz = null;
        HttpResponseMessage response = new HttpResponseMessage();
		try {
			clazz = (Class<? extends HttpHandler>)Class.forName(HTTP_BASE_LOGIC_PACKAGE + "." + logicName);
			HttpHandler handler = clazz.newInstance();
			handler.handle(request,response); 
		}catch(Exception e){
			ServerLog.error("http handler error",e);
		}
		session.write(response).addListener(IoFutureListener.CLOSE);
    }  
  
    @Override  
    public void sessionIdle(IoSession session, IdleStatus status) {  
        session.close(false);  
    }  
  
    @Override  
    public void exceptionCaught(IoSession session, Throwable cause) {  
        session.close(false);  
    }  
}
