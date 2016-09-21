package com.joymeng.slg.net.request;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyModuleMessage;
import com.joymeng.slg.net.ParametersEntity;

public class ServerInnerRequest extends JoyModuleMessage {
	
	JoyBuffer buff;
	
	int communicateId;
	
	ParametersEntity params = new ParametersEntity();
	
	public ServerInnerRequest() {
		super(111);
	}
	
	private static final long serialVersionUID = 1L;


	@Override
	protected void _deserialize(JoyBuffer in) {
		
	}

	@Override
	protected void _serialize(JoyBuffer out) {
		
	}

	public JoyBuffer getBuff() {
		return buff;
	}

	public void setBuff(JoyBuffer buff) {
		this.buff = buff;
	}

	public int getCommunicateId() {
		return communicateId;
	}

	public void setCommunicateId(int communicateId) {
		this.communicateId = communicateId;
	}
	
	
	
}
