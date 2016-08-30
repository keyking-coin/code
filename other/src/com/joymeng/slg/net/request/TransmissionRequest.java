package com.joymeng.slg.net.request;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyRequest;

@SuppressWarnings("serial")
public class TransmissionRequest extends JoyRequest {
	
	int communicateId;
	
	public TransmissionRequest() {
		super(9999);
	}

	public JoyBuffer buff;
	
	@Override
	protected void _deserialize(JoyBuffer in) {
		in.get();
		communicateId = in.getInt();
		buff = in;
	}

	@Override
	protected void _serialize(JoyBuffer out) {
		
	}

	public JoyBuffer getBuff() {
		return buff;
	}

	public int getCommunicateId() {
		return communicateId;
	}
}
