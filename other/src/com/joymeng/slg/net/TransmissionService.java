package com.joymeng.slg.net;

import com.joymeng.services.core.annotation.JoyMessageHandler;
import com.joymeng.services.core.annotation.JoyMessageService;
import com.joymeng.services.core.context.ServicesContext;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.net.request.TransmissionRequest;
@JoyMessageService
public class TransmissionService extends CommunicateService{
	
	@JoyMessageHandler
	public JoyProtocol handleComm(TransmissionRequest request, ServicesContext context) {
		return _handler(request.getCommunicateId(),request.getUserInfo(),request.getBuff());
	}
}
