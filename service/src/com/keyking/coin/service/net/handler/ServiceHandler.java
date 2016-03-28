package com.keyking.coin.service.net.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
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
				if (logicName.contains("Admin")){
					clazz = Class.forName(LOGIC_PACKAGE_NAME_ADMIN + logicName);
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
				e.printStackTrace();
				ServerLog.error("Logic",e);
			}
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		String key = session.getRemoteAddress().toString();
		ServerLog.info(key + " open connect");
		sessions.put(key,session);
		super.sessionCreated(session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		String key = session.getRemoteAddress().toString();
		sessions.remove(key);
		CTRL.haveUserOutNet(session);
		if (adminSession != null && session.equals(adminSession)){
			adminSession = null;
		}
		super.sessionClosed(session);
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
	
	
	public void sendMessageToClent(ModuleResp resp,UserCharacter user){
		sendMessageToClent(resp,user.getSessionAddress());
	}
	
	public void sendMessageToClent(ModuleResp resp,String key){
		if (resp == null || !resp.send()){
			return;
		}
		if (key != null && sessions.containsKey(key)){
			IoSession session = sessions.get(key);
			if (session.getAttribute("isAdmin").toString().equals("true")){
				return;
			}
			ServerLog.info("send module to " + key);
			session.write(resp);
		}
	}
	
	public void sendMessageToAllClent(ModuleResp resp , String excepte){
		if (resp == null || !resp.send()){
			return;
		}
		for (String key : sessions.keySet()){
			if (excepte == null || !key.equals(excepte)){
				sendMessageToClent(resp,key);
			}
		}
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
 
 
 
