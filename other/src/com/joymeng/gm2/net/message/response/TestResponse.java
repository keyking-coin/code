package com.joymeng.gm2.net.message.response;

import com.joymeng.gm2.net.ProtocolList;
import com.joymeng.gm2.net.message.GMResponse;
import com.joymeng.services.core.buffer.JoyBuffer;

public class TestResponse extends GMResponse {
	
	private String text;

	public TestResponse() {
		super(ProtocolList.RESP_TEST);
	}


	@Override
	protected void _serialize(JoyBuffer out) {
		
		out.putPrefixedString(text, JoyBuffer.STRING_TYPE_SHORT);
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}
	
	

}
