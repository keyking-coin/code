package com.joymeng.gm2.net;

import com.joymeng.gm2.GMServer;
import com.joymeng.gm2.net.message.request.TestRequest;
import com.joymeng.gm2.net.message.response.TestResponse;

public class ProtocolList {
	
	public static int REQ_TEST = 0x0000;
	public static int RESP_TEST = 0x0001;
	
	
	//聊天协议
	public static int CHAT_ROOT = 0x00000900;

    public static int REQ_MESSAGE = CHAT_ROOT | 0x05;
    public static int RESP_MESSAGE = CHAT_ROOT | 0x06;


}
