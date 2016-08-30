package com.joymeng.slg.net.handler;

import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.net.ParametersEntity;


public interface NeedContinueDoSomthing {
	public int getId();
	public JoyProtocol succeed(UserInfo info,ParametersEntity params);
	public JoyProtocol fail(UserInfo info,ParametersEntity params);
}
