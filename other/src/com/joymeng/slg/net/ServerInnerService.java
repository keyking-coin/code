package com.joymeng.slg.net;

import com.joymeng.services.core.annotation.JoyMessageHandler;
import com.joymeng.services.core.annotation.JoyMessageService;
import com.joymeng.services.core.context.ServicesContext;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.services.core.service.AbstractJoyService;
import com.joymeng.slg.net.request.ServerInnerRequest;

@JoyMessageService
public class ServerInnerService extends AbstractJoyService {
	
	@JoyMessageHandler
	public JoyProtocol handler(ServerInnerRequest request, ServicesContext context) {
		
		return null;
	}
	
}
