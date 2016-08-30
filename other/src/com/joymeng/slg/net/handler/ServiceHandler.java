package com.joymeng.slg.net.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.net.resp.TransmissionResp;

public abstract class ServiceHandler implements Instances {
	
	protected int protocolId;//指令编号
	public static final HashMap<Integer,ServiceHandler> REQUEST_HANDLERS = new HashMap<Integer,ServiceHandler>();
	protected Map<Long,List<NeedContinueDoSomthing>> nextDos = new HashMap<Long,List<NeedContinueDoSomthing>>();
	Map<Long,Object> locks = new HashMap<Long,Object>();
	
	public CommunicateResp newResp(UserInfo info) {
		CommunicateResp resp = new CommunicateResp(protocolId);
		resp.setUserInfo(info);
		return resp;
	}
	
	public TransmissionResp newTransmissionResp(UserInfo info) {
		TransmissionResp resp = new TransmissionResp();
		resp.setUserInfo(info);
		return resp;
	}
	
	public void register(int protocolId) {
		this.protocolId = protocolId;
		if (REQUEST_HANDLERS.containsKey(protocolId)){
			GameLog.error(new Exception(REQUEST_HANDLERS.get(protocolId) + "  had  registed by " + protocolId));
		}
		REQUEST_HANDLERS.put(protocolId,this);
	}
	
	@SuppressWarnings("unchecked")
	public static final void registerHandlers() {
		try {
			Document document = XmlUtils.load(Const.CONF_PATH + "ServiceHandlers.xml");
			Element element = document.getDocumentElement();
			Element[] elements = XmlUtils.getChildrenByName(element,"ServiceHandler");
			for (int i = 0; i < elements.length ; ++i) {
				String code        = XmlUtils.getAttribute(elements[i],"orderCode");
				String className   = XmlUtils.getAttribute(elements[i],"class");
				int codeNum = Integer.parseInt(code.toLowerCase().replaceFirst("0x",""),16);
				Class<? extends ServiceHandler> clazz = (Class< ? extends ServiceHandler>)Class.forName(className);
				ServiceHandler handler = clazz.newInstance();
				GameLog.info("regist " + className + ".class code = " + codeNum);
				handler.register(codeNum);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ParametersEntity deserialize(JoyBuffer in){
		ParametersEntity params = new ParametersEntity();
		_deserialize(in,params);
		return params;
	}
	
	/**
	 * 获取在线玩家
	 * @param info
	 * @return
	 */
	public Role getRole(UserInfo info){
		//Role role = world.getRole(info.getUid());
		//if (role == null || role.isRemoving()){
		//	MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_ROLE_NOT_FIND,info);
		//}
		return world.getRole(info.getUid());
	}

	public void addNextDo(long uid,NeedContinueDoSomthing nextDo) {
		synchronized (nextDos) {
			List<NeedContinueDoSomthing> nexts = nextDos.get(uid);
			if (nexts == null){
				nexts = new ArrayList<NeedContinueDoSomthing>();
				nextDos.put(uid, nexts);
			}
			nexts.add(nextDo);
		}
	}
	
	public void removeNextDo(long uid,NeedContinueDoSomthing nextDo) {
		synchronized (nextDos) {
			List<NeedContinueDoSomthing> nexts = nextDos.get(uid);
			if (nexts != null){
				nexts.remove(nextDo);
			}
		}
	}
	
	public NeedContinueDoSomthing search(long uid,int id) {
		synchronized (nextDos) {
			List<NeedContinueDoSomthing> nexts = nextDos.get(uid);
			if (nexts != null){
				for (int i = 0 ; i < nexts.size() ; i++){
					NeedContinueDoSomthing next = nexts.get(i);
					if (next.getId() == id){
						return next;
					}
				}
			}
			return null;
		}
	}
	
	public Object getLock(Long uid){
		Object obj = locks.get(uid);
		if (obj == null){
			obj = new Object();
			locks.put(uid,obj);
		}
		return obj;
	}
	
	public abstract void _deserialize(JoyBuffer in,ParametersEntity params);
	
	public abstract JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception;
}
