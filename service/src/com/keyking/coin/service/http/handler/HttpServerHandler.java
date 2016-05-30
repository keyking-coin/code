package com.keyking.coin.service.http.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	Map<String,HttpHandler> handlers = new ConcurrentHashMap<String,HttpHandler>();
	
    @Override  
    public void sessionOpened(IoSession session) {  
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);  
    }  
  
	@Override  
    public void messageReceived(IoSession session, Object message) {  
        HttpRequestMessage request = (HttpRequestMessage) message;
        String logicName = request.getContext();
        String base = getClass().getPackage().getName();
		String path = logicName.replaceAll("/",".");
        HttpHandler handler = handlers.get(path);
        HttpResponseMessage response = new HttpResponseMessage();
        try {
	        if (handler == null){
	        	String allPath = null;
	        	if (path.contains("um.")){
	        		allPath = base + ".um." + path;
	        	}else{
	        		allPath = base + ".impl." + path;
	        	}
	        	Class<?> clazz = Class.forName(allPath);
				handler = (HttpHandler)clazz.newInstance();
				handlers.put(logicName,handler);
	        }
        	synchronized (handler) {
     			handler.handle(request,response);
     		}
        }catch(Exception e){
			if (!(e instanceof ClassNotFoundException)){
				ServerLog.error("http handler error",e);
			}
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
