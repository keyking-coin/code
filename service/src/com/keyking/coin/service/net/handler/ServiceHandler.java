package com.keyking.coin.service.net.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.Logic;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.service.net.resp.sys.RespTip;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.ServerLog;

public class ServiceHandler extends IoHandlerAdapter implements Instances {
	
	private static final String LOGIC_PACKAGE_NAME_USER = "com.keyking.coin.service.net.logic.user.";
	
	private static final String LOGIC_PACKAGE_NAME_ADMIN = "com.keyking.coin.service.net.logic.admin.";
	
	private static final String LOGIC_PACKAGE_NAME_APP = "com.keyking.coin.service.net.logic.app.";
	
	private static ServiceHandler instance = new ServiceHandler();
	
	Map<String,IoSession> sessions = new ConcurrentHashMap<String,IoSession>();
	
	IoSession adminSession = null;
	
	public static ServiceHandler getInstance(){
		return instance;
	}
	
	@Override
	public void messageReceived(IoSession session , Object message) throws Exception {
		if (message instanceof DataBuffer){
			DataBuffer data = (DataBuffer)message;
			String logicName = data.getUTF();
			Class<?> clazz = null;
			try {
				ServerLog.info("do function ---->" + logicName);
				if (logicName.startsWith("Admin")){
					clazz = Class.forName(LOGIC_PACKAGE_NAME_ADMIN + logicName);
				}else if (logicName.startsWith("App")){
					clazz = Class.forName(LOGIC_PACKAGE_NAME_APP + logicName);
				}else{
					clazz = Class.forName(LOGIC_PACKAGE_NAME_USER + logicName);
				}
				Object obj = clazz.newInstance();
				if (obj instanceof Logic){
					Logic logic = (Logic)obj;
					logic.setSession(session);
					Object resp = logic.doLogic(data,logicName);
					if (resp != null){
						session.write(resp);
					}
				}
			} catch (Exception e) {
				ServerLog.error("请求处理异常",e);
			}
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		String key = session.getRemoteAddress().toString();
		ServerLog.info(key + " open connect ");
		sessions.put(key,session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		String key = session.getRemoteAddress().toString();
		sessions.remove(key);
		CTRL.haveUserOutNet(session);
		if (adminSession != null && session.equals(adminSession)){
			adminSession = null;
		}
		session.close(true);
		ServerLog.info(key + " close connect");
	}
	
	@Override
	public void sessionIdle(IoSession iosession, IdleStatus idlestatus) throws Exception {
		super.sessionIdle(iosession, idlestatus);
	}

	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		super.exceptionCaught(session, cause);
		session.close(true);
	}

	public IoSession getAdminSession() {
		return adminSession;
	}

	public void setAdminSession(IoSession adminSession) {
		this.adminSession = adminSession;
	}

	public IoSession search(String key){
		if (key == null){
			return null;
		}
		return sessions.get(key);
	}
	
	public void sendMessageToAdmin(ModuleResp resp){
		if (adminSession == null){
			return ;
		}
		adminSession.write(resp);
	}
	
	public void sendTip(String tip,UserCharacter user){
		IoSession session = NET.search(user.getSessionAddress());
		if (session == null){
			return;
		}
		RespTip resp = new RespTip(tip);
		session.write(resp);
	}
}
 
 
 
