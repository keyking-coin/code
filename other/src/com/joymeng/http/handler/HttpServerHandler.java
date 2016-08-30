package com.joymeng.http.handler;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.joymeng.Instances;
import com.joymeng.http.handler.impl.HttpFile;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.http.response.HttpResponseMessage;
import com.joymeng.log.GameLog;

public class HttpServerHandler extends IoHandlerAdapter implements Instances {
	@SuppressWarnings("unchecked")
	@Override  
    public void messageReceived(IoSession session, Object message) {  
        HttpRequestMessage request = (HttpRequestMessage) message;
        String logicName = request.getContext();
        Class<? extends HttpHandler> clazz = null;
        HttpResponseMessage response = new HttpResponseMessage();
        HttpHandler handler = null;
		try {
			if (logicName.contains(".js") || logicName.contains(".jsp") ||
				logicName.contains(".jpg") || logicName.contains(".png")){
				handler = new HttpFile(logicName);
			}else{
				String base = getClass().getPackage().getName() + ".impl.";
				logicName = logicName.replaceAll("/",".");
				String className = base + logicName;
				clazz = (Class<? extends HttpHandler>)Class.forName(className);
				handler = clazz.newInstance();
			}
			handler.handle(request,response);
		}catch(Exception e){
			if (!(e instanceof ClassNotFoundException)){
				GameLog.error("http handler error",e);
			}
		}
		session.write(response);
    }
}
