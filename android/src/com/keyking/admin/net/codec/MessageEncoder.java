package com.keyking.admin.net.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.exp.TransformDataException;
import com.keyking.admin.net.request.Request;

public class MessageEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession arg0, Object obj, ProtocolEncoderOutput out)
			throws Exception {
		if (!(obj instanceof Request)){
			throw new TransformDataException(obj);
		}
		Request request = (Request)obj;
		DataBuffer buffer = DataBuffer.allocate(1024);
		request.serialize(buffer);
		byte[] datas = buffer.arrayToPosition();
		out.write(IoBuffer.wrap(datas));
	}
}
