package com.joymeng.slg.net.request;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyRequest;

@SuppressWarnings("serial")
public class CommunicateRequest extends JoyRequest {
	
	JoyBuffer buff;
	
	int communicateId;
	
	public CommunicateRequest() {
		super(123456);
	}

	@Override
	protected void _deserialize(JoyBuffer joybuffer) {
		communicateId = joybuffer.getInt();
		buff = joybuffer;
	}

	@Override
	protected void _serialize(JoyBuffer joybuffer) {
		
	}

	public int getCommunicateId() {
		return communicateId;
	}

	public JoyBuffer getBuff() {
		return buff;
	}
}
