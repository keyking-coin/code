package com.keyking.coin.service.http.handler;

import java.io.FileInputStream;
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
        HttpResponseMessage response = new HttpResponseMessage();
        try {
        	if (logicName.endsWith(".js") || logicName.endsWith(".html") || logicName.endsWith(".png") || 
        		logicName.endsWith(".jpg") || logicName.endsWith(".css") || logicName.endsWith(".bmp") ||
        		logicName.endsWith(".gif") || logicName.endsWith(".ico")){
        		loadFile("./web/" + logicName,response);
        	}else {
        		String allPath = null;
        		String path = logicName.replaceAll("/",".");
        		HttpHandler handler = handlers.get(path);
        		String base = getClass().getPackage().getName();
        		if (path.contains("um.") || path.contains("gm.")){
        			allPath = base + "." + path;
	        	}else{
	        		allPath = base + ".impl." + path;
	        	}
        		if (handler == null){
        			Class<?> clazz = Class.forName(allPath);
    				handler = (HttpHandler)clazz.newInstance();
    				handlers.put(logicName,handler);
        		}
				synchronized (handler) {
	     			handler.handle(request,response);
	     			response.tranform();
	     		}
        	}
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
    
    public void loadFile(String filePath,HttpResponseMessage response){
    	try {
			FileInputStream fis = new FileInputStream(filePath);
			byte[] buffer = new byte[1024];
			do{
				int len = fis.read(buffer);
				if (len == -1){
					break;
				}
				response.appendBody(buffer,0,len);
			}while(true);
			//response.setContentType("text/plain");
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
