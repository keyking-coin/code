package com.joymeng.slg.net;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.annotation.JoyMessageHandler;
import com.joymeng.services.core.annotation.JoyMessageService;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.context.ServicesContext;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.services.core.service.AbstractJoyService;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.request.CommunicateRequest;

@JoyMessageService
public class CommunicateService extends AbstractJoyService {
	
	@JoyMessageHandler
	public JoyProtocol handler(CommunicateRequest request, ServicesContext context) {
		return _handler(request.getCommunicateId(),request.getUserInfo(),request.getBuff());
	}
	
	public JoyProtocol _handler(int protocolId,UserInfo info,JoyBuffer in){
		JoyProtocol resp = null;
		try {
			if (!ServiceApp.FREEZE){
				ServiceHandler handler = ServiceHandler.REQUEST_HANDLERS.get(protocolId);
				if (handler != null){
					Object locker = handler.getLock(info.getUid());
					synchronized (locker) {
						ParametersEntity params = handler.deserialize(in);
						resp = handler.handle(info,params);
					}
				}
			}else {
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_SERVICE_NOT_START);
			}
  		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
}
