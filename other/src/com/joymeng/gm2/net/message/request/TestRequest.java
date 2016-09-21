package com.joymeng.gm2.net.message.request;

import com.joymeng.gm2.net.ProtocolList;
import com.joymeng.gm2.net.message.GMRequest;
import com.joymeng.services.core.buffer.JoyBuffer;

/**
 * GM测试协议
 * @author wangshaolong
 *
 */
public class TestRequest extends GMRequest{

	private String text;
	
	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public TestRequest() {
		super(ProtocolList.REQ_TEST);
	}


	@Override
	protected void _deserialize(JoyBuffer in) {
		this.text = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		
	}

}
