package com.joymeng.gm2.net.handler;

import org.apache.mina.core.session.IoSession;

import com.joymeng.gm2.net.message.request.TestRequest;
import com.joymeng.gm2.net.message.response.TestResponse;

/**
 * 协议处理器
 * @author ShaoLong Wang
 *
 */
public class TestHandler extends AbstractProtocolHandler<TestRequest>{

	@Override
	protected void _handle(IoSession session, TestRequest request) {
		
		System.out.println("receive:" + request.getText());
		
		TestResponse response = new TestResponse();
		response.setText("world!");
		
		session.write(response);
//
//		GMServer.getInstance().send(response);
	}

}
