package com.joymeng.gm2.net.message;

import com.joymeng.services.core.buffer.JoyBuffer;


/**
 * 添加result概念，
 * 以及实现默认串行化接口
 * @author ShaoLong Wang
 */
public abstract class GMResponse extends AbstractGMProtocol
{
	private static final long serialVersionUID = 9107231389173088443L;
	
	public static final byte JOY_RESP_SUCC = 0;
	public static final byte JOY_RESP_FAIL = 1;
	protected int instanceId;
	protected byte result;

	public GMResponse(int commandID)
	{
		super(commandID);
	}
	public GMResponse(int commandID, byte result)
	{
		super(commandID);
		this.result = result;
	}
	
	public byte getResult()
	{
		return result;
	}

	public void setResult(byte result)
	{
		this.result = result;
	}
	
	public final void _serializeInner(JoyBuffer out)
	{
		out.put(result);
	}
	
	public final void _deserializeInner(JoyBuffer in)
	{
		result = in.get();
	}
	

	@Override
	protected final void _deserialize(JoyBuffer in) {
		

	}

}
