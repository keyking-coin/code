package com.keyking.http.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.keyking.http.request.HttpRequestMessage;
import com.keyking.http.response.HttpResponseMessage;
import com.keyking.util.Instances;
import com.keyking.util.ServerLog;

public class HttpServerHandler extends IoHandlerAdapter implements Instances {
	
	Map<String,HttpHandler> handlers = new ConcurrentHashMap<String,HttpHandler>();
	
    @Override  
    public void sessionOpened(IoSession session) {  
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);  
    }  
  
	@Override  
    public void messageReceived(IoSession session, Object message) {  
        HttpRequestMessage request = (HttpRequestMessage) message;
        String logicName = request.getContext();
		try {
			HttpHandler handler = handlers.get(logicName);
			if (handler == null){
				String base = getClass().getPackage().getName();
				String path = logicName.replaceAll("/",".");
				Class<?> clazz = Class.forName(base + ".impl." + path);
				handler = (HttpHandler)clazz.newInstance();
				handlers.put(logicName,handler);
			}
			synchronized (handler) {
				HttpResponseMessage response = new HttpResponseMessage();
				handler.handle(request,response);
				session.write(response).addListener(IoFutureListener.CLOSE);
			}
		}catch(Exception e){
			ServerLog.error("http handler error",e);
		}
    }  
}
